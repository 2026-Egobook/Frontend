package com.egobook.app.ui.square.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.egobook.app.R
import com.egobook.app.databinding.FragmentMyLettersBinding
import com.egobook.app.ui.square.adapter.MySentLettersAdapter
import com.egobook.app.ui.square.viewmodel.LetterViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyLettersFragment : Fragment(R.layout.fragment_my_letters) {
    private lateinit var binding: FragmentMyLettersBinding
    private val adapter by lazy {
        MySentLettersAdapter { letterId ->
            val action = MyLettersFragmentDirections.actionMyLettersFragmentToMyLetterDetailFragment(letterId = letterId)
            findNavController().navigate(action)
        }
    }

    private val viewModel: LetterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyLettersBinding.bind(view)
        fetchData()
        initViews()
        initListeners()
        initObservers()
    }

    private fun fetchData() {
        viewModel.getSentLetters(size = 10)
    }

    private fun initViews() = with(binding) {
        rvMySentLetters.adapter = adapter
    }

    private fun initListeners() = with(binding) {
        ivMyLettersBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.sentLetters.collectLatest { pagingData ->
                        if(pagingData != null) {
                            adapter.submitData(lifecycle, pagingData)
                        }
                    }
                }
            }
        }
    }
}