package com.egobook.app.ui.counseling.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.egobook.app.R
import com.egobook.app.databinding.FragmentEgoRoomDailyPraiseBinding
import com.egobook.app.domain.model.NotificationType
import com.egobook.app.ui.counseling.adapter.CounselingDailyPraiseAdapter
import com.egobook.app.ui.counseling.model.DailyAndWeeklyNotificationModel
import com.egobook.app.ui.counseling.model.DailyPraiseDetailModel
import com.egobook.app.ui.counseling.viewmodel.DailyPraiseViewModel
import com.egobook.app.ui.notification.model.NotificationModel
import com.egobook.app.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EgoRoomDailyPraiseFragment : Fragment(R.layout.fragment_ego_room_daily_praise) {

    private lateinit var binding: FragmentEgoRoomDailyPraiseBinding
    private val viewModel: DailyPraiseViewModel by viewModels()
    private val counselingDailyPraiseAdapter = CounselingDailyPraiseAdapter { diaryDate ->
        viewModel.fetchDailyPraiseByData(date = diaryDate)
    }
    private var isNotificationEnabled: Boolean? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEgoRoomDailyPraiseBinding.bind(view)
        initViews()
        initListeners()
        initObservers()
        fetchData()
    }

    private fun initViews() = with(binding) {
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.divider_praise)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        with(recyclerviewCounselingDailyPraise) {
            adapter = counselingDailyPraiseAdapter
            addItemDecoration(dividerItemDecoration)
        }
    }

    private fun initListeners() = with(binding) {
        ivCounselingDailyPraiseNotification.setOnClickListener {
            if (isNotificationEnabled == true) {
                viewModel.updateDailyPraiseNotification(
                    isEnabled = false
                )
            } else {
                viewModel.updateDailyPraiseNotification(
                    isEnabled = true
                )
            }
        }
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dailyPraiseList.collect { pagingData ->
                        counselingDailyPraiseAdapter.submitData(lifecycle, pagingData)
                    }
                }
                launch {
                    counselingDailyPraiseAdapter.loadStateFlow.collect { loadStates ->
                        val isListEmpty = loadStates.source.refresh is LoadState.NotLoading && loadStates.append.endOfPaginationReached && counselingDailyPraiseAdapter.itemCount == 0
                        recyclerviewCounselingDailyPraise.isVisible = !isListEmpty
                        llCounselingDailyPraisePlaceholder.isVisible = isListEmpty
                    }
                }
                launch {
                    viewModel.dailyAndWeeklyNotification.collect { state ->
                        when (state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<DailyAndWeeklyNotificationModel> -> {
                                val notificationStatus: DailyAndWeeklyNotificationModel = state.data
                                updateNotificationUi(notificationStatus.isDailyPraiseEnabled)
                            }
                        }
                    }
                }
                launch {
                    viewModel.updateNotificationStatus.collect { state ->
                        when (state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Boolean> -> {
                                val isEnabled = state.data
                                updateNotificationUi(isEnabled)
                                val toastMessage = if (isEnabled) R.string.notification_on else R.string.notification_off
                                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                launch {
                    viewModel.dailyPraiseByDate.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<DailyPraiseDetailModel> -> {
                                val detailInfo = state.data
                                counselingDailyPraiseAdapter.updateItem(item = detailInfo)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateNotificationUi(isEnabled: Boolean) = with(binding) {
        this@EgoRoomDailyPraiseFragment.isNotificationEnabled = isEnabled
        if (isEnabled) {
            tvCounselingDailyPraiseNotification.text =
                getString(R.string.counseling_daily_praise_notification_on)
            ivCounselingDailyPraiseNotification.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_notification_on
                )
            )
        } else {
            tvCounselingDailyPraiseNotification.text =
                getString(R.string.counseling_daily_praise_notification_off)
            ivCounselingDailyPraiseNotification.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_notification_off
                )
            )
        }
    }

    private fun fetchData() {
        viewModel.fetchDailyPraises(size = 10)
        viewModel.getDailyAndWeeklyNotification()
    }
}