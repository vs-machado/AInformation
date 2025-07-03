package com.phoenix.ainformation.feature_news.common

sealed class Result<out T> {
    data class Success<T>(val data: T): Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()

    fun isLoading() = this is Loading
    fun isSuccessful() = this is Success
    fun isError() = this is Error
}