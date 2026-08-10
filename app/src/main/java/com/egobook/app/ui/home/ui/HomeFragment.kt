package com.egobook.app.ui.home.ui

import android.Manifest
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

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
    }

    override fun onResume() {
        super.onResume()
        redDotViewModel.refresh()
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
}
