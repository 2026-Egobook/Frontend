package com.egobook.app.ui.shop

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.FragmentStoreBinding
import com.egobook.app.ui.home.ui.AdDialog
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoreFragment: Fragment() {
    private var _binding: FragmentStoreBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment가 제거되었습니다." }
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        viewPager = binding.vp2StoreCollectionContainer
        viewPager.adapter = StoreCollectionAdapter(this)
        val tabLayout = binding.tlTabs
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = ItemTab.of(position).text
        }.attach()

        val viewModel: StoreViewModel by activityViewModels()
        viewModel.loadEquippedItems()
        binding.ivBack.setOnClickListener {
            applyScreenBlur(BlurLevel.BASE)
            val dialog = StoreLeavingDialog()
            dialog.isCancelable = false
            dialog.show(parentFragmentManager, "StoreLeavingDialog")
        }



        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.equippedItems.collect { equippedList ->
                equippedList.forEach { equippedItem ->
                    updateEquipItemUi(equippedItem)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateEquipItemUi(item: CustomItem) {
        binding.ivStoreTurtle.visibility = View.INVISIBLE
        when (item.type) {
            ItemType.BACK -> {
                if (item.outfitImage is ItemImage.Url) {
                    binding.ivStoreTurtleBack.load(item.outfitImage.path)
                }
            }
            ItemType.SKIN -> {
                if (item.outfitImage is ItemImage.Url) {
                    binding.ivStoreTurtleSkin.load(item.outfitImage.path)
                }
            }
            ItemType.DECO_1 -> {
                if (item.outfitImage is ItemImage.Url) {
                    binding.ivStoreTurtleDeco1.load(item.outfitImage.path)
                }
            }
            ItemType.DECO_2 -> {
                if (item.outfitImage is ItemImage.Url) {
                    binding.ivStoreTurtleDeco2.load(item.outfitImage.path)
                }
            }
            ItemType.BACKGROUND -> {
                if (item.outfitImage is ItemImage.Url) {
                    binding.ivStoreBackground.load(item.outfitImage.path)
                }
            }
        }
    }

}



