package com.egobook.app.ui.login.view

import android.content.Intent
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetCredentialRequest
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import com.egobook.app.MainActivity
import com.egobook.app.R
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.databinding.ActivityLoginBinding
import com.egobook.app.ui.login.viewmodel.LoginViewModel
import com.egobook.app.ui.login.viewmodel.LoginViewModel.AutoEvent as AutoEvent
import com.egobook.app.ui.login.viewmodel.LoginViewModel.LoginEvent as LoginEvent
import com.egobook.app.ui.login.viewmodel.LoginViewModel.LoginState as LoginState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import org.json.JSONObject
import android.util.Base64

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject lateinit var userInfoStorage: UserInfoStorage
    private lateinit var request: GetCredentialRequest //로그인 요청 -> 구글에서 id토큰 받아오는 request
    private var keepSplash = true
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val viewModel: LoginViewModel by viewModels()
    private val blurRadius = 5f

    private val credentialManager by lazy {
        CredentialManager.create(this)
    }

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
        enableEdgeToEdge()
        setContentView(binding.root)

        // 시스템 바를 고려한 패딩
        ViewCompat.setOnApplyWindowInsetsListener(binding.login) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observeLoginState()
        observeAutoLoginState()
        setupGuideText() //폰트 커스텀 적용
        setupBlur() //블러뷰
        setupClickListeners() //클릭리스너 설정

    }

    //===========================================스플래시 화면에서의 자동 로그인 시도===============================================
    private suspend fun splashLogic() {
        //액세스 토큰 확인
        val accessToken = userInfoStorage.getAccessToken().first()
        val hasAccessToken = !accessToken.isNullOrEmpty()

        // 리프레시 토큰도 확인
        val refreshToken = userInfoStorage.getRefreshToken().first()
        val hasRefreshToken = !refreshToken.isNullOrEmpty()

        when {
            hasAccessToken && isTokenValid(accessToken) -> {
                Log.d(TAG, "AccessToken 유효 → 자동 로그인")
                navigateToMain()
                return@splashLogic
            }
            hasRefreshToken -> {
                Log.d(TAG, "AccessToken 만료 → RefreshToken으로 재발급 시도")
                viewModel.onAutoEvent(AutoEvent.TryAutoLoginByGoogle)
                navigateToMain()
                return@splashLogic
            }
            else -> {
                Log.d(TAG, "리프레스 토큰 없음 or 만료 → 로그인 화면 표시")
                return@splashLogic
            }

        }
    }

    //=======================================================================================================================


    //토큰 유효성 체크
    fun isTokenValid(token: String): Boolean {
        val parts = token.split(".")
        if (parts.size != 3) return false

        // payload 디코딩 (Base64 URL Safe)
        val payload = try {
            val decoded = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP)
            String(decoded)
        } catch (e: Exception) {
            return false
        }

        // JSON에서 exp 필드 추출
        val exp = try {
            JSONObject(payload).getLong("exp")
        } catch (e: Exception) {
            return false
        }

        val currentTime = System.currentTimeMillis() / 1000
        return exp > currentTime
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
        // 상단 로그인 버튼 - 콜백 인터페이스 세팅 & 바텀시트 띄우기
        binding.btnLogin.setOnClickListener {
            binding.blurView.visibility = View.VISIBLE

            val loginBottomSheet = LoginBottomSheetFragment()
            loginBottomSheet.setOnLoginConfirmListener(object : LoginBottomSheetFragment.OnLoginConfirmListener {
                override fun onLoginConfirmed() {

                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(true)
                        .setServerClientId(getString(R.string.google_web_client_id))
                        .setAutoSelectEnabled(true)
                        .build()

                    request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    lifecycleScope.launch {
                        try {
                            val result = credentialManager.getCredential(
                                request = request,
                                context = this@LoginActivity
                            )
                            handleSignIn(result, isLogin = true)
                        } catch (e: GetCredentialException) {
                            Log.d(TAG, "로그인 실패")
                        }

                    }
                }

            })
            loginBottomSheet.show(supportFragmentManager, LoginBottomSheetFragment.TAG)
        }

        // Google 계정으로 회원가입 버튼 - 구글 로그인 창 띄우기
        binding.btnGoogleLogin.setOnClickListener {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(getString(R.string.google_web_client_id))
                .setAutoSelectEnabled(true)
                .build()

            request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            lifecycleScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@LoginActivity
                    )
                    handleSignIn(result, isLogin = false)
                } catch (e: GetCredentialException) {
                    Log.d(TAG, "회원가입 실패")
                }

            }

        }
    }

    private fun handleSignIn(result: GetCredentialResponse, isLogin: Boolean = false) {
        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                Log.d(TAG, "Google ID Token 받음")

                // 회원가입 vs 로그인 분기
                if (isLogin) {
                    viewModel.onEvent(LoginEvent.TryLoginByGoogle(idToken))  // 로그인
                } else {
                    viewModel.onEvent(LoginEvent.TrySignInByGoogle(idToken))  // 회원가입
                }

            } catch (e: GetCredentialException) {
                Log.e(TAG, "구글 토큰 파싱 실패", e)
            }

        } else {
            Log.e(TAG, "구글 로그인 credential 아님")
        }
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Success -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "회원가입 성공!",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateToMain()
                    }
                    is LoginState.Error -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "회원가입 실패: ${state.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun observeAutoLoginState() {
        lifecycleScope.launch {
            viewModel.autoLoginState.collect { state ->
                when (state) {
                    is LoginState.Success -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "자동 로그인 성공!",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateToMain()
                    }
                    is LoginState.Error -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "자동 로그인 실패: ${state.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


//========================================특정 char 폰트 커스텀===========================================================
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

    //모든 API 레벨에서 커스텀 폰트를 적용하기 위한 Span 클래스
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

//=======================================================================================================================


    companion object {
        private const val TAG = "LoginActivity"
    }
}