package com.chukmaldin.news.data.mapper

import com.chukmaldin.news.data.local.ArticleDbModel
import com.chukmaldin.news.data.remote.NewsResponseDto
import com.chukmaldin.news.domain.entity.Article
import java.text.SimpleDateFormat
import java.util.Locale

fun NewsResponseDto.toDbModels(topic: String): List<ArticleDbModel> {
    return articles.map {
        ArticleDbModel(
            title = it.title,
            description = it.description,
            url =  it.url,
            imageUrl = it.urlToImage,
            sourceName = it.source.name,
            topic =  topic,
            publishedAt = it.publishedAt.toTimestamp()
        )
    }
}

// Создает уникальные статьи
fun List<ArticleDbModel>.toEntities(): List<Article> {
    return map {
        Article(
            title = it.title,
            description = it.description,
            url =  it.url,
            imageUrl = it.imageUrl,
            sourceName = it.sourceName,
            publishedAt = it.publishedAt
        )
    }.distinct()
}

private fun String.toTimestamp(): Long {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    return dateFormatter.parse(this)?.time ?: System.currentTimeMillis()
}