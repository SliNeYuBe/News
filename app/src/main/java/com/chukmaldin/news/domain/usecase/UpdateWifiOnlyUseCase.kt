package com.chukmaldin.news.domain.usecase

import com.chukmaldin.news.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateWifiOnlyUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(wifiOnly: Boolean) =
        settingsRepository.updateWifiOnly(wifiOnly)
}