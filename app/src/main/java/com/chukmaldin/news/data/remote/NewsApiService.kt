package com.chukmaldin.news.data.remote

import com.chukmaldin.news.BuildConfig
import retrofit2.http.Query
import retrofit2.http.GET

interface NewsApiService {

    @GET("v2/everything?apiKey=${BuildConfig.NEWS_API_KEY}")
    suspend fun loadArticles(
        @Query("q") topic: String,
        @Query("language") language: String
    ): NewsResponseDto
}