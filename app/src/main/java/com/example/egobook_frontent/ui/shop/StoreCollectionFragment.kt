package com.example.egobook_frontent.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.egobook_frontent.databinding.FragmentStoreCollectionBinding
import kotlin.getValue

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
        val viewModel: StoreViewModel by viewModels()
        val targetBundle = checkNotNull(arguments) { "구현 오류: 올바른 탭을 표시하기 위해 번들은 필수입니다"}
        val tabItem = checkNotNull(BundleCompat.getParcelable(targetBundle, ItemTab.BUNDLE_KEY, ItemTab::class.java)) {
            "구현 오류: 올바른 탭을 표시하기 위해 tabItem을 번들을 통해 넘겨야 합니다."
        }

        binding.rvItems.apply {
            adapter = ItemAdapter(viewModel.loadItems(tabItem.type))
            layoutManager = GridLayoutManager(context, 3)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
