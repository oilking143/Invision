package com.intecular.invis.data.db

import android.service.autofill.UserData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.intecular.invis.data.db.entity.UserInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_info WHERE userName =:userName")
    fun getUserInfo(userName: String): Flow<UserInfoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInfo(vararg userInfoEntity: UserInfoEntity)

    @Update
    suspend fun updateUserInfo(userInfoEntity: UserInfoEntity)

    @Delete
    suspend fun deleteUserInfo(userInfoEntity: UserInfoEntity)
}