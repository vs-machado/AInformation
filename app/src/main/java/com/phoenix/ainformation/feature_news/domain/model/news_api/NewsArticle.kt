package com.phoenix.ainformation.feature_news.domain.model.news_api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val results: List<NewsArticle>,
    val nextPage: String?
)

@Serializable
data class NewsArticle(
    @SerialName("article_id") val articleId: String,
    val title: String,
    val link: String,
    @SerialName("source_id") val sourceId: String,
    @SerialName("source_url") val sourceUrl: String?,
    @SerialName("source_icon") val sourceIcon: String?,
    @SerialName("source_priority") val sourcePriority: Int,
    val keywords: List<String>?,
    val creator: List<String>?,
    @SerialName("image_url") val imageUrl: String?,
    @SerialName("video_url") val videoUrl: String?,
    val description: String,
    val pubDate: String,
    val pubDateTZ: String?,
    val content: String?,
    val country: List<String>?,
    val category: List<String>?,
    val language: String,
    @SerialName("ai_tag") val aiTag: List<String>?,
    val sentiment: String?,
    @SerialName("sentiment_stats") val sentimentStats: SentimentStats?,
    @SerialName("ai_region") val aiRegion: List<String>?,
    @SerialName("ai_org") val aiOrg: List<String>?,
    val duplicate: Boolean?,
    val coin: List<String>?
)

@Serializable
data class SentimentStats(
    val positive: Double,
    val negative: Double,
    val neutral: Double
)