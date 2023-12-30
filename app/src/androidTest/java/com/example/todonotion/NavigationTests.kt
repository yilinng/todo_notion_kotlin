package com.example.todonotion

import android.util.Log
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.ViewPagerActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.todonotion.ui.TodoListFragment
import com.example.todonotion.ui.TodoSearchFragment
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class NavigationTests {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    @Test
    fun navigate_to_todoDetail_nav_component(){
        //https://developer.android.com/training/testing/espresso
        onView(withText("Home Page")).check(matches(isDisplayed()))

        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        val todoScenario = launchFragmentInContainer<TodoListFragment>()

        todoScenario.onFragment { fragment ->

            navController.setGraph(R.navigation.nav_graph)

            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.tabLayout)).check(matches(isDisplayed()))

        //onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()))


        //https://github.com/android/testing-samples/blob/main/ui/espresso/RecyclerViewSample/app/src/androidTest/java/com/example/android/testing/espresso/RecyclerViewSample/RecyclerViewSampleTest.java

        //onView(withId(R.id.bottom_navigation)).perform(click())
        // Verify that performing a click changes the NavController’s state
       // onData(anything()).inAdapterView(withId(R.id.bottom_navigation)).atPosition(1).perform(click())
        //assertEquals(navController.currentDestination?.id, R.id.todoSearchFragment)

        /*
        onView(withId(R.id.recycler_view))
            .perform(
                RecyclerViewActions
                    .actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
       */

      //  Log.d("nav_detail", navController.currentDestination?.id.toString())

    }

}