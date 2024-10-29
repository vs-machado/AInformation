package com.phoenix.ainformation.feature_news.domain.model.rss_feed.repository

import com.phoenix.ainformation.feature_news.domain.model.rss_feed.RssItem

interface RssNewsRepository {
    suspend fun getItems(page: Int, pageSize: Int): Result<List<RssItem>>
}