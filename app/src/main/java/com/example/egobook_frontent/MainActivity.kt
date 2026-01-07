package com.example.egobook_frontent

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.egobook_frontent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        setContentView(binding.root)
        val navHostFragment = binding.fragmentContainer.getFragment<NavHostFragment>()

        binding.bottomNavigation.setupWithNavController(navHostFragment.navController)

        // navController변수 선언.
        val navController = navHostFragment.navController

        // 목적지 변경 리스너 추가: 특정 프래그먼트에서 바텀바 숨기기
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.diaryWriteFragment) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }
}
