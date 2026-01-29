package com.egobook.app.ui.square.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.egobook.app.R
import com.egobook.app.databinding.FragmentSquareAllRepliesBinding
import com.egobook.app.ui.square.adapter.SquareAllRepliesAdapter
import com.egobook.app.ui.square.viewmodel.QuestionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SquareAllRepliesFragment : Fragment(R.layout.fragment_square_all_replies) {
    private lateinit var binding: FragmentSquareAllRepliesBinding
    private val adapter = SquareAllRepliesAdapter()
    private val viewModel: QuestionViewModel by activityViewModels()

    private val args: SquareAllRepliesFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSquareAllRepliesBinding.bind(view)
        fetchData()
        initViews()
        initListeners()
        initObservers()
    }

    private fun fetchData() {
        viewModel.getTodayAllUserReplies(size = 3)
    }

    private fun initViews() = with(binding) {
        tvSquareAllRepliesTodayQuestion.text = "Q. \n${args.todayQuestionContent}"
        rvSquareAllReplies.adapter = adapter
    }

    private fun initListeners() = with(binding) {
        ivSquareAllRepliesBack.setOnClickListener {
            findNavController().popBackStack()
        }
        fabSquareAllReplies.setOnClickListener {
            rvSquareAllReplies.smoothScrollToPosition(0)
        }
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.todayAllUserReplies.collectLatest { pagingData ->
                    if(pagingData != null) adapter.submitData(lifecycle, pagingData)
                }
            }
        }
    }

}