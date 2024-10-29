package com.phoenix.ainformation.feature_news.presentation.main_screen

import com.phoenix.ainformation.feature_news.domain.model.news_api.NewsArticle

sealed class ApiFeedState {
    object Initial : ApiFeedState()
    object Loading : ApiFeedState()
    data class Success(val items: List<NewsArticle>): ApiFeedState()
    data class Error(val message: String): ApiFeedState()
}