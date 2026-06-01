package com.egobook.app.store.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.FragmentStoreBinding
import com.egobook.app.store.data.model.ItemType
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoreFragment: Fragment() {
    private var _binding: FragmentStoreBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment가 제거되었습니다." }
    private lateinit var viewPager: ViewPager2
    private val viewModel: StoreViewModel by activityViewModels()

    private var lastSelected = -1
    private var previewState = PreviewExpansionState.COLLAPSED

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreBinding.inflate(inflater)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_PREVIEW_STATE, previewState.name)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.getString(KEY_PREVIEW_STATE)?.let { name ->
            previewState = PreviewExpansionState.valueOf(name)
            if (previewState.isExpanded) togglePreviewExpanded(expanded = true)
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.initialize()
        }

        viewModel.loadInk()
        viewPager = binding.vp2StoreCollectionContainer
        viewPager.adapter = StoreCollectionAdapter(this)
        val tabLayout = binding.tlTabs
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = ItemTab.of(position).text
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == lastSelected) return
                lastSelected = position
                viewModel.clearPreviewItems()
                Log.d("StoreFragment", "페이지 변경 감지됨: $position, 임시 착용 아이템 초기화")
            }
        })

        binding.ivBack.setOnClickListener {
            applyScreenBlur(BlurLevel.BASE)
            val dialog = StoreLeavingDialog()
            dialog.isCancelable = false
            dialog.show(parentFragmentManager, "StoreLeavingDialog")
        }

        binding.tvPurchase.setOnClickListener {
            if(viewModel.loadPurchaseItem() != null) {
                applyScreenBlur(BlurLevel.BASE)
                val dialog = StorePurchasingItemDialog()
                dialog.isCancelable = false
                dialog.show(parentFragmentManager, "StorePurchasingItemDialog")
            }
        }

        binding.ivReset.setOnClickListener {
            viewModel.clearPreviewItems()
        }

        binding.ivExpand.setOnClickListener {
            previewState = previewState.toggle()
            togglePreviewExpanded(previewState.isExpanded)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ink.collect { ink ->
                    binding.tvInk.text = ink.value.toString()
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.equippedItems.collect { equippedList ->
                val typeToItem = equippedList.associateBy { it.type }
                ItemType.entries.forEach { type ->
                    updateEquipItemUi(type, typeToItem[type])
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    val visibility = if (isLoading) View.VISIBLE else View.GONE
                    binding.pbLoading.visibility = visibility
                    binding.vLoadingOverlay.visibility = visibility
                }
            }
        }

        observeViewModel()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateEquipItemUi(type: ItemType, item: CustomItem?) {
        val imagePath = (item?.outfitImage as? ItemImage.Url)?.path
        when (type) {
            ItemType.BACK -> {
                binding.ivStoreTurtleBack.load(imagePath)
            }
            ItemType.SKIN -> {
                binding.ivStoreTurtleSkin.load(imagePath)
            }
            ItemType.DECO_1 -> {
                if (imagePath?.contains("Default") == true) {
                    binding.ivStoreTurtleDeco1.load(null)
                } else {
                    binding.ivStoreTurtleDeco1.load(imagePath)
                }
            }
            ItemType.DECO_2 -> {
                if (imagePath?.contains("Default") == true) {
                    binding.ivStoreTurtleDeco2.load(null)
                } else {
                    binding.ivStoreTurtleDeco2.load(imagePath)
                }
            }
            ItemType.BACKGROUND -> {
                binding.ivStoreBackground.load(imagePath)
            }
            ItemType.LETTER_PAPER -> {}
        }
    }

    private fun togglePreviewExpanded(expanded: Boolean) {
        applyConstraints(expanded)
        val visibility = if (expanded) View.GONE else View.VISIBLE
        listOf(
            binding.ivTabLayoutBackground,
            binding.tlTabs,
            binding.vp2StoreCollectionContainer,
            binding.llCollectionController,
            binding.ivReset,
            binding.tvPurchase
        ).forEach { it.visibility = visibility }
        binding.ivExpand.setImageResource(
            if (expanded) R.drawable.arrow_in_icon else R.drawable.arrow_out_icon
        )
    }

    private fun applyConstraints(expanded: Boolean) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.root)

        if (expanded) {
            constraintSet.connect(R.id.iv_store_default_background, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            constraintSet.connect(R.id.iv_store_background, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            constraintSet.connect(R.id.iv_store_turtle, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.connect(R.id.iv_store_turtle, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            constraintSet.setMargin(R.id.iv_store_turtle, ConstraintSet.BOTTOM, 0)
            constraintSet.connect(R.id.iv_expand, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        } else {
            constraintSet.connect(R.id.iv_store_default_background, ConstraintSet.BOTTOM, R.id.vp2_store_collection_container, ConstraintSet.BOTTOM)
            constraintSet.connect(R.id.iv_store_background, ConstraintSet.BOTTOM, R.id.vp2_store_collection_container, ConstraintSet.BOTTOM)
            constraintSet.clear(R.id.iv_store_turtle, ConstraintSet.TOP)
            constraintSet.connect(R.id.iv_store_turtle, ConstraintSet.BOTTOM, R.id.iv_tab_layout_background, ConstraintSet.TOP)
            constraintSet.setMargin(R.id.iv_store_turtle, ConstraintSet.BOTTOM, 0)
            constraintSet.connect(R.id.iv_expand, ConstraintSet.BOTTOM, R.id.iv_tab_layout_background, ConstraintSet.TOP)
        }

        constraintSet.applyTo(binding.root)
    }

    companion object {
        private const val KEY_PREVIEW_STATE = "preview_state"
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.toastEvent.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
