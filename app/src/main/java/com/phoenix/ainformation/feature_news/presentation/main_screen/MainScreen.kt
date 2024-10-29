package com.phoenix.ainformation.feature_news.presentation.main_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phoenix.ainformation.R
import com.phoenix.ainformation.feature_news.domain.model.news_api.NewsArticle
import com.phoenix.ainformation.feature_news.domain.model.rss_feed.RssItem
import com.phoenix.ainformation.feature_news.presentation.main_screen.components.FeedItem
import com.phoenix.ainformation.feature_news.presentation.main_screen.components.SearchBar

/**
 * The main screen shows a search bar to allow user to query specific news.
 * A news feed is automatically shown if the fetch was successful,
 * if there is an error during the fetch, it displays an error and
 * show a "Try again" button.
 */

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = hiltViewModel(),
    onNavigationToNewsDetail: (RssItem) -> Unit,
    onNavigationToAISummaryScreen: (RssItem, String) -> Unit
) {
    val feedState by viewModel.filteredFeed.collectAsState()
    val apiFeedState by viewModel.filteredFeed.collectAsState()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = WindowInsets.systemBars
                            .asPaddingValues()
                            .calculateTopPadding()
                    )
                    .padding(top = 16.dp)
            ) {
                Image(
                    painter = if(isSystemInDarkTheme())
                        painterResource(id = R.drawable.black_logo)
                    else painterResource(id = R.drawable.white_logo),
                    contentDescription = stringResource(R.string.app_logo),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .height(24.dp)
                        .width(200.dp)
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            var searchQuery by remember { mutableStateOf("") }

            SearchBar(
                onSearchTextChanged = { newQuery -> searchQuery = newQuery },
                horizontalPadding = 16.dp,
                verticalPadding = 8.dp,
                backgroundColor = MaterialTheme.colorScheme.surface,
                cursorBrushColor = SolidColor(MaterialTheme.colorScheme.secondary),
                handleColor = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
//                when (val state = feedState) {
//                    is RssFeedState.Initial -> {}
//                    is RssFeedState.Loading -> LoadingIndicator()
//                    is RssFeedState.Success -> {
//                        val filteredFeed = state.items.filter {
//                            it.title.contains(searchQuery, ignoreCase = true) ||
//                                    it.description.contains(searchQuery, ignoreCase = true)
//                        }
//
//                        FeedContent(
//                            feed = filteredFeed,
//                            onNavigationToNewsDetail = onNavigationToNewsDetail,
//                            onNavigationToAISummaryScreen = onNavigationToAISummaryScreen
//                        )
//                    }
//                    is RssFeedState.Error -> {
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.Center,
//                            modifier = Modifier.fillMaxSize()
//                        ) {
//                            Text(
//                                text = stringResource(R.string.connection_error),
//                                modifier = Modifier.padding(bottom = 16.dp),
//                                color = MaterialTheme.colorScheme.onBackground
//                            )
//                            Button(
//                                onClick = { viewModel.fetchApiLatestNews(isScrolling = false) },
//                                colors = ButtonDefaults.buttonColors(
//                                    contentColor = MaterialTheme.colorScheme.onSecondary,
//                                    containerColor = MaterialTheme.colorScheme.secondary
//                                )
//                            ) {
//                                Text(stringResource(R.string.try_again))
//                            }
//                        }
//                    }
//                }
                when(val state = apiFeedState) {
                    is ApiFeedState.Initial -> {}
                    is ApiFeedState.Loading -> LoadingIndicator()
                    is ApiFeedState.Success -> {
                        val filteredFeed = state.items.filter {
//                            it.title.contains(searchQuery, ignoreCase = true) ||
//                                    it.description.contains(searchQuery, ignoreCase = true)
//                        }
//
//                        FeedContent(
//                            feed = filteredFeed,
//                            onNavigationToNewsDetail = onNavigationToNewsDetail,
//                            onNavigationToAISummaryScreen = onNavigationToAISummaryScreen
//                        )
                    }
                    is ApiFeedState.Error -> {}
                }
            }
        }
    }
}

@Composable
fun FeedContent(
//    feed: List<RssItem>,
    feed: List<NewsArticle>,
    viewModel: MainScreenViewModel = hiltViewModel(),
    onNavigationToNewsDetail: (RssItem) -> Unit,
    onNavigationToAISummaryScreen: (RssItem, String) -> Unit
) {
    val lazyListState = rememberLazyListState()

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo }
            .collect { layoutInfo ->
                if (lazyListState.isScrollInProgress) {
                    if (layoutInfo.visibleItemsInfo.isNotEmpty()) {
                        val lastVisibleItem = layoutInfo.visibleItemsInfo.last()
                        val lastIndex = lazyListState.layoutInfo.totalItemsCount - 1

                        if (lastVisibleItem.index == lastIndex) {
                            viewModel.fetchApiLatestNews(isScrolling = true)
                        }
                    }
                }
            }
    }

    LazyColumn(state = lazyListState) {
        item {
            Text(
                text = stringResource(R.string.latest_news),
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 20.dp, top = 8.dp),
                color = MaterialTheme.colorScheme.onBackground // Using onBackground for text color
            )
        }
        items(feed) { item ->
            FeedItem(
                item = item,
                onItemClick = { onNavigationToNewsDetail(item) },
                onNavigateToAISummaryScreen = { summary ->
                    onNavigationToAISummaryScreen(item, summary)
                }
            )
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary) // Using secondary color for loading indicator
    }
}