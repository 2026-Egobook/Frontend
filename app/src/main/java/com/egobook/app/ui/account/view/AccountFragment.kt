package com.egobook.app.ui.account.view

import android.content.Intent
import androidx.core.net.toUri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.egobook.app.BuildConfig
import com.egobook.app.R
import com.egobook.app.databinding.FragmentAccountBinding
import com.egobook.app.ui.account.viewmodel.AccountViewModel
import com.egobook.app.util.UiState
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.egobook.app.BlurLevel
import com.egobook.app.applyScreenBlur
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.graphics.Color
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context


@AndroidEntryPoint
class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountViewModel by viewModels()
    private lateinit var request: GetCredentialRequest
    private val credentialManager by lazy {
        CredentialManager.create(requireContext())
    }

    private val blurRadius = 5f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            insets
        }

        setClickListeners()
        setupBlur()
        observeUserIdState()
        observeLinkState()
        observeLinkToastEvent()
        observeNicknameState()
        observeNicknameToastEvent()
    }

    private fun observeUserIdState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userIdState.collect { state ->
                    when (state) {

                        is UiState.Idle -> {
                            binding.tvRealAccountId.visibility = View.INVISIBLE
                        }

                        is UiState.Loading -> {
                            binding.tvRealAccountId.visibility = View.INVISIBLE
                            //추후 로딩뷰를 삽입하자
                        }

                        is UiState.Success -> {
                            binding.tvRealAccountId.visibility = View.VISIBLE
                            binding.tvRealAccountId.text = state.data
                        }

                        is UiState.Failure -> {
                            binding.tvRealAccountId.visibility = View.INVISIBLE
                            Toast.makeText(requireContext(), "유저 id를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()

    private fun observeLinkState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.linkState.collect { state ->
                    when (state) {
                        is UiState.Idle -> Unit

                        is UiState.Loading -> {
                            //추후 로딩뷰를 삽입하자
                        }
                        is UiState.Success -> {
                            binding.btnIntegrate.apply {
                                layoutParams = layoutParams.apply {
                                    width = 111.dp   // 111dp
                                    height = 34.dp   // 34dp
                                }

                                icon = ContextCompat.getDrawable(context, R.drawable.ic_google_logo)
                                text = "연동완료"
                                //isEnabled = false
                            }
                        }
                        is UiState.Failure -> {

                        }
                    }
                }
            }
        }
    }

    private fun observeNicknameState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.nickname.collect { nickname ->
                    binding.tvNickname.text = nickname ?: "기본닉네임"
                }
            }
        }
    }

    private fun observeNicknameToastEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.nicknameToastEvent.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeLinkToastEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.linkToastEvent.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigate(R.id.action_accountFragment_to_homeFragment)
            }

            btnEditNickname.setOnClickListener {
                applyScreenBlur(BlurLevel.BASE)
                val dialog = NicknameEditDialogFragment()
                dialog.isCancelable = true
                dialog.show(childFragmentManager, NicknameEditDialogFragment.TAG)
            }

            btnIntegrate.setOnClickListener {
                binding.blurView.visibility = View.VISIBLE

                val accountBottomSheetFragment = AccountBottomSheetFragment()

                accountBottomSheetFragment.setOnLinkConfirmListener(object: AccountBottomSheetFragment.OnLinkConfirmListener {
                    override fun onLinkConfirmed() {
                        request = getGoogleRequest()

                        lifecycleScope.launch{
                            try {
                                val result = credentialManager.getCredential(
                                    request = request,
                                    context = requireContext()
                                )
                                handleGoogleResult(result)
                            } catch (e: GetCredentialException) {
                                Timber.d("계정연동 실패: ${e.message}")
                            }
                        }
                    }
                })
                accountBottomSheetFragment.show(childFragmentManager, AccountBottomSheetFragment.TAG)
            }

            tvDeleteAccount.setOnClickListener {
                applyScreenBlur(BlurLevel.BASE)

                val accountDeleteDialog1Fragment = AccountDeleteDialog1Fragment()
                accountDeleteDialog1Fragment.isCancelable = true
                accountDeleteDialog1Fragment.show(childFragmentManager, "AccountDeleteDialog1Fragment")
            }

            // 고객지원 클릭
            tvSupport.setOnClickListener {
                copySupportEmailToClipboard()
            }

            setupPromiseText()


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

    private fun handleGoogleResult(result: GetCredentialResponse) {
        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                Timber.d("Google ID Token 받음")

                //뷰모델의 linkToGoogle 메서드 호출
                viewModel.linkToGoogle(idToken)
            } catch (e: GetCredentialException) {
                Timber.e(e, "구글 토큰 파싱 실패")
            }
        } else {
            Timber.e("구글 로그인 credential 아님")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun copySupportEmailToClipboard() {
        val email = "egobook.official@gmail.com"   // ← 여기 너네 고객지원 이메일로 바꿔

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Support Email", email)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(requireContext(), "고객지원 이메일이 복사되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun setupPromiseText() {
        val fullText = "이용약관 및 개인정보처리"
        val spannable = SpannableString(fullText)

        // ===== 이용약관 =====
        val termsText = "이용약관"
        val termsStart = fullText.indexOf(termsText)
        val termsEnd = termsStart + termsText.length

        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "https://bevel-beetle-a49.notion.site/2f638a539ac5801aa872e99ec4282f28".toUri()   // ← 약관 URL
                )
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // ===== 개인정보처리 =====
        val privacyText = "개인정보처리"
        val privacyStart = fullText.indexOf(privacyText)
        val privacyEnd = privacyStart + privacyText.length

        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "https://bevel-beetle-a49.notion.site/2f638a539ac58059b9a1c883ad7d7164".toUri()  // ← 개인정보 URL
                )
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvPromise.text = spannable
        binding.tvPromise.movementMethod = LinkMovementMethod.getInstance()
        binding.tvPromise.highlightColor = Color.TRANSPARENT
    }

    //=============다이알로그 출력용 블러뷰 세팅====================

    private fun setupBlur() {
        binding.blurView.setupWith(binding.blurTarget)
            .setBlurRadius(blurRadius)
            .setBlurAutoUpdate(true)
    }
    fun clearBlur() {
        binding.blurView.visibility = View.GONE
    }

}