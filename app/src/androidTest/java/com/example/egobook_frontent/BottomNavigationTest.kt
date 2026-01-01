package com.example.egobook_frontent

import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.egobook_frontent.ui.counseling.EgoRoomFragment
import com.example.egobook_frontent.ui.diary.DiaryFragment
import com.example.egobook_frontent.ui.home.HomeFragment
import com.example.egobook_frontent.ui.square.SquareFragment
import org.assertj.core.api.Assertions.assertThat

import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class BottomNavigationTest {
    @Test
    fun bottom_navigation_view_transition_test() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        val testCases = mapOf(
            R.id.menu_diary to DiaryFragment::class.java,
            R.id.menu_ego_room to EgoRoomFragment::class.java,
            R.id.menu_square to SquareFragment::class.java,
            R.id.menu_home to HomeFragment::class.java
        )
        testCases.forEach { (menuId, expectedClass) ->
            onView(withId(menuId)).perform(click())
            scenario.onActivity { activity ->
                val navHostFragment = activity.supportFragmentManager
                    .findFragmentById(R.id.fragment_container) as NavHostFragment

                val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
                assertThat(currentFragment).isInstanceOf(expectedClass)
            }
        }
        scenario.close()
    }
}
