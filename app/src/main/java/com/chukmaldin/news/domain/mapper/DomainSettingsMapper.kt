package com.chukmaldin.news.domain.mapper

import com.chukmaldin.news.domain.entity.Interval
import com.chukmaldin.news.domain.entity.Interval.MIN_15
import com.chukmaldin.news.domain.entity.Interval.entries
import com.chukmaldin.news.domain.entity.Language
import com.chukmaldin.news.domain.entity.Language.ENGLISH
import com.chukmaldin.news.domain.entity.Language.FRENCH
import com.chukmaldin.news.domain.entity.Language.GERMAN
import com.chukmaldin.news.domain.entity.Language.RUSSIAN
import com.chukmaldin.news.domain.entity.RefreshConfig
import com.chukmaldin.news.domain.entity.Settings

fun Settings.toRefreshConfig(): RefreshConfig {
    return RefreshConfig(
        language = language,
        interval = interval,
        wifiOnly = wifiOnly
    )
}

fun Int.toInterval(): Interval {
    return entries.find { it.minutes == this } ?: MIN_15
}

fun Language.toQueryParam(): String {
    return when(this) {
        ENGLISH -> "en"
        RUSSIAN -> "ru"
        FRENCH -> "fn"
        GERMAN -> "de"
    }
}