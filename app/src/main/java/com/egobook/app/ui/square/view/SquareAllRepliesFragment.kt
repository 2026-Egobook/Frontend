package com.egobook.app.ui.square.view

import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.FragmentSquareAllRepliesBinding
import com.egobook.app.domain.model.square.ReportOrigin
import com.egobook.app.ui.square.adapter.SquareAllRepliesAdapter
import com.egobook.app.ui.square.viewmodel.QuestionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SquareAllRepliesFragment : Fragment(R.layout.fragment_square_all_replies) {
    private lateinit var binding: FragmentSquareAllRepliesBinding
    private val reportedAnswerIds = mutableSetOf<Long>()
    private val adapter by lazy {
        SquareAllRepliesAdapter { answerId ->
            if (reportedAnswerIds.contains(answerId)) {
                Toast.makeText(context, "이미 신고한 답변입니다.", Toast.LENGTH_SHORT).show()
                return@SquareAllRepliesAdapter
            }
            val dialog = SquareReportDialog(
                origin = ReportOrigin.TODAY_QUESTION_ANSWER,
                answerId = answerId,
                onReportSuccess = {
                    reportedAnswerIds.add(answerId)
                }
            ).apply { isCancelable = false }
            dialog.show(childFragmentManager, SquareReportDialog.TAG)
            applyScreenBlur(BlurLevel.BASE)
        }
    }
    private val viewModel: QuestionViewModel by activityViewModels()

    private val args: SquareAllRepliesFragmentArgs by navArgs()

    private val dividerDrawable by lazy {
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            color = resources.getColorStateList(R.color.green_secondary, null)
            setSize(0, (1*resources.displayMetrics.density).toInt())
        }
    }

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
        rvSquareAllReplies.addItemDecoration(object: RecyclerView.ItemDecoration() {
            override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                val left = parent.paddingLeft
                val right = parent.width - parent.paddingRight
                for(i in 0 until parent.childCount) {
                    val child = parent.getChildAt(i)
                    val top = child.bottom
                    val bottom = top + dividerDrawable.intrinsicHeight
                    dividerDrawable.setBounds(left, top, right, bottom)
                    dividerDrawable.draw(c)
                }
            }
        })
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
                launch {
                    viewModel.todayAllUserReplies.collectLatest { pagingData ->
                        if(pagingData != null) adapter.submitData(lifecycle, pagingData)
                    }
                }
                launch {
                    adapter.loadStateFlow.collectLatest { loadStates ->
                        val isRefreshing = loadStates.refresh is LoadState.Loading
                        val isListEmpty = loadStates.refresh is LoadState.NotLoading && adapter.itemCount == 0
                        tvSquareAllRepliesPlaceholder.isVisible = isListEmpty
                        rvSquareAllReplies.isVisible = !isListEmpty
                    }
                }
            }
        }
    }

}
