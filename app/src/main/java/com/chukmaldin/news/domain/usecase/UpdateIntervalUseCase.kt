package com.chukmaldin.news.domain.usecase

import com.chukmaldin.news.domain.entity.Interval
import com.chukmaldin.news.domain.entity.Settings
import com.chukmaldin.news.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateIntervalUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(interval: Interval) =
        settingsRepository.updateInterval(interval.minutes)
}