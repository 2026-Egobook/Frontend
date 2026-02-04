package com.egobook.app.ui.login.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.egobook.app.MainActivity
import com.egobook.app.R
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.ui.onboarding.view.OnboardingActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

//진짜 스플래시 화면으로 쓰는 용도
class IntroActivity : ComponentActivity() {
    @Inject lateinit var userInfoStorage: UserInfoStorage

    private var keepSplash = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplash }

        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            try {
                splashLogic()
            } finally {
                keepSplash = false
            }
        }

        actionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private suspend fun splashLogic() {
        //액세스 토큰 읽기
        val accessToken = userInfoStorage.getAccessToken().first()
        val hasAccessToken = !accessToken.isNullOrEmpty()

        //액세스 토큰이 있다면 메인 화면으로, 없다면 로그인 화면으로 이동
        if(hasAccessToken) {
            navigateToMain()
        } else {
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