    package com.egobook.app.ui.diary.view

    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.fragment.app.Fragment
    import androidx.fragment.app.activityViewModels
    import androidx.navigation.fragment.findNavController
    import com.egobook.app.BlurLevel
    import com.egobook.app.R
    import com.egobook.app.applyScreenBlur
    import com.egobook.app.databinding.FragmentDiaryBinding
    import com.egobook.app.ui.diary.adapter.DiaryVPAdapter
    import com.egobook.app.ui.diary.viewmodel.DiariesEvent
    import com.egobook.app.ui.diary.viewmodel.DiariesViewModel
    import com.google.android.material.tabs.TabLayout
    import com.google.android.material.tabs.TabLayoutMediator
    import kotlin.getValue

    class DiaryFragment : Fragment() {
        private var _binding: FragmentDiaryBinding? = null
        private val binding get() = _binding!!

        private val viewModel: DiariesViewModel by activityViewModels()

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?
        ): View {
            _binding = FragmentDiaryBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            //뷰페이저 어댑터 설정
            initViewPager()

            //버튼 클릭 리스너
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

            // 탭 선택 이벤트 처리
            binding.tbType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val types = getDiaryTypesByPosition(tab.position)
                    viewModel.onEvent(DiariesEvent.SwipeTab(types))
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }

        private fun getDiaryTypesByPosition(position: Int): Set<com.egobook.app.domain.model.DiaryType>? {
            return when(position) {
                0 -> null // 전체
                1 -> setOf(com.egobook.app.domain.model.DiaryType.EMOTION)
                2 -> setOf(com.egobook.app.domain.model.DiaryType.WORRY)
                3 -> setOf(com.egobook.app.domain.model.DiaryType.PRAISE)
                4 -> setOf(com.egobook.app.domain.model.DiaryType.THANKS)
                else -> null
            }
        }


        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }