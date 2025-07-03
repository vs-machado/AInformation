package com.phoenix.ainformation.feature_news.data.repository

import com.phoenix.ainformation.feature_news.data.api.RssApiService
import com.phoenix.ainformation.feature_news.domain.model.rss_feed.RssItem
import com.phoenix.ainformation.feature_news.domain.model.rss_feed.repository.RssNewsRepository
import javax.inject.Inject

// Fetch data from RssFeed
class RssNewsRepositoryImpl @Inject constructor(
    private val rssApiService: RssApiService
): RssNewsRepository {

    // Fetch items from the rssfeed. pageSize defines how many items will be fetched on each method call.
    override suspend fun getItems(page: Int, pageSize: Int): Result<List<RssItem>> {
        return try {
            val rssFeed = rssApiService.getRssFeed()
            val itemsList = rssFeed.channel.items
            val startIndex = page * pageSize

            if(startIndex + pageSize <= itemsList.size) {
                Result.success(
                    itemsList.slice(startIndex until startIndex + pageSize)
                )
            } else Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }

    }
}