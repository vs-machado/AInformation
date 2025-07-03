package com.phoenix.ainformation.feature_news.data.api

import com.phoenix.ainformation.BuildConfig
import com.phoenix.ainformation.feature_news.domain.model.news_api.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

const val NEWS_API_BASE_URL = "https://newsdata.io/api/1/"

interface NewsDataApiService {

    @GET("latest")
    suspend fun getLatestNews(
        @Query("apikey") apiKey: String = BuildConfig.newsDataApiKey,
        @Query("q") query: String = "",
        @Query("page") nextPage: String? = null
    ): NewsResponse

    @GET("latest")
    suspend fun getSearchNews(
        @Query("apikey") apiKey: String = BuildConfig.newsDataApiKey,
        @Query("q") query: String,
        @Query("page") nextPage: String? = null
    ): NewsResponse
}