package com.example.egobook_frontent.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentStoreBinding
import com.google.android.material.tabs.TabLayoutMediator

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

        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_storeFragment_to_homeFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



