package com.egobook.app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.egobook.app.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BlurController, NotificationController {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var rewardedAd: RewardedAd? = null
    private val adUnitId = "ca-app-pub-3940256099942544/5224354917"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        MobileAds.initialize(this) {}

        loadAd()

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
                R.id.accountFragment, //계정 화면
                R.id.psychologyFragment
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

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                rewardedAd = null
                Toast.makeText(this@MainActivity, "광고를 불러오는데 실패했습니다", Toast.LENGTH_SHORT).show()
                Log.d("AdMob", "광고 로드 실패, $adError")
            }

            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                Log.d("AdMob", "광고 로드 성공!")
            }
        })
    }

    fun showAd(userId: String, onAdClosed: () -> Unit) {
        if (rewardedAd != null) {

            val ssvOptions = ServerSideVerificationOptions.Builder()
                .setUserId(userId)

            rewardedAd?.setServerSideVerificationOptions(ssvOptions.build())

            rewardedAd?.show(this) { rewardItem ->
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
                Log.d("jang", "보상 지급! (서버로 콜백 날아감), $rewardType, rewardAmout: $rewardAmount")
            }
            onAdClosed()
            rewardedAd = null
            loadAd()
        } else {
            Log.d("jang", "아직 광고가 준비 안 됐어요. 잠시 후 다시 시도해주세요.")
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
