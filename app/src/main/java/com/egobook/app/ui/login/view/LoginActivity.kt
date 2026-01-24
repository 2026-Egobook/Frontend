package com.egobook.app.ui.login.view

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.egobook.app.R
import com.egobook.app.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    private val blurRadius = 5f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // 시스템 바를 고려한 패딩
        ViewCompat.setOnApplyWindowInsetsListener(binding.login) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupGuideText()
        setupBlur()
        setupClickListeners()

    }

    private fun setupBlur() {
        binding.blurView.setupWith(binding.blurTarget)
            .setBlurRadius(blurRadius)
            .setBlurAutoUpdate(true)
    }

    fun clearBlur() {
        binding.blurView.visibility = View.GONE
    }
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            binding.blurView.visibility = View.VISIBLE

            LoginBottomSheetFragment()
                .show(supportFragmentManager, LoginBottomSheetFragment.TAG)
        }
    }

    private fun setupGuideText() {
        val fullText = "시작 시 이용약관 및\n개인정보 수집 및 이용에 동의하게 됩니다"
        val spannableString = SpannableString(fullText)

        // 폰트 리소스를 Typeface 객체로 불러옴.
        val semiBoldTypeface = ResourcesCompat.getFont(this, R.font.arita_semibold) ?: return

        // 폰트를 적용할 텍스트 ("이용약관")
        val target1 = "이용약관"
        val start1 = fullText.indexOf(target1)
        if (start1 >= 0) {
            val end1 = start1 + target1.length
            spannableString.setSpan(CustomTypefaceSpan(semiBoldTypeface), start1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // 폰트를 적용할 텍스트 ("개인정보 수집 및 이용")
        val target2 = "개인정보 수집 및 이용"
        val start2 = fullText.indexOf(target2)
        if (start2 >= 0) {
            val end2 = start2 + target2.length
            spannableString.setSpan(CustomTypefaceSpan(semiBoldTypeface), start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // 완성된 SpannableString을 TextView에 적용
        binding.tvStartGuide.text = spannableString
    }

    /**
     * 모든 API 레벨에서 커스텀 폰트를 적용하기 위한 Span 클래스
     */
    private class CustomTypefaceSpan(private val typeface: Typeface) : MetricAffectingSpan() {
        override fun updateDrawState(ds: TextPaint) {
            applyCustomTypeface(ds)
        }

        override fun updateMeasureState(p: TextPaint) {
            applyCustomTypeface(p)
        }

        private fun applyCustomTypeface(paint: TextPaint) {
            paint.typeface = typeface
        }
    }
}