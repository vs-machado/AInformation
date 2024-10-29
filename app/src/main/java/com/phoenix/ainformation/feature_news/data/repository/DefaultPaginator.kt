package com.phoenix.ainformation.feature_news.data.repository

import com.phoenix.ainformation.feature_news.domain.model.Paginator

/*
 * Generic paginator class that handles pagination logic
 */
class DefaultPaginator<Key, Item> (
    private val initialKey: Key,
    private inline val onLoadUpdated: (Boolean) -> Unit,
    private inline val onRequest: suspend (nextKey: Key) -> Result<List<Item>>,
    private inline val getNextKey: suspend (List<Item>) -> Key,
    private inline val onError: suspend (Throwable?) -> Unit,
    private inline val onSuccess: suspend (items: List<Item>, newKey: Key) -> Unit
): Paginator<Key, Item> {

    private var currentKey = initialKey
    private var isMakingRequest = false

    override suspend fun loadNextItems() {
        // Prevent multiple simultaneous requests
        if(isMakingRequest) {
            return
        }

        isMakingRequest = true
        onLoadUpdated(true)
        val result = onRequest(currentKey)
        isMakingRequest = false

        val items = result.getOrElse {
            onError(it)
            onLoadUpdated(false)
            return
        }
        currentKey = getNextKey(items) // Update the key for the next request
        onSuccess(items, currentKey)
        onLoadUpdated(false)
    }

    override fun reset() {
        currentKey = initialKey
    }
}