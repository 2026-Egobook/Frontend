    package com.example.egobook_frontent.ui.diary.view

    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.fragment.app.Fragment
    import androidx.navigation.fragment.findNavController
    import com.example.egobook_frontent.BlurLevel
    import com.example.egobook_frontent.R
    import com.example.egobook_frontent.applyScreenBlur
    import com.example.egobook_frontent.databinding.FragmentDiaryBinding
    import com.example.egobook_frontent.ui.diary.adapter.DiaryVPAdapter
    import com.google.android.material.tabs.TabLayoutMediator

    class DiaryFragment : Fragment() {
        private var _binding: FragmentDiaryBinding? = null
        private val binding get() = _binding!!

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?
        ): View {
            _binding = FragmentDiaryBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // 2. 뷰페이저 어댑터 설정
            initViewPager()

            // 3. 버튼 클릭 리스너
            binding.apply {
                btnAdd.setOnClickListener {
                    findNavController().navigate(R.id.action_diaryFragment_to_diaryWriteFragment)
            }
                btnCalender.setOnClickListener {
                    findNavController().navigate(R.id.action_diaryFragment_to_candlerFragment)
                }
                btnExport.setOnClickListener {
                    applyScreenBlur(BlurLevel.BASE)
                    val dialog = DiaryExportDialogFragment()
                    dialog.isCancelable = true
                    dialog.show(parentFragmentManager, "ConfirmDialog")
                }
            }
        }

        private fun initViewPager() {
            val adapter = DiaryVPAdapter(this)
            binding.vpDiary.adapter = adapter

            val tabTitles = listOf("전체", "감정", "고민", "칭찬", "감사")

            TabLayoutMediator(binding.tbType, binding.vpDiary) { tab, position ->
                tab.text = tabTitles[position]
            }.attach()
        }


        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }