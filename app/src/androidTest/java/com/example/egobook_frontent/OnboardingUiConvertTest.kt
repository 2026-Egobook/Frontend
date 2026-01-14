package com.example.egobook_frontent

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
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
class OnboardingUiConvertTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test_onboarding_flow_to_home_fragment() {
        val sleepTime = 1500L // 1.5초 대기

        // 앱이 시작되면 OnboardingContainerFragment의 뷰페이저가 보여야 함
        onView(withId(R.id.vp_onboarding)).check(matches(isDisplayed()))
        Thread.sleep(sleepTime)

        // 1. 온보딩 화면을 4번 왼쪽으로 스와이프하여 마지막 페이지까지 이동
        onView(withId(R.id.vp_onboarding)).perform(swipeLeft())
        Thread.sleep(sleepTime)

        onView(withId(R.id.vp_onboarding)).perform(swipeLeft())
        Thread.sleep(sleepTime)

        onView(withId(R.id.vp_onboarding)).perform(swipeLeft())
        Thread.sleep(sleepTime)

        onView(withId(R.id.vp_onboarding)).perform(swipeLeft())
        Thread.sleep(sleepTime)

        // 2. 마지막 화면(OnboardingFragment5)의 시작하기 버튼이 보이는지 확인
        onView(withId(R.id.btn_start_egobook)).check(matches(isDisplayed()))

        // 3. "에고북 시작하기" 버튼을 클릭
        onView(withId(R.id.btn_start_egobook)).perform(click())
        Thread.sleep(sleepTime)

        // 4. HomeFragment로 전환되었는지, 바텀 네비게이션이 보이는지 검증
        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()))

        // HomeFragment의 특정 뷰가 보이는지 확인하여 전환을 확신 (예: R.id.menu_home)
        onView(withId(R.id.menu_home)).check(matches(isDisplayed()))
    }
}