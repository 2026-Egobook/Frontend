package com.egobook.app.ui.square.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
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
import com.egobook.app.databinding.FragmentLetterReplyBinding
import com.egobook.app.databinding.LayoutLetterTooltipPopupBinding
import com.egobook.app.domain.model.square.letter.LetterBackgroundColor
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.model.square.letter.LetterStatus
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.square.model.letter.AbusiveContentModel
import com.egobook.app.domain.model.square.letter.LetterPaperItem
import com.egobook.app.ui.square.model.letter.ReplyLetterModel
import com.egobook.app.ui.square.viewmodel.LetterViewModel
import com.egobook.app.util.UiState
import kotlinx.coroutines.launch

class LetterReplyFragment : Fragment(R.layout.fragment_letter_reply) {
    private lateinit var binding: FragmentLetterReplyBinding
    private val viewModel: LetterViewModel by activityViewModels()
    private val letterItem by lazy {
        val args: LetterReplyFragmentArgs by navArgs()
        args.letterItem
    }
    private val premiumLetterColorList by lazy {
        listOf(
            binding.cvLetterReplyColorPink,
            binding.cvLetterReplyColorGreen,
            binding.cvLetterReplyColorBlue,
            binding.cvLetterReplyColorPurple
        )
    }

    private var loadingDialog: DetectAbusiveContentLoadingDialog? = null
    private var replyLetterColor: LetterBackgroundColor = LetterBackgroundColor.WHITE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLetterReplyBinding.bind(view)
        fetchData()
        initViews()
        initListeners()
        initObservers()
    }

    private fun fetchData() {
        viewModel.getUserInfo()
    }

    private fun initViews() = with(binding) {
        tvLetterReplyReceivedContent.text = letterItem.content
        cvLetterReplyColorBeige.isSelected = true
        cvLetterReplyColorBeige.getChildAt(0).isVisible = true
    }

    private fun selectReplyColor(color: LetterBackgroundColor) = with(binding) {
        replyLetterColor = color
        val allColorViews = premiumLetterColorList + cvLetterReplyColorBeige
        allColorViews.forEach { it.isSelected = false; it.getChildAt(0)?.isVisible = false }
        val selectedView = when (color) {
            LetterBackgroundColor.WHITE -> cvLetterReplyColorBeige
            LetterBackgroundColor.PINK -> cvLetterReplyColorPink
            LetterBackgroundColor.GREEN -> cvLetterReplyColorGreen
            LetterBackgroundColor.BLUE -> cvLetterReplyColorBlue
            LetterBackgroundColor.PURPLE -> cvLetterReplyColorPurple
        }
        selectedView.isSelected = true
        selectedView.getChildAt(0)?.isVisible = true
        val bgColorRes = when (color) {
            LetterBackgroundColor.WHITE -> R.color.letter_bg_beige
            LetterBackgroundColor.PINK -> R.color.letter_bg_pink
            LetterBackgroundColor.GREEN -> R.color.letter_bg_green
            LetterBackgroundColor.BLUE -> R.color.letter_bg_blue
            LetterBackgroundColor.PURPLE -> R.color.letter_bg_purple
        }
        cvLetterReplyContainer.setCardBackgroundColor(resources.getColor(bgColorRes, null))
    }

    private fun initListeners() = with(binding) {
        ivLetterReplyChevron.setOnClickListener {
            tvLetterReplyReceivedContent.isVisible = !tvLetterReplyReceivedContent.isVisible
            val chevron =
                if (tvLetterReplyReceivedContent.isVisible) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down
            ivLetterReplyChevron.setImageResource(chevron)
        }
        cvLetterReplyColorBeige.setOnClickListener {
            selectReplyColor(LetterBackgroundColor.WHITE)
        }
        premiumLetterColorList.forEach { colorView ->
            colorView.setOnClickListener { clickedView ->
                val color = when (clickedView.id) {
                    R.id.cv_letter_reply_color_pink -> LetterBackgroundColor.PINK
                    R.id.cv_letter_reply_color_green -> LetterBackgroundColor.GREEN
                    R.id.cv_letter_reply_color_blue -> LetterBackgroundColor.BLUE
                    else -> LetterBackgroundColor.PURPLE
                }
                val items = (viewModel.letterPaperItems.value as? UiState.Success)?.data
                val item = items?.find { it.color == color }
                if (item == null || item.isPurchased) {
                    selectReplyColor(color)
                } else {
                    val dialog = PremiumLetterBuyDialog(letterColor = color, letterPaperItem = item).apply { isCancelable = false }
                    dialog.show(childFragmentManager, PremiumLetterBuyDialog.TAG)
                    applyScreenBlur(BlurLevel.BASE)
                }
            }
        }
        etLetterReplyContent.addTextChangedListener(object : TextWatcher {
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
                btnLetterReply.isEnabled = !text.isNullOrBlank()
                tvLetterReplyContentLength.text = "${text?.length}/$MAX_LENGTH"
            }
        })
        ivLetterReplyTooltip.setOnClickListener {
            showTooltipPopup(anchorView = ivLetterReplyTooltip)
        }
        btnLetterReply.setOnClickListener {
            viewModel.detectAbusiveContent(text = etLetterReplyContent.text.toString())
        }
        ivLetterReplyBack.setOnClickListener {
            viewModel.deferReplyLetter(letterId = letterItem.letterId)
        }
    }

    private fun showTooltipPopup(anchorView: View) {
        val popupBinding = LayoutLetterTooltipPopupBinding.inflate(layoutInflater).apply {
            tvTooltipContent.text = if(letterItem.mode == LetterMode.RANDOM) "익명으로 상대에게 전달돼요\n비하나 과도한 비판 대신,\n공감부터 시작하는\n따뜻한 말을 보내주세요!" else "친구에게 편지가 전달돼요\n비하나 과도한 비판 대신,\n공감부터 시작하는\n따뜻한 말을 보내주세요!"
        }
        val popupWindow = PopupWindow(
            popupBinding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popupBinding.root.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val popupWidth = popupBinding.root.measuredWidth
        popupWindow.showAsDropDown(
            anchorView,
            0 - popupWidth + anchorView.width,
            0
        )
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userInfo.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success -> {
                                val userNickname = state.data.nickname
                                when(letterItem.mode) {
                                    LetterMode.FRIEND -> {
                                        tvLetterReplyReceiver.text = "To ${letterItem.fromLabel}"
                                        tvLetterReplySender.text = "From $userNickname"
                                    }
                                    LetterMode.RANDOM -> {
                                        tvLetterReplyReceiver.text = "To 낯선 고북이"
                                        tvLetterReplySender.text = "From 또다른 고북이"
                                    }
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.detectAbusiveContentResult.collect { state ->
                        when (state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {
                                showLoadingDialog()
                            }
                            is UiState.Success<AbusiveContentModel> -> {
                                hideLoadingDialog()
                                val abusiveContent = state.data
                                if (abusiveContent.isHarmful && abusiveContent.riskScore >= 80.0f) {
                                    val dialog = DetectAbusiveContentFailureDialog(
                                        originalContent = abusiveContent.text,
                                        badWords = abusiveContent.detectedBadWords
                                    ).apply {
                                        isCancelable = false
                                    }
                                    dialog.show(
                                        childFragmentManager,
                                        DetectAbusiveContentFailureDialog.TAG
                                    )
                                    applyScreenBlur(BlurLevel.BASE)
                                } else {
                                    viewModel.replyLetter(
                                        letterId = letterItem.letterId,
                                        text = etLetterReplyContent.text.toString()
                                    )
                                    // TODO: replyLetterColor를 replyLetter API에 전달 (백엔드 지원 시)
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.replyLetterResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<ReplyLetterModel> -> {
                                viewModel.getSentLetters(4)
                                val dialog = DetectAbusiveContentSuccessDialog(status = LetterStatus.REPLIED, replyItem = state.data).apply {
                                    isCancelable = false
                                }
                                dialog.show(parentFragmentManager, DetectAbusiveContentSuccessDialog.TAG)
                                applyScreenBlur(BlurLevel.BASE)
                            }
                        }
                    }
                }
                launch {
                    viewModel.deferReplyLetterResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Unit> -> {
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = DetectAbusiveContentLoadingDialog().apply {
                isCancelable = false
            }
            loadingDialog?.show(childFragmentManager, DetectAbusiveContentLoadingDialog.TAG)
            applyScreenBlur(BlurLevel.BASE)
        }
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
        removeScreenBlur()
    }

    companion object {
         private const val MAX_LENGTH = 350
    }
}