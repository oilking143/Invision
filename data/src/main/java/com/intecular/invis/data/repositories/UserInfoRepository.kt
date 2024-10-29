package com.intecular.invis.data.repositories

import com.intecular.invis.base.di.IoDispatcher
import com.intecular.invis.data.db.UserDao
import com.intecular.invis.data.db.entity.UserInfoEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserInfoRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val userDao: UserDao
) {

    suspend fun getUserInfo(userName: String) = withContext(ioDispatcher) {
        userDao.getUserInfo(userName)
    }

    suspend fun insertUserInfo(userInfoEntity: UserInfoEntity) = withContext(ioDispatcher) {
        userDao.insertUserInfo(userInfoEntity)
    }

    suspend fun deleteUserInfo(userInfoEntity: UserInfoEntity) = withContext(ioDispatcher) {
        userDao.deleteUserInfo(userInfoEntity)
    }

    suspend fun updateUserInfo(userInfoEntity: UserInfoEntity) = withContext(ioDispatcher) {
        userDao.updateUserInfo(userInfoEntity)
    }
}