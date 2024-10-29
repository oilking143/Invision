package com.intecular.invis.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
data class UserInfoEntity(
    @PrimaryKey
    val userName: String,
    val refreshToken: String = "",
    val accessToken: String = ""
)
