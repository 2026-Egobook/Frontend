package com.example.egobook_frontent.ui.diary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.egobook_frontent.databinding.FragmentDiaryBinding

class DiaryFragment : Fragment() {
    lateinit var binding: FragmentDiaryBinding
    private var diaryDatas = ArrayList<Diary>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDiaryBinding.inflate(inflater, container, false)

        return binding.root

    }
}