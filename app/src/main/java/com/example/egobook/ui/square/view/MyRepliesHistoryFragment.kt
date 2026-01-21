package com.example.egobook.ui.square.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.egobook.R
import com.example.egobook.databinding.FragmentMyRepliesHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyRepliesHistoryFragment : Fragment(R.layout.fragment_my_replies_history) {
    private lateinit var binding: FragmentMyRepliesHistoryBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyRepliesHistoryBinding.bind(view)
    }
}