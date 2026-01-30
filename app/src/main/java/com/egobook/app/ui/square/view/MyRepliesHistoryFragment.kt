package com.egobook.app.ui.square.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.egobook.app.R
import com.egobook.app.databinding.FragmentMyRepliesHistoryBinding
import com.egobook.app.ui.square.adapter.MyRepliesHistoryAdapter
import com.egobook.app.ui.square.viewmodel.QuestionViewModel
import com.egobook.app.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyRepliesHistoryFragment : Fragment(R.layout.fragment_my_replies_history) {
    private lateinit var binding: FragmentMyRepliesHistoryBinding
    private val viewModel: QuestionViewModel by activityViewModels()
    private val adapter by lazy {
        MyRepliesHistoryAdapter { answerId ->
            viewModel.deleteMyQuestionAnswer(answerId = answerId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyRepliesHistoryBinding.bind(view)
        fetchData()
        initViews()
        initListeners()
        initObservers()
    }

    private fun fetchData() {
        viewModel.getMyRepliesHistory(size = 10)
    }

    private fun initViews() = with(binding) {
        rvMyRepliesHistory.adapter = adapter
    }
    private fun initListeners() = with(binding) {
        ivMyRepliesHistoryBack.setOnClickListener {
            findNavController().popBackStack()
        }
        btnMyRepliesHistoryScrollUp.setOnClickListener {
            rvMyRepliesHistory.smoothScrollToPosition(0)
        }
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.myRepliesHistory.collectLatest { item ->
                        if(item != null) {
                            adapter.submitData(lifecycle, item)
                        }
                    }
                }
                launch {
                    viewModel.deleteMyQuestionAnswerResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Unit> -> {
                                Toast.makeText(context, "유저의 답변이 삭제 되었습니다", Toast.LENGTH_SHORT).show()
                                adapter.refresh()
                            }
                        }
                    }
                }
            }
        }
    }
}