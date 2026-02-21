package com.chukmaldin.news.domain.usecase

import com.chukmaldin.news.domain.repository.NewsRepository
import javax.inject.Inject

class RemoveSubscriptionUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

    suspend operator fun invoke(topic: String) = newsRepository.removeSubscription(topic)
}