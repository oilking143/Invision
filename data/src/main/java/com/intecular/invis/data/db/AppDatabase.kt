package com.intecular.invis.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.intecular.invis.data.db.entity.UserInfoEntity


@Database(entities = [UserInfoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}