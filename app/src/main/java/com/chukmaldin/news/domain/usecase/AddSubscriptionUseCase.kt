package com.chukmaldin.news.domain.usecase

import android.util.Log
import com.chukmaldin.news.domain.repository.NewsRepository
import com.chukmaldin.news.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddSubscriptionUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(topic: String) {
        newsRepository.addSubscription(topic)
        // Обязательно запускать через контекст viewModel,
        // иначе запрос не будет удаляться из поиска до полной загрузки статей
        CoroutineScope(currentCoroutineContext()).launch {
            // Настройки лучше брать внутри скоупа, так как именно тут мы их и используем
            val settings = settingsRepository.getSettings().first()
            newsRepository.updateArticlesForTopic(topic, settings.language)
        }
    }
}