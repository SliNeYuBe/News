package com.chukmaldin.news.presentation.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chukmaldin.news.R
import com.chukmaldin.news.domain.entity.Interval
import com.chukmaldin.news.domain.entity.Language

@Composable
fun Language.displayLanguage(): String {
    return when(this) {
        Language.ENGLISH -> "English"
        Language.RUSSIAN -> "Русский"
        Language.FRENCH -> "Français"
        Language.GERMAN -> "Deutsch"
    }
}

@Composable
fun Interval.displayInterval(): String {
    return when {
        minutes < 60 -> stringResource(R.string.minutes, minutes)
        minutes == 60 -> stringResource(R.string.hour, minutes / 60)
        else -> stringResource(R.string.hours, minutes / 60)
    }
}