package com.phoenix.ainformation.feature_news.domain.model.news_api.repository

import com.phoenix.ainformation.feature_news.domain.model.news_api.NewsArticle
import com.phoenix.ainformation.feature_news.domain.model.news_api.NewsResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface ApiNewsRepository {
    suspend fun getLatestNews(page: String? = null): Result<List<NewsArticle>>
}