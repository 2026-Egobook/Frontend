package com.egobook.app.ui.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.egobook.app.R
import com.egobook.app.databinding.FragmentNotificationBinding
import com.egobook.app.ui.home.NotificationViewModel
import com.egobook.app.ui.home.notification.NotificationType
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment: Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment가 제거되었습니다." }
    private val viewModel: NotificationViewModel by viewModels()
    private val notificationAdapter = NotificationAdapter { notification ->
        viewModel.readNotification(notification)
        when (notification.type) {
            is NotificationType.EgoRoom -> {
                val bottomNav =
                    requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
                bottomNav.selectedItemId = R.id.menu_ego_room
            }
            is NotificationType.Letter -> {
                val bottomNav =
                    requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
                bottomNav.selectedItemId = R.id.menu_square
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadNotifications()

        binding.rvNotification.apply {
            adapter = notificationAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.ivHomeNotificationButton.setOnClickListener {
            viewModel.changeNotificationSetting()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notifications.collect { list ->
                    notificationAdapter.submitList(list)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notificationSettingState.collect { notificationSettingState ->
                    if (notificationSettingState.enabled) {
                        binding.ivHomeNotificationButton.setImageResource(R.drawable.ic_notification_on)
                    } else {
                        binding.ivHomeNotificationButton.setImageResource(R.drawable.ic_notification_off)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
