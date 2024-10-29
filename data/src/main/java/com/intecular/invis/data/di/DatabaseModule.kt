package com.intecular.invis.data.di

import android.content.Context
import androidx.room.Room
import com.intecular.invis.data.db.AppDatabase
import com.intecular.invis.data.db.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext applicationContext: Context) =
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app-database").build()

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
}