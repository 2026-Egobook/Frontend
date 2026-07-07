package com.egobook.app.ui.square.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.FragmentMyLetterDetailBinding
import com.egobook.app.domain.model.square.ReportOrigin
import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.ui.square.model.letter.ReceivedReplyModel
import com.egobook.app.ui.square.model.letter.SentLetterWithReplyModel
import com.egobook.app.ui.square.viewmodel.LetterViewModel
import com.egobook.app.util.UiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.egobook.app.ui.home.HomeViewModel

class MyLetterDetailFragment : Fragment(R.layout.fragment_my_letter_detail) {
    private lateinit var binding: FragmentMyLetterDetailBinding
    private val letterId: Long by lazy {
        val args: MyLetterDetailFragmentArgs by navArgs()
        args.letterId
    }
    private var replyId: Long? = null
    private var threadId: Long? = null
    private var isReplyReported: Boolean? = null

    private var myNickName: String? = null

    private val viewModel: LetterViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyLetterDetailBinding.bind(view)
        fetchData()
        initListeners()
        initObservers()
    }

    private fun fetchData() {
        viewModel.getSentLetterWithReply(letterId = letterId)
    }

    private fun initListeners() = with(binding) {
        ivMyLetterDetailBack.setOnClickListener {
            findNavController().popBackStack()
        }
        ivMyLetterDetailSentChevron.setOnClickListener {
            cvMyLetterDetailSentContent.isVisible = !cvMyLetterDetailSentContent.isVisible
            if (cvMyLetterDetailSentContent.isVisible) {
                ivMyLetterDetailSentChevron.setImageResource(R.drawable.ic_chevron_up)
                val params = llMyLetterDetailReplied.layoutParams as ConstraintLayout.LayoutParams
                with(params) {
                    topToBottom = ConstraintLayout.LayoutParams.UNSET
                    topToBottom = cvMyLetterDetailSentContent.id
                }
            } else {
                ivMyLetterDetailSentChevron.setImageResource(R.drawable.ic_chevron_down)
                val params = llMyLetterDetailReplied.layoutParams as ConstraintLayout.LayoutParams
                with(params) {
                    topToBottom = ConstraintLayout.LayoutParams.UNSET
                    topToBottom = tvMyLetterDetailSentTitle.id
                }
            }
        }
        ivMyLetterDetailReport.setOnClickListener {
            if (isReplyReported == true) Toast.makeText(
                context,
                "답장이 이미 신고되었습니다.",
                Toast.LENGTH_SHORT
            ).show() else {
                val dialog = SquareReportDialog(
                    origin = ReportOrigin.LETTER_REPLY,
                    letterId = letterId,
                    replyId = replyId,
                    onReportSuccess = {
                        isReplyReported = true
                    }
                ).apply { isCancelable = false }
                dialog.show(childFragmentManager, SquareReportDialog.TAG)
                applyScreenBlur(BlurLevel.BASE)
            }
        }
//        btnDeleteLetterThread.setOnClickListener {
//            viewModel.deleteLetterThread(threadId = threadId ?: -1L)
//        }
        btnDeleteLetterThread.setOnClickListener {
            val currentInk = homeViewModel.uiState.value.ink.value
            val dialog = LetterDeleteConfirmDialog(
                inkAmount = currentInk,
                onConfirmDelete = {
                    viewModel.deleteLetterThread(threadId = threadId ?: -1L)
                }
            ).apply { isCancelable = true }
            dialog.show(childFragmentManager, LetterDeleteConfirmDialog.TAG)
            applyScreenBlur(BlurLevel.BASE)
        }
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.sentLetterWithReply.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<SentLetterWithReplyModel> -> {
                                val data = state.data
                                threadId = data.threadId
                                val sentCardBackgroundColor = when(data.backgroundColor) {
                                    LetterBackgroundColor.WHITE -> R.color.letter_bg_beige
                                    LetterBackgroundColor.PINK -> R.color.letter_bg_pink
                                    LetterBackgroundColor.GREEN -> R.color.letter_bg_green
                                    LetterBackgroundColor.BLUE -> R.color.letter_bg_blue
                                    LetterBackgroundColor.PURPLE -> R.color.letter_bg_purple
                                }
                                tvMyLetterDetailSentAt.text = formatDate(createdDateTime = data.createdAt)
                                tvMyLetterDetailSentContent.text = data.sentContent
                                cvMyLetterDetailSentContent.backgroundTintList = resources.getColorStateList(sentCardBackgroundColor, null)
                                if(data.reply != null) {
                                    myNickName = data.fromLabel
                                    viewModel.getReceivedReplyById(replyId = data.reply.replyId)
                                } else {
                                    llMyLetterDetailReplied.isVisible = false
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.receivedReplies.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<ReceivedReplyModel> -> {
                                val replyItem = state.data

                                replyId = replyItem.replyId
                                isReplyReported = replyItem.isReported
                                tvMyLetterDetailRepliedContent.text = replyItem.replyContent
                                cvMyLetterDetailReceivedContent.backgroundTintList = when(replyItem.letterColor) {
                                    LetterBackgroundColor.WHITE -> resources.getColorStateList(R.color.letter_bg_beige, null)
                                    LetterBackgroundColor.PINK -> resources.getColorStateList(R.color.letter_bg_pink, null)
                                    LetterBackgroundColor.GREEN -> resources.getColorStateList(R.color.letter_bg_green, null)
                                    LetterBackgroundColor.BLUE -> resources.getColorStateList(R.color.letter_bg_blue, null)
                                    LetterBackgroundColor.PURPLE -> resources.getColorStateList(R.color.letter_bg_purple, null)
                                }

                                if(replyItem.isAIGenerated) {
                                    tvMyLetterDetailReceiver.text = "To $myNickName"
                                    tvMyLetterDetailSender.text = "FROM. 당신의 고북"
                                    tvMyLetterDetailAiGeneratedDescription.isVisible = true
                                    val params = tvMyLetterDetailSender.layoutParams as ConstraintLayout.LayoutParams
                                    params.topMargin = (72 * resources.displayMetrics.density).toInt()
                                } else {
                                    tvMyLetterDetailAiGeneratedDescription.isVisible = false
                                    when(replyItem.mode) {
                                        LetterMode.RANDOM -> {
                                            tvMyLetterDetailReceiver.text = "To 낯선 고북이"
                                            tvMyLetterDetailSender.text = "From 또 다른 고북이"
                                        }
                                        LetterMode.FRIEND -> {
                                            tvMyLetterDetailReceiver.text = "To $myNickName"
                                            tvMyLetterDetailSender.text = "From ${replyItem.fromLabel}"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.deleteLetterThreadResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Unit> -> {
                                Toast.makeText(context, "내가 쓴 편지가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun formatDate(createdDateTime: String): String {
        return try {
            val localDateTime = LocalDateTime.parse(createdDateTime)
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            formatter.format(localDateTime)
        } catch (e: Exception) {
            try {
                val instant = java.time.Instant.parse(createdDateTime)
                val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
                    .withZone(java.time.ZoneId.systemDefault())
                formatter.format(instant)
            } catch (e2: Exception) {
                try {
                    val zonedDateTime = java.time.ZonedDateTime.parse(createdDateTime)
                    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
                    formatter.format(zonedDateTime)
                } catch (e3: Exception) {
                    createdDateTime
                }
            }
        }
    }
}
