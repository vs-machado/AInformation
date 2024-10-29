package com.phoenix.ainformation.feature_news.presentation.main_screen

import com.phoenix.ainformation.feature_news.domain.model.rss_feed.RssItem


sealed class RssFeedState {
    object Initial: RssFeedState()
    object Loading: RssFeedState()
    data class Success(val items: List<RssItem>): RssFeedState()
    data class Error(val message: String): RssFeedState()
}