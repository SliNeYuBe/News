package com.chukmaldin.news.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SubscriptionDbModel::class, ArticleDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class NewsDatabase: RoomDatabase() {

    abstract fun newsDao(): NewsDao
}