package com.chukmaldin.news.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chukmaldin.news.domain.entity.Interval
import com.chukmaldin.news.domain.entity.Language
import com.chukmaldin.news.domain.usecase.GetSettingsUseCase
import com.chukmaldin.news.domain.usecase.UpdateIntervalUseCase
import com.chukmaldin.news.domain.usecase.UpdateLanguageUseCase
import com.chukmaldin.news.domain.usecase.UpdateNotificationsEnabledUseCase
import com.chukmaldin.news.domain.usecase.UpdateWifiOnlyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val updateLanguageUseCase: UpdateLanguageUseCase,
    private val updateIntervalUseCase: UpdateIntervalUseCase,
    private val updateNotificationEnabled: UpdateNotificationsEnabledUseCase,
    private val updateWifiOnly: UpdateWifiOnlyUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {


    private val _state = MutableStateFlow<SettingsState>(
        SettingsState.Initial
    )
    val state
        get() = _state.asStateFlow()

    init {
        getSettingsUseCase().onEach { settings ->
            _state.update {
                SettingsState.Configuration(
                    language = settings.language,
                    interval = settings.interval,
                    notificationsEnabled = settings.notificationsEnabled,
                    wifiOnly = settings.wifiOnly
                )
            }
        }.launchIn(viewModelScope)
    }

    fun processCommand(command: SettingsCommand) {
        viewModelScope.launch {
            when (command) {
                is SettingsCommand.Back -> {
                    _state.update {
                        SettingsState.Finished
                    }
                }

                is SettingsCommand.UpdateInterval -> {
                    updateIntervalUseCase(command.interval)
                }

                is SettingsCommand.UpdateLanguage -> {
                    updateLanguageUseCase(command.language)
                }

                is SettingsCommand.UpdateNotificationEnabled -> {
                    updateNotificationEnabled(command.enabled)
                }

                is SettingsCommand.UpdateWifiOnly -> {
                    updateWifiOnly(command.wifiOnly)
                }
            }
        }
    }

}

sealed interface SettingsCommand {

    data class UpdateLanguage(val language: Language) : SettingsCommand

    data class UpdateInterval(val interval: Interval) : SettingsCommand

    data class UpdateNotificationEnabled(val enabled: Boolean) : SettingsCommand

    data class UpdateWifiOnly(val wifiOnly: Boolean) : SettingsCommand

    data object Back : SettingsCommand
}

sealed interface SettingsState {

    data class Configuration(
        val language: Language,
        val interval: Interval,
        val notificationsEnabled: Boolean,
        val wifiOnly: Boolean,
        val languages: List<Language> = Language.entries,
        val intervals: List<Interval> = Interval.entries
    ) : SettingsState

    data object Finished : SettingsState

    data object Initial : SettingsState
}