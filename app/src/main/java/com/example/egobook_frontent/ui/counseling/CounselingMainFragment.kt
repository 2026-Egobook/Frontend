package com.example.egobook_frontent.ui.counseling

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.egobook_frontent.R
import com.example.egobook_frontent.databinding.FragmentCounselingMainBinding

private lateinit var binding: FragmentCounselingMainBinding
class CounselingMainFragment : Fragment(R.layout.fragment_counseling_main) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCounselingMainBinding.bind(view)
    }

}