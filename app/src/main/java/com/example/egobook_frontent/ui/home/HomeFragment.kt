package com.example.egobook_frontent.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { userState ->
                    binding.tvLevel.text = "Lv ${userState.level.number}"
                    binding.ivLevelType.setImageResource(userState.level.type.getResId())
                    binding.tvInk.text = "${userState.ink.value}"
                }
            }
        }
        binding.ivStore.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_storeFragment)
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
