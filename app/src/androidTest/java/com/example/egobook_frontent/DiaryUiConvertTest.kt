package com.example.egobook_frontent

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DiaryUiConvertTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test_diary_navigation_flow() {
        val sleepTime = 1000L

        // 1. 바텀 네비게이션 '감정일기' 클릭 -> DiaryFragment로 전환
        onView(withId(R.id.menu_diary)).perform(click())
        Thread.sleep(sleepTime)

        // DiaryFragment의 리사이클러뷰가 보이는지 확인
        onView(withId(R.id.rv_diary)).check(matches(isDisplayed()))
        Thread.sleep(sleepTime)

        // 2. '+' 버튼 클릭 -> DiaryWriteFragment로 전환
        onView(withId(R.id.btn_add)).perform(click())
        Thread.sleep(sleepTime)

        // DiaryWriteFragment의 일기 입력창이 보이는지 확인
        onView(withId(R.id.et_diary_content)).check(matches(isDisplayed()))
        Thread.sleep(sleepTime)

        // 3. 뒤로가기 버튼 클릭 -> DiaryFragment로 복귀
        onView(withId(R.id.btn_back)).perform(click())
        Thread.sleep(sleepTime)

        // 다시 DiaryFragment의 리사이클러뷰가 보이는지 확인
        onView(withId(R.id.rv_diary)).check(matches(isDisplayed()))
        Thread.sleep(sleepTime)

        // 4. 달력 버튼 클릭 -> CandlerFragment로 전환
        onView(withId(R.id.btn_calender)).perform(click())
        Thread.sleep(sleepTime)

        // CandlerFragment의 달력 뷰가 보이는지 확인
        onView(withId(R.id.calendarView)).check(matches(isDisplayed()))
        Thread.sleep(sleepTime)
    }
}