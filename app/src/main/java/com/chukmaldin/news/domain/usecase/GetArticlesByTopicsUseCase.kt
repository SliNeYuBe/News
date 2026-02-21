package com.chukmaldin.news.domain.usecase

import com.chukmaldin.news.domain.repository.NewsRepository
import javax.inject.Inject

class GetArticlesByTopicsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

    operator fun invoke(topics: List<String>) =
        newsRepository.getArticlesByTopics(topics)
}