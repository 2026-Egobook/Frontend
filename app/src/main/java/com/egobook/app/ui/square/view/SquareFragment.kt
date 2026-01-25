package com.egobook.app.ui.square.view

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.egobook.app.R
import com.egobook.app.databinding.FragmentSquareBinding
import com.egobook.app.ui.square.model.question.TodayQuestionModel
import com.egobook.app.ui.square.viewmodel.QuestionViewModel
import com.egobook.app.util.UiState
import kotlinx.coroutines.launch

class SquareFragment : Fragment(R.layout.fragment_square) {
    private lateinit var binding: FragmentSquareBinding
    private val questionViewModel: QuestionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSquareBinding.bind(view)
        fetchData()
        initListeners()
        initCollectors()
    }

    private fun fetchData() {
        questionViewModel.getTodayQuestion()
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
    }

    private fun initCollectors() = with(binding) {
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
                                tvSquareTodayQuestionContent.text = "Q. ${todayQuestion.content}"
                                if(todayQuestion.isUserAnswered) {
                                    tvSquareTodayQuestionDescriptionUnanswered.isVisible = false
                                    btnSquareTodayQuestionWrite.isVisible = false
                                    cvSquareTodayQuestionDescriptionAnswered.isVisible = true
                                    tvSquareTodayQuestionDescriptionAnswered.text = "사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답 사용자가 작성한 답"
                                } else {
                                    cvSquareTodayQuestionDescriptionAnswered.isVisible = false
                                    tvSquareTodayQuestionDescriptionUnanswered.isVisible = true
                                    btnSquareTodayQuestionWrite.isVisible = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
