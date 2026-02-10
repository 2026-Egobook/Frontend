package com.egobook.app.ui.shop

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.egobook.app.databinding.FragmentStoreCollectionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class StoreCollectionFragment(): Fragment() {
    private var _binding: FragmentStoreCollectionBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment가 제거되었습니다." }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreCollectionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: StoreViewModel by activityViewModels()
        val targetBundle = checkNotNull(arguments) { "구현 오류: 올바른 탭을 표시하기 위해 번들은 필수입니다"}
        val tabItem = checkNotNull(BundleCompat.getParcelable(targetBundle, ItemTab.BUNDLE_KEY, ItemTab::class.java)) {
            "구현 오류: 올바른 탭을 표시하기 위해 tabItem을 번들을 통해 넘겨야 합니다."
        }

        val itemAdapter = ItemAdapter { customItem ->
            viewModel.equipItem(customItem)
        }

        binding.rvItems.apply {
            adapter = itemAdapter
            layoutManager = GridLayoutManager(context, 3)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.itemStates.collect { newItems ->
                    itemAdapter.submitList(newItems[tabItem.type])
                }
            }
        }
        viewModel.loadItems(tabItem.type)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
