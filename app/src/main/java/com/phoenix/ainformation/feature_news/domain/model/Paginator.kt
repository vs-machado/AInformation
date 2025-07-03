package com.phoenix.ainformation.feature_news.domain.model

interface Paginator<Key, Item> {
    suspend fun loadNextItems()
    fun reset()
}