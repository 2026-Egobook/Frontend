package com.egobook.app.ui.account.view

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
        setClickListeners()
        setupBlur()
        observeUserIdState()
        observeLinkState()
    }

    private fun observeUserIdState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userIdState.collect { state ->
                    when (state) {

                        is UiState.Idle -> Unit

                        is UiState.Loading -> {
                            //추후 로딩뷰를 삽입하자
                        }

                        is UiState.Success -> {
                            binding.tvRealAccountId.text = state.data
                        }

                        is UiState.Failure -> {
                            Toast.makeText(requireContext(), "유저 id를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

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
                                icon = ContextCompat.getDrawable(context, R.drawable.ic_google_logo)
                                text = "연동 완료"
                                isEnabled = false
                            }
                            Toast.makeText(requireContext(), "GOOGLE 계정 연동이 완료되었습니다!", Toast.LENGTH_SHORT).show()
                        }
                        is UiState.Failure -> {

                        }
                    }
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigate(R.id.action_accountFragment_to_homeFragment)
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