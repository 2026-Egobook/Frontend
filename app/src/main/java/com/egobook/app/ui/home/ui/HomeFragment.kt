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
import androidx.navigation.fragment.findNavController
import coil.load
import com.egobook.app.BlurLevel
import com.egobook.app.NotificationController
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.FragmentHomeBinding
import com.egobook.app.ui.home.HomeViewModel
import com.egobook.app.ui.home.repository.UserTendencyRepository
import com.egobook.app.ui.home.user.LevelType
import com.egobook.app.ui.home.ui.RadarDialog
import com.egobook.app.ui.home.ui.StreakDialog
import com.egobook.app.ui.shop.CustomItem
import com.egobook.app.ui.shop.ItemImage
import com.egobook.app.ui.shop.ItemType
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch
@AndroidEntryPoint
class HomeFragment(): Fragment() {
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: HomeViewModel by viewModels()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { userState ->
                    binding.tvLevel.text = "Lv ${userState.level.number}"
                    binding.ivLevelType.setImageResource(userState.level.type.getResId())
                    binding.tvInk.text = "${userState.ink.value}"
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.equippedItems.collect { equippedList ->
                equippedList.forEach { equippedItem ->
                    updateEquipItemUi(equippedItem)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dailyPhycologyReadState.collect { phycologyReadState ->
                if(phycologyReadState) {
                    binding.ivDailyBottle.visibility = View.VISIBLE
                }
            }
        }

        binding.ivStore.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_storeFragment)
        }

        binding.ivAd.setOnClickListener {
            applyScreenBlur(BlurLevel.BASE)
            val dialog = AdDialog()
            dialog.isCancelable = false
            dialog.show(parentFragmentManager, "ConfirmDialog")
        }
        binding.ivBell.setOnClickListener {
            val notificationController =
                checkNotNull(activity as? NotificationController) { "해당 액티비티는 notification controller를 구현하지 않았습니다" }
            notificationController.openDrawer()
        }

        binding.ivSetting.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_accountFragment)

        }

        binding.ivRadar.setOnClickListener {
            applyScreenBlur(BlurLevel.BASE)
            val dialog = RadarDialog()
            dialog.isCancelable = false
            dialog.show(parentFragmentManager, "RadarDialog")
        }

        binding.ivCalendar.setOnClickListener {
            applyScreenBlur(BlurLevel.BASE)
            val dialog = StreakDialog()
            dialog.isCancelable = false
            dialog.show(parentFragmentManager, "SteakDialog")
        }
    }

    private fun updateEquipItemUi(item: CustomItem) {
        when (item.type) {
            ItemType.BACK -> {
                if (item.outfitImage is ItemImage.Url) {
                    binding.ivHomeTurtleBack.load(item.outfitImage.path)
                }
            }
            ItemType.SKIN -> {
                if (item.outfitImage is ItemImage.Url) {
                    binding.ivHomeTurtleSkin.load(item.outfitImage.path)
                }
            }
            ItemType.DECO_1 -> {
                if (item.outfitImage is ItemImage.Url) {
                    if(item.outfitImage.path.contains("Default")) {
                        binding.ivHomeTurtleDeco1.load(null)
                        return
                    }
                    binding.ivHomeTurtleDeco1.load(item.outfitImage.path)
                }
            }
            ItemType.DECO_2 -> {
                if (item.outfitImage is ItemImage.Url) {
                    if(item.outfitImage.path.contains("Default")) {
                        binding.ivHomeTurtleDeco2.load(null)
                        return
                    }
                    binding.ivHomeTurtleDeco2.load(item.outfitImage.path)
                }
            }
            ItemType.BACKGROUND -> {
                if (item.outfitImage is ItemImage.Url) {
                    binding.ivHomeBackground.load(item.outfitImage.path)
                }
            }
        }
    }

    private fun LevelType.getResId(): Int {
        return when (this) {
            LevelType.ONE -> R.drawable.level_type_1
            LevelType.TWO -> R.drawable.level_type_2
            LevelType.THREE -> R.drawable.level_type_3
            LevelType.FOUR -> R.drawable.level_type_4
            LevelType.FIVE -> R.drawable.level_type_5
            LevelType.SIX -> R.drawable.level_type_6
            LevelType.SEVEN -> R.drawable.level_type_7
            LevelType.EIGHT -> R.drawable.level_type_8
        }
    }
}
