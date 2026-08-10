package com.egobook.app.ui.counseling.view

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.egobook.app.BuildConfig
import com.egobook.app.R
import com.egobook.app.databinding.DialogWeeklyReportUnlockBinding
import com.egobook.app.domain.model.counseling.WeeklyReportUnlockType
import com.egobook.app.ui.counseling.viewmodel.WeeklyReportViewModel
import com.egobook.app.ui.home.user.User
import com.egobook.app.util.UiState
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import timber.log.Timber

class WeeklyReportUnlockDialog(private val startDate: String): DialogFragment(R.layout.dialog_weekly_report_unlock) {
    private lateinit var binding: DialogWeeklyReportUnlockBinding
    private val viewModel: WeeklyReportViewModel by activityViewModels()
    private var rewardedAd: RewardedAd? = null
    private var isLoadingRewardedAd = false
    private var isUnlockRequestInFlight = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogWeeklyReportUnlockBinding.bind(view)
        initListeners()
        initObservers()
        loadRewardedAd()
    }

    private fun showCustomToast(message: String) {
        val rootView = requireActivity().findViewById<View>(android.R.id.content)
        val snackBar = Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT)

        val customView = layoutInflater.inflate(R.layout.toast_message, null)
        customView.findViewById<TextView>(R.id.tv_message).text = message

        val layout = snackBar.view as ViewGroup
        layout.setPadding(0, 0, 0, 0)
        layout.setBackgroundColor(Color.TRANSPARENT)
        layout.addView(customView, 0)

        val bottomNav = requireActivity().findViewById<View>(R.id.bottom_navigation)
        if (bottomNav != null) {
            snackBar.anchorView = bottomNav
            val extra = (9 * resources.displayMetrics.density).toInt()
            val params = snackBar.view.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin += extra
            snackBar.view.layoutParams = params
        }

        snackBar.show()
    }

    private fun initListeners() = with(binding) {
        btnWeeklyReportUnlockUseInk.setOnClickListener {
            if (isUnlockRequestInFlight) return@setOnClickListener
            isUnlockRequestInFlight = true
            viewModel.getUserInfo()
        }
        btnWeeklyReportUnlockWatchAdd.setOnClickListener {
            showRewardedAd()
        }
    }

    private fun loadRewardedAd() {
        if (isLoadingRewardedAd || rewardedAd != null) return
        isLoadingRewardedAd = true
        RewardedAd.load(
            requireContext(),
            BuildConfig.ADMOB_REWARDED_AD_INK_UNIT_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    isLoadingRewardedAd = false
                    rewardedAd = ad
                    Timber.d("주간 리포트 리워드 광고 로드 성공")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    isLoadingRewardedAd = false
                    rewardedAd = null
                    Timber.d("주간 리포트 리워드 광고 로드 실패, $adError")
                }
            }
        )
    }

    private fun showRewardedAd() {
        val ad = rewardedAd
        if (ad == null) {
            showCustomToast("광고를 준비 중이에요. 잠시 후 다시 시도해주세요.")
            loadRewardedAd()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                loadRewardedAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                rewardedAd = null
                loadRewardedAd()
                showCustomToast("광고를 표시하지 못했어요. 잠시 후 다시 시도해주세요.")
            }
        }
        ad.show(requireActivity()) {
            viewModel.unlockWeeklyReport(startDate = startDate, unlockType = WeeklyReportUnlockType.AD)
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userInfo.collect { state ->
                        when(state) {
                            is UiState.Failure -> {
                                isUnlockRequestInFlight = false
                            }
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<User> -> {
                                val userInfo = state.data
                                val currentInk = userInfo.ink.value
                                if (currentInk >= INK_PRICE) {
                                    viewModel.unlockWeeklyReport(startDate = startDate, unlockType = WeeklyReportUnlockType.INK)
                                } else {
                                    isUnlockRequestInFlight = false
                                    showCustomToast("현재 잉크가 부족합니다!")
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.unlockWeeklyReportResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {
                                isUnlockRequestInFlight = false
                                showCustomToast("잠금 해제에 실패했습니다. 잠시 후 다시 시도해주세요.")
                            }
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Unit> -> {
                                isUnlockRequestInFlight = false
                                showCustomToast("주간 보고서 잠금이 해제 되었습니다!")
                                val action = EgoRoomFragmentDirections.actionMenuEgoRoomToCounselingWeeklyReportDetailFragment(startDate = startDate)
                                findNavController().navigate(action)
                                dismiss()
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "WeeklyReportUnlockDialog"
        const val INK_PRICE = 10
    }
}