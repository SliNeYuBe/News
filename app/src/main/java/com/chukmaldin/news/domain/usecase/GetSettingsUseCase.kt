package com.chukmaldin.news.domain.usecase

import com.chukmaldin.news.domain.entity.Settings
import com.chukmaldin.news.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke() = settingsRepository.getSettings()
}