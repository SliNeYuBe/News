package com.chukmaldin.news.presentation.screen.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chukmaldin.news.domain.entity.Article
import com.chukmaldin.news.domain.usecase.AddSubscriptionUseCase
import com.chukmaldin.news.domain.usecase.ClearAllArticlesUseCase
import com.chukmaldin.news.domain.usecase.GetAllSubscriptionsUseCase
import com.chukmaldin.news.domain.usecase.GetArticlesByTopicsUseCase
import com.chukmaldin.news.domain.usecase.RemoveSubscriptionUseCase
import com.chukmaldin.news.domain.usecase.UpdateSubscribedArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val addSubscriptionUseCase: AddSubscriptionUseCase,
    private val clearAllArticlesUseCase: ClearAllArticlesUseCase,
    private val getAllSubscriptionsUseCase: GetAllSubscriptionsUseCase,
    private val getArticlesByTopicsUseCase: GetArticlesByTopicsUseCase,
    private val removeSubscriptionUseCase: RemoveSubscriptionUseCase,
    private val updateSubscribedArticlesUseCase: UpdateSubscribedArticlesUseCase
): ViewModel() {

    private val _state = MutableStateFlow(SubscriptionsState())
    val state = _state.asStateFlow()

    // Инициализировать нужно после state.
    // Если инициализировать до state, то программа упадет с ошибкой, так как state еще не будет создан
    init {
        observeSubscriptions()
        observeSelectedTopics()
    }

    fun processCommand(command: SubscriptionsCommand) {
        when(command) {
            SubscriptionsCommand.ClearArticles -> {
                viewModelScope.launch {
                    val topics = state.value.selectedTopics
                    clearAllArticlesUseCase(topics)
                }
            }
            SubscriptionsCommand.ClickSubscribe -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        val topic = previousState.query.trim()
                        addSubscriptionUseCase(topic)
                        previousState.copy(query = "")
                    }
                }
            }
            is SubscriptionsCommand.InputTopic -> {
                _state.update { previousState ->
                    previousState.copy(query = command.query)
                }
            }
            SubscriptionsCommand.RefreshData -> {
                viewModelScope.launch {
                    updateSubscribedArticlesUseCase()
                }
            }
            is SubscriptionsCommand.RemoveSubscription -> {
                viewModelScope.launch {
                    removeSubscriptionUseCase(command.topic)
                }
            }
            is SubscriptionsCommand.ToggleTopicSelection -> {
                _state.update { previousState ->
                    previousState.copy(
                        subscriptions = previousState.subscriptions.mapValues { (key, value) ->
                            if (key == command.topic) {
                                !value
                            } else {
                                value
                            }
                        }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSelectedTopics() {
        state.map { it.selectedTopics }
            // distinctUntilChanged() - Если selectedTopics изменился, то код будет продолжаться, иначе нет
            .distinctUntilChanged()
            // Меняет и закрывает только те flow, которые внутри flatMapLatest
            .flatMapLatest {
                getArticlesByTopicsUseCase(it)
            }
            // Получает flow базы данных. Теперь при изменении БД будет поступать новый обработанный List<Article>.
            // Каждый из них мы будем еще сами обрабатывать в .onEach()
            .onEach {
                _state.update { previousState ->
                    previousState.copy(articles = it)
                }
            // Подписываемся на изменения
            }.launchIn(viewModelScope)
    }

    private fun observeSubscriptions() {
        getAllSubscriptionsUseCase()
            .onEach { subscriptions ->
                _state.update { previousState ->
                    val updatedTopics = subscriptions.associateWith { topic ->
                        previousState.subscriptions[topic] ?: true
                    }
                    previousState.copy(subscriptions = updatedTopics)
                }
            }.launchIn(viewModelScope)
    }
}

// При всех этих командах изменяется состояние экрана
sealed interface SubscriptionsCommand {

    data class InputTopic(val query: String): SubscriptionsCommand

    data object ClickSubscribe: SubscriptionsCommand

    data object RefreshData: SubscriptionsCommand

    data class ToggleTopicSelection(val topic: String): SubscriptionsCommand

    data object ClearArticles: SubscriptionsCommand

    data class RemoveSubscription(val topic: String): SubscriptionsCommand
}

// Без sealed class так как экран всегда находится в одном состоянии, но с разными данными
data class SubscriptionsState(
    val query: String = "",
    val subscriptions: Map<String, Boolean> = mapOf(),
    val articles: List<Article> = listOf()
) {

    val subscribeButtonEnabled: Boolean
        get() = query.isNotBlank()

    val selectedTopics: List<String>
        get() = subscriptions.filter { it.value }.map { it.key }
}