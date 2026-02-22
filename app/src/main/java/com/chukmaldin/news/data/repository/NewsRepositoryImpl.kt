package com.chukmaldin.news.data.repository

import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.chukmaldin.news.data.background.RefreshDataWorker
import com.chukmaldin.news.data.local.ArticleDbModel
import com.chukmaldin.news.data.local.NewsDao
import com.chukmaldin.news.data.local.SubscriptionDbModel
import com.chukmaldin.news.data.mapper.toDbModels
import com.chukmaldin.news.data.mapper.toEntities
import com.chukmaldin.news.data.remote.NewsApiService
import com.chukmaldin.news.domain.entity.Article
import com.chukmaldin.news.domain.entity.Language
import com.chukmaldin.news.domain.entity.RefreshConfig
import com.chukmaldin.news.domain.mapper.toQueryParam
import com.chukmaldin.news.domain.repository.NewsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val newsApiService: NewsApiService,
    private val workManager: WorkManager
): NewsRepository {
    override fun getAllSubscriptions(): Flow<List<String>> {
        return newsDao.getAllSubscriptions().map { subscriptions ->
            subscriptions.map { subscription ->
                subscription.topic
            }
        }
    }

    override suspend fun addSubscription(topic: String) {
        newsDao.addSubscription(SubscriptionDbModel(topic))
    }

    override suspend fun updateArticlesForTopic(topic: String, language: Language): Boolean {
        val articles = loadArticles(topic, language)
        val ids = newsDao.addArticles(articles)
        return ids.any { it != -1L }
    }

    private suspend fun loadArticles(topic: String, language: Language): List<ArticleDbModel> {
        return try {
            newsApiService.loadArticles(topic, language.toQueryParam()).toDbModels(topic)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Log.e("NewsRepository", e.stackTraceToString())
            listOf()
        }
    }

    override suspend fun removeSubscription(topic: String) {
        newsDao.deleteSubscription(SubscriptionDbModel(topic))
    }

    override suspend fun updateArticlesForAllSubscriptions(language: Language): List<String> {
        val updatedTopics = mutableListOf<String>()
        val subscriptions = newsDao.getAllSubscriptions().first()
        coroutineScope {
            subscriptions.forEach {
                launch {
                    val updated = updateArticlesForTopic(it.topic, language)
                    if (updated) {
                        withContext(Dispatchers.Main) {
                            updatedTopics.add(it.topic)
                        }
                    }
                }
            }
        }
        return updatedTopics
    }

    override fun getArticlesByTopics(topics: List<String>): Flow<List<Article>> {
        return newsDao.getAllArticlesByTopics(topics).map {
            it.toEntities()
        }
    }

    override fun startBackgroundRefresh(
        refreshConfig: RefreshConfig
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (refreshConfig.wifiOnly) {
                    NetworkType.UNMETERED
                } else {
                    NetworkType.CONNECTED
                }
            )
            .setRequiresBatteryNotLow(true)
            .build()

        val request = PeriodicWorkRequestBuilder<RefreshDataWorker>(
            refreshConfig.interval.minutes.toLong(), TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = "Refresh data",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request = request
        )
    }

    override suspend fun clearAllArticles(topics: List<String>) {
        newsDao.deleteArticlesByTopics(topics)
    }
}