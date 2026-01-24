package com.example.egobook_frontent

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.egobook_frontent.ui.login.view.LoginActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginConvertTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    //테스트를 시작할 액티비티를 LoginActivity로 지정
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun test_login_flow_to_main_activity() {
        val sleepTime = 1500L

        // 1. 앱이 시작되고 LoginActivity의 로그인 버튼이 보이는지 확인
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
        Thread.sleep(sleepTime)

        // 2. "로그인" 버튼을 클릭하여 바텀시트를 띄움
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(sleepTime)

        // 3. 바텀시트의 "Google계정으로 로그인" 버튼이 보이는지 확인
        onView(withId(R.id.btn_bottom_google_login)).check(matches(isDisplayed()))
        Thread.sleep(sleepTime)

        // 4. "Google계정으로 로그인" 버튼을 클릭
        onView(withId(R.id.btn_bottom_google_login)).perform(click())
        Thread.sleep(sleepTime)

        // 5. MainActivity로 전환되었는지 검증
        onView(withId(R.id.main)).check(matches(isDisplayed()))
    }
}