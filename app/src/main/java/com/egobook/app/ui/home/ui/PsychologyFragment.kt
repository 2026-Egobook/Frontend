package com.egobook.app.ui.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.egobook.app.R
import com.egobook.app.databinding.FragmentPsychologyBinding
import com.egobook.app.ui.home.PsychologyViewModel
import com.egobook.app.ui.home.repository.SavedPsychologyDto
import com.egobook.app.ui.shop.ItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PsychologyFragment(): Fragment() {
    private lateinit var binding: FragmentPsychologyBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPsychologyBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: PsychologyViewModel by activityViewModels()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dailyPhycologyDto.collect { dailyPhycologyDto ->
                binding.tvPsychologyContent.text = dailyPhycologyDto.knowledge.content
                binding.tvPsychologySource.text = dailyPhycologyDto.knowledge.source
                binding.tvPsychologyDate.text = dailyPhycologyDto.date
                if (dailyPhycologyDto.isBookmarked) {
                   binding.ivBookmark.setImageResource(R.drawable.ic_bookmark_clicked)
                } else {
                    binding.ivBookmark.setImageResource(R.drawable.ic_bookmark_unclicked)
                }
            }
        }
        binding.ivBookmark.setOnClickListener {
            if (viewModel.dailyPhycologyDto.value.isBookmarked) {
                viewModel.deletePsychology(viewModel.dailyPhycologyDto.value.knowledge.knowledgeId)
            } else {
                viewModel.savePsychology(viewModel.dailyPhycologyDto.value.knowledge.knowledgeId)
            }
        }
        binding.ivBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_psychologyFragment_to_homeFragment)
        }

        val itemAdapter = SavedPsychologyAdapter { savedPsychologyDto ->
            viewModel.deletePsychology(savedPsychologyDto.knowledgeId)
        }

        binding.rvSavedPsychologies.apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    if (isLoading) {
                        binding.pbLoading.visibility = View.VISIBLE
                        binding.vLoadingOverlay.visibility = View.VISIBLE
                    } else {
                        binding.pbLoading.visibility = View.GONE
                        binding.vLoadingOverlay.visibility = View.GONE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.savedPsychologies.collect { psychologies ->
                    itemAdapter.submitList(psychologies)
                }
            }
        }
    }
}
