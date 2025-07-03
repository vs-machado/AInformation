package com.phoenix.ainformation.feature_news.data.api

import com.phoenix.ainformation.feature_news.domain.model.rss_feed.RssFeed
import retrofit2.http.GET

const val RSS_BASE_URL = "https://feedx.net"

interface RssApiService {

    @GET("/rss/ap.xml")
    suspend fun getRssFeed(): RssFeed
}