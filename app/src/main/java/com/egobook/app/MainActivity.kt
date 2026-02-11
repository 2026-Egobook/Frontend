package com.egobook.app

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.egobook.app.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BlurController, NotificationController {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        applyDefaultInsets(binding.main)
        applyDefaultInsets(binding.fcvNotificationDrawer)
        setContentView(binding.root)
        val navHostFragment = binding.fragmentContainer.getFragment<NavHostFragment>()
        binding.bottomNavigation.setupWithNavController(navHostFragment.navController)
        // navController변수 선언
        val navController = navHostFragment.navController

        // 목적지 변경 리스너 추가: 특정 프래그먼트에서 바텀바 숨기기
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.diaryWriteFragment, // 일기 작성 화면
                R.id.calenderFragment, // 달력 화면
                R.id.storeFragment,
                R.id.accountFragment //계정 화면
                    -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
            }

            if (destination.id == R.id.menu_home) {
                binding.root.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                binding.root.closeDrawer(binding.fcvNotificationDrawer)
                binding.root.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        binding.blurView.setupWith(binding.blurTarget).setFrameClearDrawable(window.decorView.background).setBlurEnabled(false)
        binding.root.addDrawerListener(object: DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                activateBlur(BlurLevel.BASE)
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                deactivateBlur()
            }
        })
    }

    private fun applyDefaultInsets(targetView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(targetView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    override fun activateBlur(blurLevel: BlurLevel) {
        binding.blurView.apply {
            setBlurEnabled(true)
            setBlurRadius(blurLevel.value)
        }
    }

    override fun deactivateBlur() {
        binding.blurView.setBlurEnabled(false)
    }

    override fun openDrawer() {
        binding.root.openDrawer(binding.fcvNotificationDrawer)
    }

    override fun closerDrawer() {
        binding.root.closeDrawer(binding.fcvNotificationDrawer)
    }
}
