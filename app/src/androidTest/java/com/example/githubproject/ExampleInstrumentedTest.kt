package com.example.githubproject

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.githubproject.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testDataFetching() {
        Thread.sleep(2000)

        onView(withId(R.id.rvUsers)).check(matches(isDisplayed()))

        onView(withId(R.id.rvUsers)).check(matches(hasMinimumChildCount(1)))
    }
}
