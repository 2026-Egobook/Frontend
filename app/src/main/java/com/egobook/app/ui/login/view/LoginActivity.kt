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
import com.egobook.app.MainActivity
import com.egobook.app.R
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.databinding.ActivityLoginBinding
import com.egobook.app.ui.login.viewmodel.LoginViewModel
import com.egobook.app.ui.login.viewmodel.LoginViewModel.AutoEvent as AutoEvent
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

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject lateinit var userInfoStorage: UserInfoStorage
    private lateinit var request: GetCredentialRequest
    var keepSplash = true
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val viewModel: LoginViewModel by viewModels()
    private val blurRadius = 5f

    private val credentialManager by lazy {
        CredentialManager.create(this)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplash }

        lifecycleScope.launch {
            splashLogic()
            keepSplash = false  // 로직 완료 후 사라짐
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // 시스템 바를 고려한 패딩
        ViewCompat.setOnApplyWindowInsetsListener(binding.login) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //observeLoginState()
        observeAutoLoginState()
        setupGuideText() //폰트 커스텀 적용
        setupBlur() //블러뷰
        setupClickListeners() //클릭리스너 설정

    }

    //===========================================스플래시 회면에서의 자동 로그인 시도===============================================
    private suspend fun splashLogic() {

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(getString(R.string.google_web_client_id))
            .setAutoSelectEnabled(true)
            .build()

        request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(
                request = request,
                context = this@LoginActivity
            )
            handleSignIn(result)
        } catch (e: GetCredentialException) {
            // 자동 로그인 실패 -> 로그인 액티비티로 이동
            Log.d(TAG, "회원가입한 적 없음 → 로그인 화면으로 이동")
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {

        val credential = result.credential

        // 기대하는 건 Google 로그인뿐
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                //val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                Log.d(TAG, "Google ID Token 받음")

                // 서버에서 토큰 가져오기 요청 -> 성공 시 메인액티비티로
                viewModel.onAutoEvent(AutoEvent.TryAutoLoginByGoogle)

            } catch (e: GoogleIdTokenParsingException) {
                Log.e(TAG, "구글 토큰 파싱 실패", e)
            }

        } else {
            Log.e(TAG, "구글 로그인 credential 아님")
        }
    }

    //=======================================================================================================================

    private fun setupBlur() {
        binding.blurView.setupWith(binding.blurTarget)
            .setBlurRadius(blurRadius)
            .setBlurAutoUpdate(true)
    }

    fun clearBlur() {
        binding.blurView.visibility = View.GONE
    }
    private fun setupClickListeners() {
        // 상단 로그인 버튼 - 바텀시트 띄우기
        binding.btnLogin.setOnClickListener {
            binding.blurView.visibility = View.VISIBLE

            LoginBottomSheetFragment()
                .show(supportFragmentManager, LoginBottomSheetFragment.TAG)
        }

        // Google 계정으로 회원가입 버튼 - 구글 로그인 창 띄우기
        binding.btnGoogleLogin.setOnClickListener {
            TODO()
        }
    }

//    private fun observeLoginState() {
//        lifecycleScope.launch {
//            viewModel.loginState.collect { state ->
//                when (state) {
//                    is LoginState.Success -> {
//                        Toast.makeText(
//                            this@LoginActivity,
//                            "회원가입 성공!",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        navigateToMain()
//                    }
//                    is LoginState.Error -> {
//                        Toast.makeText(
//                            this@LoginActivity,
//                            "회원가입 실패: ${state.message}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    else -> {}
//                }
//            }
//        }
//    }

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