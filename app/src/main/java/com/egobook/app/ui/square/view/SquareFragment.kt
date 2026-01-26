package com.egobook.app.ui.square.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.egobook.app.R
import com.egobook.app.databinding.FragmentSquareBinding
import com.egobook.app.databinding.LayoutPopupVisibilityTypeBinding
import com.egobook.app.domain.model.square.question.AnswerVisibility
import com.egobook.app.ui.square.model.question.TodayAnswerModel
import com.egobook.app.ui.square.model.question.TodayQuestionModel
import com.egobook.app.ui.square.viewmodel.QuestionViewModel
import com.egobook.app.util.UiState
import kotlinx.coroutines.launch

class SquareFragment : Fragment(R.layout.fragment_square) {
    private lateinit var binding: FragmentSquareBinding
    private val questionViewModel: QuestionViewModel by activityViewModels()

    private var visibilityType = AnswerVisibility.PUBLIC // 오늘의 질문 답변 제출할 때 필요한 변수

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSquareBinding.bind(view)
        fetchData()
        initListeners()
        initObservers()
    }

    private fun fetchData() {
        questionViewModel.getTodayQuestion(isSubmit = false)
    }

    private fun initListeners() = with(binding) {
        btnSquareNavigateToFriends.setOnClickListener {
            findNavController().navigate(R.id.action_menu_square_to_friendsFragment)
        }
        tvSquareTodayQuestionMyRepliesViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_menu_square_to_myRepliesHistoryFragment)
        }
        tvSquareAllUsersRepliesViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_menu_square_to_squareAllRepliesFragment)
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
            questionViewModel.submitTodayAnswer(answer = TodayAnswerModel(
                content = etSquareTodayQuestionInput.text.toString(),
                visibilityType = visibilityType
            ))
        }
    }

    /**
     * 1. PopupWindow 생성자
     * 두번째, 세번째 인자: 팝업창의 너비와 높이
     * 네번째 인자: true로 설정하면 팝업이 포커스를 가진다. 팝업이 포커스를 가지게 되면 팝업 바깥 영역을 터치했을 때 팝업이 자동으로 닫힌다.
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
                                if(todayQuestion.isUserAnswered) {
                                    cvSquareTodayQuestionAnswered.isVisible = true
                                    tvSquareTodayQuestionContentAnswered.text = "Q. ${todayQuestion.content}"
                                    tvSquareTodayQuestionDescriptionAnswered.text = "사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답" // 필드가 없다!
                                } else {
                                    cvSquareTodayQuestionUnanswered.isVisible = true
                                    tvSquareTodayQuestionContentUnanswered.text = "Q. ${todayQuestion.content}"
                                    tvSquareTodayQuestionContentWriting.text = "Q. ${todayQuestion.content}"
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
                            is UiState.Success<*> -> {
                                // 답변 전송이 성공한 경우
                                Toast.makeText(context, "답변 전송이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                cvSquareTodayQuestionWriting.isVisible = false
                                questionViewModel.getTodayQuestion(isSubmit = true)
                            }
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
