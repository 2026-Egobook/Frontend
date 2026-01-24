package com.example.egobook_frontent.ui.diary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.egobook_frontent.BlurLevel
import com.example.egobook_frontent.applyScreenBlur
import com.example.egobook_frontent.databinding.FragmentDiaryCheckBinding

class DiaryCheckFragment : Fragment() {

    private var _binding: FragmentDiaryCheckBinding? = null
    private val binding get() = _binding!!
    var onDismissListener: (() -> Unit)? = null

    // 💡 1. by navArgs()를 사용하여 전달받은 인자를 가져옵니다.
    private val args: DiaryCheckFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryCheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //임시 텍스트 대신, 전달받은 args의 데이터를 사용합니다.
        binding.tvDiaryContent.text = args.diaryContent
        binding.tvWrittenTime.text = args.diaryTime

        setClickListener()

    }

    private fun setClickListener() {
        binding.apply{
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnDelete.setOnClickListener {
                applyScreenBlur(BlurLevel.BASE)
                val dialog = DiaryDeleteDialogFragment()
                dialog.isCancelable = true
                dialog.show(parentFragmentManager, "ConfirmDialog")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
