package com.egobook.app.ui.login.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.egobook.app.MainActivity
import com.egobook.app.R
import com.egobook.app.data.local.UserInfoStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

//진짜 스플래시 화면으로 쓰는 용도
@AndroidEntryPoint
class IntroActivity : ComponentActivity() {
    @Inject lateinit var userInfoStorage: UserInfoStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate 시작")
        
        // SplashScreen API는 빠르게 종료 (배경색만 잠깐 보여줌)
        installSplashScreen()

        super.onCreate(savedInstanceState)

        actionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Timber.d("UI 설정 완료, 코루틴 시작")
        
        // IntroActivity UI가 렌더링된 후 로직 실행
        lifecycleScope.launch {
            try {
                // UI 렌더링을 위한 짧은 대기
                delay(100)
                splashLogic()
            } catch (e: Exception) {
                // 예외 발생 시 로그인 화면으로 이동
                Timber.e(e, "예외 발생: ${e.message}")
                navigateToLogin()
            }
        }
    }

    private suspend fun splashLogic() {
        Timber.d("splashLogic 시작")
        
        // IntroActivity UI를 보여주는 시간
        delay(1400)

        //액세스 토큰 읽기
        val accessToken = userInfoStorage.getAccessToken().first()
        val hasAccessToken = !accessToken.isNullOrEmpty()
        
        Timber.d("토큰 존재 여부: $hasAccessToken")

        //액세스 토큰이 있다면 메인 화면으로, 없다면 로그인 화면으로 이동
        if(hasAccessToken) {
            Timber.d("메인 화면으로 이동")
            navigateToMain()
        } else {
            Timber.d("로그인 화면으로 이동")
            navigateToLogin()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}