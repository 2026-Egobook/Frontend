package com.egobook.app.store.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.egobook.app.store.ui.StoreCollectionFragment

class StoreCollectionAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        val fragment = StoreCollectionFragment()
        fragment.arguments = Bundle().apply {
            putParcelable(ItemTab.BUNDLE_KEY, ItemTab.of(position))
        }
        return fragment
    }

    override fun getItemCount() = ItemTab.entries.count()

}
