package com.phoenix.ainformation.feature_news.data.repository

import com.phoenix.ainformation.BuildConfig
import com.phoenix.ainformation.feature_news.data.api.NewsDataApiService
import com.phoenix.ainformation.feature_news.domain.model.news_api.NewsArticle
import com.phoenix.ainformation.feature_news.domain.model.news_api.repository.ApiNewsRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

// Fetch data from NewsData.io API
class ApiNewsRepositoryImpl @Inject constructor(
    private val api: NewsDataApiService
): ApiNewsRepository {

    override suspend fun getLatestNews(page: String?): Result<List<NewsArticle>> {
        return try {
            val response = api.getLatestNews(
                apiKey = BuildConfig.newsDataApiKey,
                nextPage = page
            )
            if(response.status == "success") {
                Result.success(response.results)
            } else Result.success(emptyList())

        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}