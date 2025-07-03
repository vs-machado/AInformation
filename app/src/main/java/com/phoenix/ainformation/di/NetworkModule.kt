package com.phoenix.ainformation.di

import com.phoenix.ainformation.feature_news.data.api.NEWS_API_BASE_URL
import com.phoenix.ainformation.feature_news.data.api.NewsDataApiService
import com.phoenix.ainformation.feature_news.data.api.RSS_BASE_URL
import com.phoenix.ainformation.feature_news.data.api.RssApiService
import com.phoenix.ainformation.feature_news.data.repository.ApiNewsRepositoryImpl
import com.phoenix.ainformation.feature_news.data.repository.RssNewsRepositoryImpl
import com.phoenix.ainformation.feature_news.domain.model.news_api.repository.ApiNewsRepository
import com.phoenix.ainformation.feature_news.domain.model.rss_feed.repository.RssNewsRepository
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RssRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NewsApiRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    @RssRetrofit
    fun provideRssRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(RSS_BASE_URL)
            .addConverterFactory(TikXmlConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @NewsApiRetrofit
    fun provideNewsApiRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NEWS_API_BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideRssApiService(@RssRetrofit retrofit: Retrofit): RssApiService {
        return retrofit.create(RssApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsApiService(@NewsApiRetrofit retrofit: Retrofit): NewsDataApiService {
        return retrofit.create(NewsDataApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(apiService: RssApiService): RssNewsRepository {
        return RssNewsRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideNewsApiRepository(newsDataApiService: NewsDataApiService): ApiNewsRepository {
        return ApiNewsRepositoryImpl(newsDataApiService)
    }
}