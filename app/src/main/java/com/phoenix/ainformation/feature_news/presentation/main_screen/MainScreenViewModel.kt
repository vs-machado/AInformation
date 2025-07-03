package com.phoenix.ainformation.feature_news.presentation.main_screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ai.GenerativeModel
import com.phoenix.ainformation.feature_news.data.repository.DefaultPaginator
import com.phoenix.ainformation.feature_news.domain.model.news_api.repository.ApiNewsRepository
import com.phoenix.ainformation.feature_news.domain.model.rss_feed.RssItem
import com.phoenix.ainformation.feature_news.domain.model.rss_feed.repository.RssNewsRepository
import com.phoenix.ainformation.feature_news.domain.util.Parser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * MainScreenViewModel is responsible for managing the state and business logic
 * for the main screen of the news app. It handles fetching and filtering news items,
 * pagination, search functionality, and generating AI summaries for news articles.
 *
 * @param apiNewsRepository The repository used to fetch news items.
 */
@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val rssNewsRepository: RssNewsRepository,
    private val apiNewsRepository: ApiNewsRepository,
    private val model: GenerativeModel
): ViewModel() {

    private val _rssFeedState = MutableStateFlow<RssFeedState>(RssFeedState.Initial)
    val rssFeedState: StateFlow<RssFeedState> = _rssFeedState.asStateFlow()

    // NewsData API state
    private val _newsStateFlow = MutableStateFlow<ApiFeedState>(ApiFeedState.Initial)
    val newsStateFlow: StateFlow<ApiFeedState> = _newsStateFlow.asStateFlow()


    // Verifies if the Gemini response was correctly fetched and handles the app actions.
    private val _aiSummaryState = MutableStateFlow<AISummaryState>(AISummaryState.Initial)
    val aiSummaryState: StateFlow<AISummaryState> = _aiSummaryState.asStateFlow()

    // Ensures that the AI summary displayed in the screen corresponds to the last generated AI response.
    private val _currentSummarizedItemId = MutableStateFlow("")
    val currentSummarizedItemId: StateFlow<String?> = _currentSummarizedItemId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    var state by mutableStateOf(ScreenState())

    // News feed filter.
    val filteredFeed = combine(newsStateFlow, searchQuery) { state, query ->
        when(state){
//            is RssFeedState.Success -> {
//                state.copy(items = state.items.filter { item ->
//                    item.title.contains(query, ignoreCase = true) ||
//                            item.description.contains(query, ignoreCase = true)
//                })
//            }

            is ApiFeedState.Success -> {
                state.copy(items = state.items.filter { item ->
                    item.title.contains(query, ignoreCase = true) ||
                            item.description.contains(query, ignoreCase = true)
                })
            }

            else -> state
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ApiFeedState.Initial)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Used to fetch the news feed during scrolling
    private val paginator = DefaultPaginator(
        initialKey = state.page,
        onLoadUpdated = {
            state = state.copy(isLoading = it)
        },
        onRequest = { nextPage ->
            rssNewsRepository.getItems(nextPage, 10)
        },
        getNextKey = {
            state.page + 1
        },
        onError = { error ->
            state = when(error){
                is UnknownHostException -> {
                    _rssFeedState.value = RssFeedState.Error("Connection error occurred")
                    state.copy(error = "Connection error occurred")
                }

                else -> {
                    state.copy(error = error?.localizedMessage)
                }
            }
        },
        onSuccess = { items, newKey ->
            state = state.copy(
                items = state.items + items,
                page = newKey,
                endReached = items.isEmpty(),
                error = null
            )
        }
    )

    /* When user is scrolling the feed the state is not changed to Loading to avoid showing
     * the circular progress indicator used when launching the app. */
    fun fetchRssFeed(isScrolling: Boolean) {
        viewModelScope.launch {
            if (!isScrolling) {
                _rssFeedState.value = RssFeedState.Loading
            }

            paginator.loadNextItems()

            // If there's an UnknownHostException during the news fetch the FeedState is Error (defined in paginator instance)
            if (state.error != null) {
                return@launch
            }

            val processedItems = Parser().extractImageUrls(state.items)
            _rssFeedState.value = RssFeedState.Success(processedItems)
        }
    }

    fun fetchApiLatestNews(isScrolling: Boolean) {
        viewModelScope.launch {
            if(!isScrolling) {
                _newsStateFlow.value = ApiFeedState.Loading
            }

           apiNewsRepository.getLatestNews()
               .fold(
                   onSuccess = { articles ->
                       _newsStateFlow.value = ApiFeedState.Success(articles)
                   },
                   onFailure = { error ->
                       _newsStateFlow.value = ApiFeedState.Error(error.message ?: "Unknown error occurred")
                   }
               )
        }
    }

    init {
        fetchApiLatestNews(isScrolling = false)
    }

    // Updates aiSummarizedState with the latest generated AI response
    fun generateAISummary(newsContent: String, userLanguage: String, itemId: String) {
        viewModelScope.launch {
            _aiSummaryState.value = AISummaryState.Loading
            _currentSummarizedItemId.value = itemId

            runCatching {
                val response = model.generateContent("Write a summary about this news in $userLanguage that not exceeds 15 lines. In the beginning of the text " +
                        "put the summary in the first line. Write it like a website report. Use paragraphs and indentation" +
                        "when needed. Ensure that the summary reduces the quantity of lines in at least 30% when the news has more than 20 lines." +
                        "If the author's name is in the news, include it in the first line by writing Publisher: (publisher name), then present the summary on the line below. ." +
                        "The news: $newsContent")

                if(response.candidates.isNotEmpty()){
                    response.text ?: "No summary generated"
                } else {
                    throw Exception("No valid response generated")
                }

            }.onSuccess { summary ->
                _aiSummaryState.value = AISummaryState.Success(summary)
            }.onFailure { error ->
                _aiSummaryState.value = AISummaryState.Error(error.message ?: "Unknown error occurred")
                _currentSummarizedItemId.value = ""
            }
        }
    }

    fun openWebPage(url: String, context: Context) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun resetAISummaryState() {
        _aiSummaryState.value = AISummaryState.Initial
        _currentSummarizedItemId.value = ""
    }

    // Used to handle lazycolumn pagination
    data class ScreenState(
        val isLoading: Boolean = false,
        val items: List<RssItem> = emptyList(),
        val error: String? = null,
        val endReached: Boolean = false,
        val page: Int = 0
    )
}