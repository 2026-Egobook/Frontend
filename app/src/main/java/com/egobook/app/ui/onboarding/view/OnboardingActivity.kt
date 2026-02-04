package com.egobook.app.ui.onboarding.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.egobook.app.MainActivity
import com.egobook.app.databinding.ActivityOnboardingBinding
import com.egobook.app.ui.onboarding.adapter.OnboardingVPAdapter

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 하단 시스템 바 영역만큼 패딩 주기
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // 기존 패딩은 유지하면서 하단만 시스템 바 높이만큼 추가
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            insets
        }

        setAdapter()
    }

    private fun setAdapter() {
        val onboardingVPAdapter = OnboardingVPAdapter(this)
        binding.vpOnboarding.adapter = onboardingVPAdapter
        binding.circleIndicator.setViewPager(binding.vpOnboarding)
    }

    // OnboardingFifthFragment에서 호출할 메서드
    fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // 온보딩 액티비티 종료
    }
}
