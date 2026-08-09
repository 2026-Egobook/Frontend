package com.egobook.app.ui.home.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.egobook.app.databinding.DialogNoticeBinding
import com.egobook.app.removeScreenBlur

/**
 * 공지(노션) 페이지를 웹뷰로 보여주는 다이얼로그.
 *
 * 페이지 로드에 실패하면 다이얼로그를 닫고 [RESULT_KEY]로 실패를 알린다.
 */
class NoticeDialog : DialogFragment() {
    private var _binding: DialogNoticeBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment가 제거되었습니다." }

    private var loadFailed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogNoticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivNoticeClose.setOnClickListener { closeWithBlurRemoved() }

        with(binding.wvNotice) {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.pbNotice.visibility = View.GONE
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    if (request?.isForMainFrame != true) return
                    notifyLoadFailed()
                }
            }
            loadUrl(requireNoticeUrl())
        }
    }

    private fun notifyLoadFailed() {
        if (loadFailed) return
        loadFailed = true
        setFragmentResult(RESULT_KEY, bundleOf(RESULT_LOAD_FAILED to true))
        dismissAllowingStateLoss()
    }

    private fun closeWithBlurRemoved() {
        removeScreenBlur()
        dismiss()
    }

    private fun requireNoticeUrl(): String =
        checkNotNull(arguments?.getString(ARG_NOTICE_URL)) { "공지 주소가 전달되지 않았습니다." }

    override fun onDestroyView() {
        _binding?.wvNotice?.destroy()
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val RESULT_KEY = "notice_result"
        const val RESULT_LOAD_FAILED = "notice_load_failed"
        private const val ARG_NOTICE_URL = "notice_url"

        fun newInstance(url: String): NoticeDialog = NoticeDialog().apply {
            arguments = bundleOf(ARG_NOTICE_URL to url)
        }
    }
}
