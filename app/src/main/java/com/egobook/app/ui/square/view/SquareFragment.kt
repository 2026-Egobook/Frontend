package com.egobook.app.ui.square.view

import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.FragmentSquareBinding
import com.egobook.app.databinding.LayoutPopupVisibilityTypeBinding
import com.egobook.app.domain.model.square.question.AnswerVisibility
import com.egobook.app.ui.square.adapter.MySentLettersAdapter
import com.egobook.app.ui.square.adapter.TodayQuestionFriendRepliesAdapter
import com.egobook.app.ui.square.model.letter.ArrivedPendingLetterModel
import com.egobook.app.ui.square.model.question.SubmitStatus
import com.egobook.app.ui.square.model.question.TodayAnswerModel
import com.egobook.app.ui.square.model.question.TodayQuestionModel
import com.egobook.app.ui.square.viewmodel.LetterViewModel
import com.egobook.app.ui.square.viewmodel.QuestionViewModel
import com.egobook.app.util.UiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SquareFragment : Fragment(R.layout.fragment_square) {
    private lateinit var binding: FragmentSquareBinding
    private val questionViewModel: QuestionViewModel by activityViewModels()
    private val letterViewModel: LetterViewModel by activityViewModels()

    private var visibilityType = AnswerVisibility.PUBLIC // 오늘의 질문 답변 제출할 때 필요한 변수

    private val todayQuestionAdapter by lazy {
        TodayQuestionFriendRepliesAdapter()
    }

    private val sentLetterAdapter by lazy {
        MySentLettersAdapter { letterId ->
            val action = SquareFragmentDirections.actionMenuSquareToMyLetterDetailFragment(letterId = letterId)
            findNavController().navigate(action)
        }
    }

    private var todayQuestionContent: String? = null

    private var submitButtonStatus: SubmitStatus = SubmitStatus.CREATE
    private val dividerDrawable by lazy {
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            color = resources.getColorStateList(R.color.green_secondary, null)
            setSize(0, (1*resources.displayMetrics.density).toInt())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSquareBinding.bind(view)
        fetchData()
        initViews()
        initListeners()
        initObservers()
    }

    private fun fetchData() {
        questionViewModel.getTodayQuestion()
        questionViewModel.getTodayFriendsReplies(size = 3)
        letterViewModel.getArrivedPendingLetter()
        letterViewModel.getSentLetters(size = 4)
    }

    private fun initViews() = with(binding) {
        rvSquareTodayQuestionFriendReply.adapter = todayQuestionAdapter
        rvSquareTodayQuestionFriendReply.addItemDecoration(object: RecyclerView.ItemDecoration() {
            override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                val left = parent.paddingLeft
                val right = parent.width - parent.paddingRight
                for(i in 0 until parent.childCount - 1) {
                    val child = parent.getChildAt(i)
                    val top = child.bottom
                    val bottom = top + dividerDrawable.intrinsicHeight
                    dividerDrawable.setBounds(left, top, right, bottom)
                    dividerDrawable.draw(c)
                }
            }
        })
        rvSquareSentLetter.adapter = sentLetterAdapter
    }

    private fun initListeners() = with(binding) {
        btnSquareNavigateToFriends.setOnClickListener {
            findNavController().navigate(R.id.action_menu_square_to_friendsFragment)
        }
        tvSquareTodayQuestionMyRepliesViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_menu_square_to_myRepliesHistoryFragment)
        }
        tvSquareAllUsersRepliesViewAll.setOnClickListener {
            val action = SquareFragmentDirections.actionMenuSquareToSquareAllRepliesFragment(todayQuestionContent = todayQuestionContent)
            findNavController().navigate(action)
        }
        tvSquareMyLettersViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_menu_square_to_myLettersFragment)
        }
        btnSquareTodayQuestionWrite.setOnClickListener {
            cvSquareTodayQuestionUnanswered.isVisible = false
            cvSquareTodayQuestionWriting.isVisible = true
        }
        llSquareTodayQuestionVisibilityType.setOnClickListener {
            showVisibilityTypePopup(llSquareTodayQuestionVisibilityType)
            ivSquareTodayQuestionInputVisibilityTypeToggle.setImageResource(R.drawable.ic_chevron_up)
        }
        etSquareTodayQuestionInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) = Unit
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) = Unit
            override fun onTextChanged(
                text: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                btnSquareTodayQuestionInputSave.isEnabled = !text.isNullOrBlank()
                tvSquareTodayQuestionInputLength.text = "${text?.length}/$MAX_LENGTH"
            }
        })
        btnSquareTodayQuestionInputSave.setOnClickListener {
            when(submitButtonStatus) {
                SubmitStatus.CREATE -> {
                    questionViewModel.submitTodayAnswer(answer = TodayAnswerModel(
                        content = etSquareTodayQuestionInput.text.toString(),
                        visibilityType = visibilityType
                    ))
                }
                SubmitStatus.UPDATE -> {
                    questionViewModel.updateTodayAnswer(updatedAnswer = TodayAnswerModel(
                        content = etSquareTodayQuestionInput.text.toString(),
                        visibilityType = visibilityType
                    ))
                }
            }
        }
        cvSquareTodayQuestionAnswered.setOnClickListener {
            cvSquareTodayQuestionAnswered.isVisible = false
            cvSquareTodayQuestionWriting.isVisible = true
        }
        cvSquareWriteLetter.setOnClickListener {
            findNavController().navigate(R.id.action_menu_square_to_letterWriteFragment)
        }
    }

    /**
     * 1. PopupWindow 생성자
     * 두번째, 세번째 인자: 팝업창의 너비와 높이
     * 네번째 인자: true로 설정하면 팝업이 포커스를 가진다.
     * 팝업이 포커스를 가지게 되면 팝업 바깥 영역을 터치했을 때 팝업이 자동으로 닫힌다.
     * 그리고 버튼을 다시 클릭해도 팝업창이 그대로 열린게 유지되는게 아니라 닫힌다.
     * 2. 뷰가 화면에 실제로 그려지기 전에 높이가 얼마인 지 미리 계산하는 함수이다.
     * 뷰의 크기는 화면에 그려진 후에야 알 수 있다. 하지만 팝업을 띄우기 전에 팝업의 높이를 알아야 적절한 위치에 띄울 수 있기 때문에, 먼저 계산해야 한다.
     * UNSPECIFIED 는 부모 뷰의 제약 없이 뷰가 원하는 만큼의 크기를 계산하라는 모드이다.
     * 3. 높이
     * height: 화면에 뷰가 실제로 그려진 후의 물리적 높이다. 그려지기 전엔 값이 0이다.
     * measuredHeight: measure() 메소드에 의해 계산된 높이다.
     * 팝업을 띄우는 시점에는 아직 팝업이 화면에 없으므로 height은 0이지만, measure() 메소드 후에 호출된 measuredHeight는 실제 높이 값이 있어 위치 계산에 사용할 수 있다.
     * 4. 생성자 설명
     * 첫 번째: 팝업이 붙는 기준점
     * xOffset = 0 은 anchorView의 왼쪽 끝과 팝업의 왼쪽 끝이 일치한다.
     * xOffset은 오른쪽으로 갈수록 커지고, 왼쪽으로 갈수록 작아진다.
     * yOffset = 0 은 anchorView의 바닥면에서 팝업이 시작된다.
     * showAsDropDown은 기본적으로 기준점의 아래쪽에서 팝업을 띄운다
     * 위쪽으로 갈수록 값이 작아지고, 아래쪽으로 갈수록 값이 커진다.
     */
    private fun showVisibilityTypePopup(anchorView: View) = with(binding) {
        val popupBinding = LayoutPopupVisibilityTypeBinding.inflate(layoutInflater)

        val popupWindow = PopupWindow(
            popupBinding.root, llSquareTodayQuestionVisibilityType.width,
            LinearLayout.LayoutParams.WRAP_CONTENT, true
        ) // 1

        popupBinding.tvPublic.setOnClickListener {
            tvSquareTodayQuestionInputVisibilityType.text = popupBinding.tvPublic.text
            ivSquareTodayQuestionInputVisibilityTypeToggle.setImageResource(R.drawable.ic_chevron_down)
            visibilityType = AnswerVisibility.PUBLIC
            popupWindow.dismiss()
        }
        popupBinding.tvFriend.setOnClickListener {
            tvSquareTodayQuestionInputVisibilityType.text = popupBinding.tvFriend.text
            ivSquareTodayQuestionInputVisibilityTypeToggle.setImageResource(R.drawable.ic_chevron_down)
            visibilityType = AnswerVisibility.FRIEND
            popupWindow.dismiss()
        }
        popupBinding.tvPrivate.setOnClickListener {
            tvSquareTodayQuestionInputVisibilityType.text = popupBinding.tvPrivate.text
            ivSquareTodayQuestionInputVisibilityTypeToggle.setImageResource(R.drawable.ic_chevron_down)
            visibilityType = AnswerVisibility.PRIVATE
            popupWindow.dismiss()
        }

        popupBinding.root.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        ) // 2

        val popupHeight = popupBinding.root.measuredHeight // 3
        popupWindow.showAsDropDown(
            anchorView,
            0,
            -(anchorView.height + popupHeight)
        ) // 4
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    questionViewModel.todayQuestion.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<TodayQuestionModel> -> {
                                val todayQuestion = state.data
                                todayQuestionContent = todayQuestion.content
                                if(todayQuestion.isUserAnswered) {
                                    cvSquareTodayQuestionAnswered.isVisible = true
                                    tvSquareTodayQuestionContentAnswered.text = "Q. ${todayQuestion.content}"
                                    tvSquareTodayQuestionDescriptionAnswered.text = todayQuestion.myAnswer?.content
                                    tvSquareTodayQuestionContentWriting.text = "Q. ${todayQuestion.content}"
                                    tvSquareTodayQuestionInputVisibilityType.text = todayQuestion.myAnswer?.visibility?.title
                                    etSquareTodayQuestionInput.setText(todayQuestion.myAnswer?.content)
                                    visibilityType = todayQuestion.myAnswer?.visibility ?: AnswerVisibility.PUBLIC
                                    submitButtonStatus = SubmitStatus.UPDATE
                                } else {
                                    cvSquareTodayQuestionUnanswered.isVisible = true
                                    tvSquareTodayQuestionContentUnanswered.text = "Q. ${todayQuestion.content}"
                                    tvSquareTodayQuestionContentWriting.text = "Q. ${todayQuestion.content}"
                                    etSquareTodayQuestionInput.setText("")
                                    visibilityType = AnswerVisibility.PUBLIC
                                    submitButtonStatus = SubmitStatus.CREATE
                                }
                            }
                        }
                    }
                }
                launch {
                    questionViewModel.submitTodayAnswerResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Unit> -> {
                                Toast.makeText(context, "답변 전송이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                cvSquareTodayQuestionWriting.isVisible = false
                                questionViewModel.getTodayQuestion()
                            }
                        }
                    }
                }
                launch {
                    questionViewModel.todayFriendsReplies.collectLatest { pagingData ->
                        if(pagingData != null) {
                            todayQuestionAdapter.submitData(lifecycle = lifecycle, pagingData = pagingData)
                        }
                    }
                }
                launch {
                    todayQuestionAdapter.loadStateFlow.collectLatest { loadStates ->
                        // 1. 현재 '새로고침(refresh)' 중인지 확인
                        val isRefreshing = loadStates.refresh is LoadState.Loading

                        // 2. '로딩 중이 아니면서' + '아이템이 0개'일 때만 진짜 비어있는 것으로 간주
                        val isListEmpty = loadStates.refresh is LoadState.NotLoading && todayQuestionAdapter.itemCount == 0

                        // 로딩 중일 때는 플레이스홀더와 리사이클러뷰를 모두 숨기거나,
                        // 데이터가 없을 때만 플레이스홀더를 보여줍니다.
                        tvSquareTodayQuestionFriendReplyPlaceholderMain.isVisible = isListEmpty
                        tvSquareTodayQuestionFriendReplyPlaceholderSub.isVisible = isListEmpty

                        // 데이터가 있고 로딩 중이 아닐 때만 리사이클러뷰를 보여줌
                        rvSquareTodayQuestionFriendReply.isVisible = !isListEmpty && !isRefreshing
                    }
                }
                launch {
                    questionViewModel.updateTodayAnswerResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Unit> -> {
                                Toast.makeText(context, "답변 업데이트가 되었습니다.", Toast.LENGTH_SHORT).show()
                                cvSquareTodayQuestionWriting.isVisible = false
                                questionViewModel.getTodayQuestion()
                            }
                        }
                    }
                }
                launch {
                    letterViewModel.arrivedPendingLetterResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<ArrivedPendingLetterModel> -> {
                                val arrivedPendingLetter = state.data.letter
                                if(arrivedPendingLetter != null) {
                                    val dialog = ArrivedPendingLetterPopupDialog(letterInfo = arrivedPendingLetter).apply { isCancelable = false }
                                    dialog.show(childFragmentManager, ArrivedPendingLetterPopupDialog.TAG)
                                    applyScreenBlur(BlurLevel.BASE)
                                }
                            }
                        }
                    }
                }
                launch {
                    letterViewModel.sentLetters.collectLatest { pagingData ->
                        if(pagingData != null) {
                            sentLetterAdapter.submitData(lifecycle, pagingData)
                        }
                    }
                }
                launch {
                    sentLetterAdapter.loadStateFlow.collectLatest { loadStates ->
                        val isListEmpty = loadStates.refresh is LoadState.NotLoading && sentLetterAdapter.itemCount == 0
                        tvSquareSentLetterPlaceholderMain.isVisible = isListEmpty
                        tvSquareSentLetterPlaceholderSub.isVisible = isListEmpty
                        rvSquareSentLetter.isVisible = !isListEmpty

                        val layoutParams = llSquareTodayQuestionHeader.layoutParams as ConstraintLayout.LayoutParams
                        if(!isListEmpty) {
                            layoutParams.topToBottom = ConstraintLayout.LayoutParams.UNSET
                            layoutParams.topToBottom = rvSquareSentLetter.id
                            layoutParams.topMargin = (24 * resources.displayMetrics.density).toInt()
                        } else {
                            layoutParams.topToBottom = ConstraintLayout.LayoutParams.UNSET
                            layoutParams.topToBottom = tvSquareSentLetterPlaceholderSub.id
                            layoutParams.topMargin = (48 * resources.displayMetrics.density).toInt()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val MAX_LENGTH = 250
    }
}
