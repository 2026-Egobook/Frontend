package com.egobook.app.ui.home.ui

import android.Manifest
import android.graphics.Color
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import com.egobook.app.BlurLevel
import com.egobook.app.NotificationController
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.FragmentHomeBinding
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.notice.NoticeOpenResult
import com.egobook.app.ui.home.HomeViewModel
import com.egobook.app.ui.home.NotificationRedDotViewModel
import com.egobook.app.ui.home.user.LevelType
import com.egobook.app.store.ui.CustomItem
import com.egobook.app.store.ui.ItemImage
import com.egobook.app.store.data.model.ItemType
import com.egobook.app.data.local.PushPreferenceStorage
import com.egobook.app.domain.repository.push.PushTokenRepository
import com.egobook.app.push.NotificationPermissionDecision
import com.egobook.app.push.NotificationPermissionPolicy
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.time.LocalDate

@AndroidEntryPoint
class HomeFragment(): Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val redDotViewModel: NotificationRedDotViewModel by activityViewModels()

    @Inject
    lateinit var pushPreferenceStorage: PushPreferenceStorage

    @Inject
    lateinit var pushTokenRepository: PushTokenRepository

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Timber.d("알림 권한 요청 결과: $isGranted")
        }

    private var turtleBubbleJob: Job? = null
    private var lastTurtleMessageIndex = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: HomeViewModel by activityViewModels()

        viewModel.fetchUser()
        viewModel.fetchEquipItems()

        setUpPushNotification()
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.flLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }
                
                launch {
                    redDotViewModel.redDotState.collect { redDotState ->
                        binding.vBellRedDot.isVisible = redDotState.isVisible
                    }
                }

                launch {
                    viewModel.hasUnreadNotice.collect { hasUnreadNotice ->
                        binding.vNoticeRedDot.isVisible = hasUnreadNotice
                    }
                }

                launch {
                    viewModel.noticeOpenResult.collect { result -> showNotice(result) }
                }

                launch {
                    viewModel.recallDiary.collect { diary ->
                        if (diary != null) showRecallDiary(diary, viewModel)
                    }
                }

                launch {
                    viewModel.canWriteTodayDiary.collect { canWrite ->
                        if (canWrite) navigateToTodayDiaryWrite() else showDailyLimitToast()
                    }
                }

                launch {
                    viewModel.uiState.collect { userState ->
                        binding.tvLevel.text = "Lv ${userState.level.number}"
                        binding.ivLevelType.setImageResource(userState.level.type.getResId())
                        binding.tvInk.text = "${userState.ink.value}"
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.equippedItems.collect { equippedList ->
                    val typeToItem = equippedList.associateBy { it.type }
                    ItemType.entries.forEach { type ->
                        updateEquipItemUi(type, typeToItem[type])
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dailyPhycologyReadState.collect { phycologyReadState ->
                if(phycologyReadState) {
                    binding.ivDailyBottle.visibility = View.VISIBLE
                } else {
                    binding.ivDailyBottle.visibility = View.INVISIBLE
                }
            }
        }

        binding.ivStore.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_storeFragment)
        }

        binding.ivDailyBottle.setOnClickListener {
            applyScreenBlur(BlurLevel.BASE)
            val dialog = PsychologyDialog()
            dialog.isCancelable = false
            dialog.show(parentFragmentManager, "DailyPsychologyDialog")
            binding.ivDailyBottle.visibility = View.INVISIBLE
            viewModel.logPsychKnowledgeOpen()

            parentFragmentManager.setFragmentResultListener("psychology_key", viewLifecycleOwner) { _, _ ->
                Timber.d("다이얼로그 닫힘 감지 - 데이터 갱신")
                viewModel.fetchUser()
                viewModel.fetchDailyPhycologyReadState()
            }
        }

        binding.ivBottle.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_psychologyFragment)
        }

        binding.ivAd.setOnClickListener {
            val userId = viewModel.uiState.value.id
            if (userId <= 0) {
                Toast.makeText(requireContext(), "사용자 정보를 불러오는 중입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            applyScreenBlur(BlurLevel.BASE)
            val dialog = AdDialog.newInstance(userId.toString())
            dialog.isCancelable = false
            dialog.show(parentFragmentManager, "AdDialog")
        }
        binding.ivBell.setOnClickListener {
            redDotViewModel.dismiss()
            val notificationController =
                checkNotNull(activity as? NotificationController) { "해당 액티비티는 notification controller를 구현하지 않았습니다" }
            notificationController.openDrawer()
        }

        binding.ivSetting.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_accountFragment)

        }

        binding.ivRadar.setOnClickListener {
            applyScreenBlur(BlurLevel.BASE)
            val dialog = RadarDialog()
            dialog.isCancelable = false
            dialog.show(parentFragmentManager, "RadarDialog")
        }

        binding.ivCalendar.setOnClickListener {
            applyScreenBlur(BlurLevel.BASE)
            val dialog = StreakDialog()
            dialog.isCancelable = false
            dialog.show(parentFragmentManager, "SteakDialog")
        }

        binding.ivNotice.setOnClickListener { viewModel.openNotice() }

        parentFragmentManager.setFragmentResultListener(
            NoticeDialog.RESULT_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean(NoticeDialog.RESULT_LOAD_FAILED)) showNoticeErrorDialog()
        }

        parentFragmentManager.setFragmentResultListener(
            RecallDiaryDialog.RESULT_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            when (bundle.getString(RecallDiaryDialog.RESULT_ACTION)) {
                RecallDiaryDialog.ACTION_VIEW -> navigateToDiaryDetail(bundle.getLong(RecallDiaryDialog.RESULT_DIARY_ID))
                RecallDiaryDialog.ACTION_WRITE -> viewModel.checkCanWriteTodayDiary()
            }
        }

        binding.vTurtleTouchArea.setOnClickListener { showTurtleBubble() }
    }

    override fun onResume() {
        super.onResume()
        redDotViewModel.refresh()
    }

    /**
     * 공지 조회 결과에 따라 공지 웹뷰 또는 실패 다이얼로그를 띄운다.
     *
     * 조회를 기다리는 동안 화면이 흐려지지 않도록, 블러는 다이얼로그를 띄우는 시점에 적용한다.
     */
    private fun showNotice(result: NoticeOpenResult) {
        applyScreenBlur(BlurLevel.BASE)
        when (result) {
            is NoticeOpenResult.Success -> {
                val dialog = NoticeDialog.newInstance(result.url)
                dialog.isCancelable = false
                dialog.show(parentFragmentManager, "NoticeDialog")
            }

            NoticeOpenResult.Failure -> showNoticeErrorDialog()
        }
    }

    /**
     * 과거의 나 돌아보기 팝업을 띄우고, 띄운 직후 서버에 노출을 확정한다.
     */
    private fun showRecallDiary(diary: Diary, viewModel: HomeViewModel) {
        if (parentFragmentManager.findFragmentByTag(RecallDiaryDialog.TAG) != null) return
        applyScreenBlur(BlurLevel.BASE)
        val dialog = RecallDiaryDialog.newInstance(diary)
        dialog.isCancelable = false
        dialog.show(parentFragmentManager, RecallDiaryDialog.TAG)
        viewModel.onRecallShown(diary.diaryId)
    }

    private fun navigateToDiaryDetail(diaryId: Long) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToDiaryCheckFragment(diaryId)
        )
    }

    private fun navigateToTodayDiaryWrite() {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToDiaryWriteFragment(
                selectedDate = LocalDate.now().toString()
            )
        )
    }

    /**
     * 오늘 작성 가능한 일기 수(48개)를 넘었을 때 안내 토스트를 띄운다. (DiaryFragment와 동일한 UI)
     */
    private fun showDailyLimitToast() {
        val snackBar = Snackbar.make(requireView(), "", Snackbar.LENGTH_LONG)
        val customView = layoutInflater.inflate(R.layout.toast_over_write, null)

        val layout = snackBar.view as ViewGroup
        layout.setPadding(0, 0, 0, 0)
        layout.setBackgroundColor(Color.TRANSPARENT)
        layout.addView(customView, 0)

        snackBar.anchorView = requireActivity().findViewById<View>(R.id.bottom_navigation)

        val extra = (9 * resources.displayMetrics.density).toInt()
        val params = snackBar.view.layoutParams as ViewGroup.MarginLayoutParams
        params.bottomMargin += extra
        snackBar.view.layoutParams = params

        snackBar.show()
    }

    private fun showTurtleBubble() {
        val messages = resources.getStringArray(R.array.turtle_messages)
        if (messages.isEmpty()) return

        // 직전 문구는 빼고 랜덤 (문구가 1개뿐이면 그거 그대로)
        val candidates = messages.indices
            .filter { it != lastTurtleMessageIndex }
            .ifEmpty { messages.indices.toList() }
        val index = candidates.random()
        lastTurtleMessageIndex = index

        binding.tvTurtleBubble.text = messages[index]
        binding.llTurtleBubble.isVisible = true

        // 이전 타이머 취소 후 다시 3초 → 마지막 클릭 기준 3초
        turtleBubbleJob?.cancel()
        turtleBubbleJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(TURTLE_BUBBLE_DURATION_MS)
            binding.llTurtleBubble.isVisible = false
        }
    }

    private fun showNoticeErrorDialog() {
        val dialog = NoticeErrorDialog()
        dialog.isCancelable = false
        dialog.show(parentFragmentManager, "NoticeErrorDialog")
    }

    private fun updateEquipItemUi(type: ItemType, item: CustomItem?) {
        val imagePath = (item?.outfitImage as? ItemImage.Url)?.path
        when (type) {
            ItemType.BACK -> binding.ivHomeTurtleBack.load(imagePath)
            ItemType.SKIN -> binding.ivHomeTurtleSkin.load(imagePath)
            ItemType.DECO_1 -> {
                if (imagePath?.contains("Default") == true) binding.ivHomeTurtleDeco1.load(null)
                else binding.ivHomeTurtleDeco1.load(imagePath)
            }
            ItemType.DECO_2 -> {
                if (imagePath?.contains("Default") == true) binding.ivHomeTurtleDeco2.load(null)
                else binding.ivHomeTurtleDeco2.load(imagePath)
            }
            ItemType.BACKGROUND -> binding.ivHomeBackground.load(imagePath)
            ItemType.LETTER_PAPER -> {}
        }
    }

    /**
     * 홈 진입 시 알림 권한을 요청하고, FCM 토큰을 서버에 등록한다.
     *
     * 권한을 거절해도 토큰 등록은 진행한다.
     * 사용자가 나중에 시스템 설정에서 알림을 켜면 바로 수신할 수 있어야 하기 때문이다.
     */
    private fun setUpPushNotification() {
        viewLifecycleOwner.lifecycleScope.launch {
            requestNotificationPermissionIfNeeded()
            pushTokenRepository.registerCurrentToken()
        }
    }

    private suspend fun requestNotificationPermissionIfNeeded() {
        val decision =
            NotificationPermissionPolicy.decide(
                sdkInt = Build.VERSION.SDK_INT,
                isGranted = isNotificationPermissionGranted(),
                hasRequestedBefore = pushPreferenceStorage.hasRequestedPermissionBefore(),
            )

        if (decision == NotificationPermissionDecision.REQUEST) {
            pushPreferenceStorage.markPermissionRequested()
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun LevelType.getResId(): Int {
        return when (this) {
            LevelType.ONE -> R.drawable.level_type_1
            LevelType.TWO -> R.drawable.level_type_2
            LevelType.THREE -> R.drawable.level_type_3
            LevelType.FOUR -> R.drawable.level_type_4
            LevelType.FIVE -> R.drawable.level_type_5
            LevelType.SIX -> R.drawable.level_type_6
            LevelType.SEVEN -> R.drawable.level_type_7
            LevelType.EIGHT -> R.drawable.level_type_8
        }
    }
    companion object {
        private const val TURTLE_BUBBLE_DURATION_MS = 3_000L
    }
}
