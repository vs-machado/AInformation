package com.phoenix.ainformation.feature_news.presentation.main_screen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.common.truth.Truth.assertThat
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.Candidate
import com.google.firebase.ai.type.GenerateContentResponse
import com.phoenix.ainformation.feature_news.data.repository.FakeNewsRepository
import com.phoenix.ainformation.feature_news.domain.model.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class MainScreenViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock private lateinit var model: GenerativeModel
    @Mock private lateinit var generateContentResponse: GenerateContentResponse
    @Mock private lateinit var candidate: Candidate

    private lateinit var state: MutableState<MainScreenViewModel.ScreenState>
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var newsRepository: NewsRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        newsRepository = FakeNewsRepository()
        viewModel = MainScreenViewModel(newsRepository, model)
        state = mutableStateOf(MainScreenViewModel.ScreenState(isLoading = false, endReached = false))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `aiSummaryState is Success at the end of the generateAISummary method`() = runTest {
        val newsContent =
            "Gov. Greg Abbott can only grant clemency after receiving a recommendation from the board. " +
                    "Abbott does have the power to grant a one-time 30-day reprieve without a board recommendation."
        val userLanguage = "English"
        val itemId = "123"
        val expectedSummary =
            "Gov. Greg Abbott can only grant clemency after receiving a recommendation from the board."

        `when`(model.generateContent(anyString())).thenReturn(generateContentResponse)
        `when`(generateContentResponse.candidates).thenReturn(listOf(candidate))
        `when`(generateContentResponse.text).thenReturn(expectedSummary)

        assertThat(viewModel.aiSummaryState.value).isInstanceOf(AISummaryState.Initial::class.java)

        viewModel.generateAISummary(newsContent, userLanguage, itemId)
        advanceUntilIdle()

        val aiSummaryState = viewModel.aiSummaryState.value
        assertThat(aiSummaryState).isEqualTo(AISummaryState.Success(expectedSummary))
    }

    @Test
    fun `aiSummaryState is Error when generateContentResponse candidates list is empty`() = runTest {
        val newsContent =
            "Gov. Greg Abbott can only grant clemency after receiving a recommendation from the board. " +
                    "Abbott does have the power to grant a one-time 30-day reprieve without a board recommendation."
        val userLanguage = "English"
        val itemId = "123"
        val expectedSummary =
            "Gov. Greg Abbott can only grant clemency after receiving a recommendation from the board."

        `when`(model.generateContent(anyString())).thenReturn(generateContentResponse)
        `when`(generateContentResponse.candidates).thenReturn(emptyList())
        `when`(generateContentResponse.text).thenReturn(expectedSummary)

        assertThat(viewModel.aiSummaryState.value).isInstanceOf(AISummaryState.Initial::class.java)

        viewModel.generateAISummary(newsContent, userLanguage, itemId)
        advanceUntilIdle()

        val aiSummaryState = viewModel.aiSummaryState.value
        assertThat(aiSummaryState).isInstanceOf(AISummaryState.Error::class.java)
    }

}