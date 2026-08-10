package com.egobook.app.ui.home.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.egobook.app.R
import com.egobook.app.databinding.DialogStreakBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.home.repository.DayOfWeek
import com.egobook.app.ui.home.repository.MissionType
import com.egobook.app.ui.home.repository.UserActivityRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import timber.log.Timber

@AndroidEntryPoint
class StreakDialog : DialogFragment() {
    private var _binding: DialogStreakBinding? = null
    private val binding get() = checkNotNull(_binding) { "Fragment가 제거되었습니다." }

    @Inject
    lateinit var userActivityRepository: UserActivityRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogStreakBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    userActivityRepository.loadActivityRecord()
                }
                data.dailyCompletedMissions.forEach {
                    when (it) {
                        MissionType.WRITING_EMOTION_DIARY -> binding.ivStampDiary.visibility =
                            View.VISIBLE

                        MissionType.WRITING_LETTER -> binding.ivStampLetter.visibility =
                            View.VISIBLE

                        MissionType.ANSWERING_DAILY_QUESTION -> binding.ivStampTodayAnswer.visibility =
                            View.VISIBLE
                    }
                }

                data.weeklyCompletedDayOfWeek.forEach {
                    when (it) {
                        DayOfWeek.MONDAY -> checkDayOfWeekStreak(
                            binding.tvMonday,
                            binding.ivMondayCheck
                        )

                        DayOfWeek.TUESDAY -> checkDayOfWeekStreak(
                            binding.tvTuesday,
                            binding.ivTuesdayCheck
                        )

                        DayOfWeek.WEDNESDAY -> checkDayOfWeekStreak(
                            binding.tvWednesday,
                            binding.ivWednesdayCheck
                        )

                        DayOfWeek.THURSDAY -> checkDayOfWeekStreak(
                            binding.tvThursday,
                            binding.ivThursdayCheck
                        )

                        DayOfWeek.FRIDAY -> checkDayOfWeekStreak(
                            binding.tvFriday,
                            binding.ivFridayCheck
                        )

                        DayOfWeek.SATURDAY -> checkDayOfWeekStreak(
                            binding.tvSaturday,
                            binding.ivSaturdayCheck
                        )

                        DayOfWeek.SUNDAY -> checkDayOfWeekStreak(
                            binding.tvSunday,
                            binding.ivSundayCheck
                        )
                    }
                }

                val currentDayOfWeek = DayOfWeek.of(LocalDate.now().dayOfWeek.value - 1)
                val pastMissedDayOfWeeks =
                    DayOfWeek.entries.filter { !data.weeklyCompletedDayOfWeek.contains(it) && it.order < currentDayOfWeek.order }
                pastMissedDayOfWeeks.forEach {
                    when (it) {
                        DayOfWeek.MONDAY -> markImpossibleDayOfWeekStreak(
                            binding.tvMonday,
                            binding.ivMondayCheck
                        )

                        DayOfWeek.TUESDAY -> markImpossibleDayOfWeekStreak(
                            binding.tvTuesday,
                            binding.ivTuesdayCheck
                        )

                        DayOfWeek.WEDNESDAY -> markImpossibleDayOfWeekStreak(
                            binding.tvWednesday,
                            binding.ivWednesdayCheck
                        )

                        DayOfWeek.THURSDAY -> markImpossibleDayOfWeekStreak(
                            binding.tvThursday,
                            binding.ivThursdayCheck
                        )

                        DayOfWeek.FRIDAY -> markImpossibleDayOfWeekStreak(
                            binding.tvFriday,
                            binding.ivFridayCheck
                        )

                        DayOfWeek.SATURDAY -> markImpossibleDayOfWeekStreak(
                            binding.tvSaturday,
                            binding.ivSaturdayCheck
                        )

                        DayOfWeek.SUNDAY -> markImpossibleDayOfWeekStreak(
                            binding.tvSunday,
                            binding.ivSundayCheck
                        )
                    }
                }

            } catch (e: Exception) {
                Timber.e(e, "스트릭 정보 조회 실패")
            }
        }
        binding.ivClose.setOnClickListener {
            removeScreenBlur()
            dismiss()
        }
    }

    private fun checkDayOfWeekStreak(originalView: TextView, afterView: ImageView) {
        originalView.visibility = View.INVISIBLE
        afterView.visibility = View.VISIBLE
        afterView.setBackgroundResource(R.drawable.bg_steak_dialog_day_of_week)
        afterView.setImageResource(R.drawable.ic_activity_check)
    }

    private fun markImpossibleDayOfWeekStreak(originalView: TextView, afterView: ImageView) {
        originalView.visibility = View.INVISIBLE
        afterView.visibility = View.VISIBLE
        afterView.setBackgroundResource(R.drawable.bg_streak_dialog_day_of_week_unchecked)
        afterView.setImageResource(R.drawable.ic_streak_unchecked)

    }
}
