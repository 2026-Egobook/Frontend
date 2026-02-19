    package com.egobook.app.ui.login.view

    import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.MetricAffectingSpan
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.egobook.app.BuildConfig
import com.egobook.app.MainActivity
import com.egobook.app.R
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.databinding.ActivityLoginBinding
import com.egobook.app.ui.login.viewmodel.LoginViewModel
import com.egobook.app.ui.login.viewmodel.LoginViewModel.LoginEvent as LoginEvent
import com.egobook.app.ui.login.viewmodel.LoginViewModel.LoginState as LoginState
import com.egobook.app.ui.onboarding.view.OnboardingActivity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

    @AndroidEntryPoint
    class LoginActivity : AppCompatActivity() {

        @Inject lateinit var userInfoStorage: UserInfoStorage
        private lateinit var request: GetCredentialRequest
        private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
        private val viewModel: LoginViewModel by viewModels()
        private val blurRadius = 5f

        private val credentialManager by lazy {
            CredentialManager.create(this)
        }

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

            observeLoginState()
            observeFirstSignUp()
            observeGuestSignUp()
            setupGuideText() //폰트 커스텀 적용
            setupBlur() //블러뷰
            setupClickListeners() //클릭리스너 설정

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
                        request = getGoogleRequest()

                        lifecycleScope.launch {
                            try {
                                val result = credentialManager.getCredential(
                                    request = request,
                                    context = this@LoginActivity
                                )
                                handleSignIn(result, isLogin = true)
                            } catch (e: GetCredentialException) {
                                Timber.d("로그인 실패: ${e.message}")
                            }
                        }
                    }
                })
                loginBottomSheet.show(supportFragmentManager, LoginBottomSheetFragment.TAG)
            }

            //게스트 로그인 버튼 클릭이벤트
            binding.btnGuestLogin.setOnClickListener {
                viewModel.onEvent(LoginEvent.TryGuestLogin)
            }

            // Google 계정으로 회원가입 버튼 - 구글 로그인 창 띄우기
            binding.btnGoogleLogin.setOnClickListener {
                request = getGoogleRequest()

                lifecycleScope.launch {
                    try {
                        val result = credentialManager.getCredential(
                            request = request,
                            context = this@LoginActivity
                        )
                        handleSignIn(result, isLogin = false)
                    } catch (e: GetCredentialException) {
                        Timber.d("회원가입 실패: ${e.message}")
                    }

                }

            }

            //약관
            binding.tvStartGuide.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, "https://bevel-beetle-a49.notion.site/2f638a539ac58059b9a1c883ad7d7164".toUri())
                startActivity(intent)
            }
        }

        private fun getGoogleRequest(): GetCredentialRequest {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)  // 모든 구글 계정 표시
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)  // 사용자가 직접 선택
                .build()

            return GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
        }

        private fun handleSignIn(result: GetCredentialResponse, isLogin: Boolean = false) {
            val credential = result.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken

                    Timber.d("Google ID Token 받음")

                    // 회원가입 vs 로그인 분기
                    if (isLogin) {
                        viewModel.onEvent(LoginEvent.TryLoginByGoogle(idToken))  // 로그인
                    } else {
                        viewModel.onEvent(LoginEvent.TrySignInByGoogle(idToken))  // 회원가입
                    }

                } catch (e: GetCredentialException) {
                    Timber.e(e, "구글 토큰 파싱 실패")
                }

            } else {
                Timber.e("구글 로그인 credential 아님")
            }
        }
        private fun observeLoginState() {
            lifecycleScope.launch {
                viewModel.loginState.collect { state ->
                    when (state) {
                        is LoginState.Success -> {
                            Toast.makeText(
                                this@LoginActivity,
                                "로그인 성공!",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateToMain()
                        }
                        is LoginState.Error -> {
                            val message = state.error.message ?: "알 수 없는 오류가 발생했습니다"
                            Toast.makeText(
                                this@LoginActivity,
                                message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {}
                    }
                }
            }
        }

        private fun observeFirstSignUp() {
            lifecycleScope.launch {
                viewModel.isFirstSignUp.collect {
                    Toast.makeText(this@LoginActivity, "에고북에 오신 걸 환영합니다!", Toast.LENGTH_SHORT).show()
                    navigateToOnboarding() //온보딩 화면 이동
                }
            }
        }

        private fun observeGuestSignUp() {
            lifecycleScope.launch {
                viewModel.isGuestSignUp.collect {
                    Toast.makeText(this@LoginActivity, "에고북에 오신 걸 환영합니다!", Toast.LENGTH_SHORT).show()
                    navigateToOnboarding() //온보딩 화면 이동
                }
            }
        }

        private fun navigateToMain() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        private fun navigateToOnboarding() {
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        }


    //========================================특정 char 폰트 커스텀===========================================================
    private fun setupGuideText() {
        val fullText = "시작 시 이용약관 및\n개인정보 수집 및 이용에 동의하게 됩니다"
        val spannableString = SpannableString(fullText)

        val semiBoldTypeface = ResourcesCompat.getFont(this, R.font.arita_semibold) ?: return

        // ================= 이용약관 =================
        val termText = "이용약관"
        val termStart = fullText.indexOf(termText)
        if (termStart >= 0) {
            val termEnd = termStart + termText.length

            spannableString.setSpan(
                CustomTypefaceSpan(semiBoldTypeface),
                termStart, termEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannableString.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://bevel-beetle-a49.notion.site/2f638a539ac5801aa872e99ec4282f28".toUri() // ← 약관 URL
                    )
                    startActivity(intent)
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                }
            }, termStart, termEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // ================= 개인정보 =================
        val privacyText = "개인정보 수집 및 이용"
        val privacyStart = fullText.indexOf(privacyText)
        if (privacyStart >= 0) {
            val privacyEnd = privacyStart + privacyText.length

            spannableString.setSpan(
                CustomTypefaceSpan(semiBoldTypeface),
                privacyStart, privacyEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannableString.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://bevel-beetle-a49.notion.site/2f638a539ac58059b9a1c883ad7d7164".toUri() // ← 개인정보 URL
                    )
                    startActivity(intent)
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                }
            }, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.tvStartGuide.text = spannableString
        binding.tvStartGuide.movementMethod = LinkMovementMethod.getInstance()
        binding.tvStartGuide.highlightColor = Color.TRANSPARENT
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
    }