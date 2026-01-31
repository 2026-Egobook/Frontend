package com.egobook.app.ui.login.view

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.egobook.app.MainActivity
import com.egobook.app.R
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.databinding.ActivityLoginBinding
import com.egobook.app.ui.onboarding.view.OnboardingActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    
    @Inject
    lateinit var userInfoStorage: UserInfoStorage

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

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

        setupGoogleSignIn()
        setupGuideText()
        setupBlur()
        setupClickListeners()

    }

    private fun setupGoogleSignIn() {
        // Google Sign-In 결과를 받을 ActivityResultLauncher 초기화
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task) //회원가입 성공 여부에 따른 로직
        }

        // Google Sign-In 옵션 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
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
        // 상단 로그인 버튼 - 바텀시트 띄우기
        binding.btnLogin.setOnClickListener {
            binding.blurView.visibility = View.VISIBLE

            LoginBottomSheetFragment()
                .show(supportFragmentManager, LoginBottomSheetFragment.TAG)
        }

        // Google 계정으로 회원가입 버튼 - 구글 로그인 창 띄우기
        binding.btnGoogleLogin.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    //회원가입 성공 여부에 따른 로직
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account.idToken

            if (idToken != null) {
                // Google ID Token 저장
                lifecycleScope.launch {
                    userInfoStorage.saveIdToken(idToken)
                    
                    Log.d(TAG, "Google Sign-In 성공")
                    Log.d(TAG, "이름: ${account.displayName}")
                    Log.d(TAG, "이메일: ${account.email}")
                    Log.d(TAG, "ID Token 저장 완료")

                    Toast.makeText(
                        this@LoginActivity,
                        "로그인 성공: ${account.displayName}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // MainActivity로 이동 - 임시 구현
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                Log.e(TAG, "ID Token이 null입니다")
                Toast.makeText(
                    this,
                    "로그인 실패: 토큰을 받지 못했습니다",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } catch (e: ApiException) {
            // 로그인 실패
            Log.e(TAG, "Google Sign-In 실패: ${e.statusCode}", e)
            Toast.makeText(
                this,
                "로그인 실패: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
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

    companion object {
        private const val TAG = "LoginActivity"
    }
}