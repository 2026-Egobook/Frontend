package com.egobook.app.ui.home.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.egobook.app.R
import com.egobook.app.databinding.FragmentNotificationBinding
import com.egobook.app.ui.home.NotificationViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment: Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment가 제거되었습니다." }
    private val viewModel: NotificationViewModel by viewModels()
    private val notificationAdapter = NotificationAdapter { notification ->
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // "EgoRoom 탭 버튼을 누른 것으로 처리해줘!"
        bottomNav.selectedItemId = R.id.menu_ego_room
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
                viewModel.notificationSettingState.collect { notificationSettingDto ->
                    Log.d("jang", "UI에서 감지된 설정: ${notificationSettingDto}")
                    if(notificationSettingDto.isEnabled) {
                        binding.ivHomeNotificationButton.setImageResource(R.drawable.ic_notification_on)
                    } else {
                        binding.ivHomeNotificationButton.setImageResource(R.drawable.ic_notification_off)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notifications.collect { list ->
                    notificationAdapter.submitList(list)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
