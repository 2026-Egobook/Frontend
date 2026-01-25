package com.egobook.app.ui.diary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.egobook.app.databinding.FragmentDiaryCheckBinding
import com.egobook.app.domain.model.Diary
import com.egobook.app.ui.diary.util.toDateTimeString
import com.egobook.app.ui.diary.util.toDayOfMonthString
import com.egobook.app.ui.diary.util.toMonthString
import com.egobook.app.ui.diary.util.toYearString
import com.egobook.app.ui.diary.viewmodel.DiaryCheckViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DiaryCheckFragment : Fragment() {

    private var _binding: FragmentDiaryCheckBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiaryCheckViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryCheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListener()
        observeDiary()
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        // 다른 버튼 리스너들은 여기에 추가
    }

    // ViewModel의 diary Flow를 직접 구독합니다.
    private fun observeDiary() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.diary.collectLatest { diary ->
                    if (diary != null) {
                        // diary 객체가 null이 아닐 때 UI를 업데이트합니다.
                        updateUi(diary)
                    } else {
                        // diary가 null이면 (데이터 로딩 실패 등) 사용자에게 알립니다.
                        Toast.makeText(requireContext(), "일기를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // UI를 업데이트하는 로직을 별도 함수로 분리합니다.
    private fun updateUi(diary: Diary) {
        binding.tvDiaryContent.text = diary.content
        binding.tvWrittenTime.text = diary.updatedAt.toDateTimeString() // 포맷팅 함수 사용
        binding.tvDate.text = "${diary.createdAt.toYearString()}년 ${diary.createdAt.toMonthString()}월 ${diary.createdAt.toDayOfMonthString()}일"

//        if (diary.emotionLevel != null) {
//            binding.ivEmotion.setImageResource(diary.emotionLevel.imageRes)
//            binding.ivEmotion.visibility = View.VISIBLE
//        } else {
//            binding.ivEmotion.visibility = View.GONE
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
