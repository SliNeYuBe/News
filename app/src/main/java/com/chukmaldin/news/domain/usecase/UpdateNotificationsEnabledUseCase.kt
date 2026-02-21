package com.chukmaldin.news.domain.usecase

import com.chukmaldin.news.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateNotificationsEnabledUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(enabled: Boolean) =
        settingsRepository.updateNotificationsEnabled(enabled)
}