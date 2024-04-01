@file:Suppress("DEPRECATION")

package com.example.githubproject

import com.example.githubproject.viewmodel.DarkModeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.mockito.Mockito.*

@Suppress("DEPRECATION")
@ExperimentalCoroutinesApi
class DarkModeViewModelTest {

    private lateinit var viewModel: DarkModeViewModel
    private lateinit var mockPref: SettingPreferences

    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockPref = mock(SettingPreferences::class.java)
        viewModel = DarkModeViewModel(mockPref)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testSaveThemeSetting() = testDispatcher.runBlockingTest {
        val isDarkModeActive = true
        viewModel.saveThemeSetting(isDarkModeActive)
        verify(mockPref).saveThemeSetting(isDarkModeActive)
        Assert.assertTrue(true)
    }
}

class CoroutineTestRule(private val testDispatcher: TestCoroutineDispatcher) : TestRule {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                testDispatcher./**/runBlockingTest {
                    base.evaluate()
                }
            }
        }
    }
}

