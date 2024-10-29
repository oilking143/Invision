package com.intecular.invis.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intecular.invis.base.ApiHeader
import com.intecular.invis.base.ext.formatApiHeader
import com.intecular.invis.data.ErrorHandlerDelegate
import com.intecular.invis.data.datastore.ProtoDataStore
import com.intecular.invis.data.db.entity.UserInfoEntity
import com.intecular.invis.data.entities.request.DeleteAccountRequest
import com.intecular.invis.data.entities.request.LogOutRequest
import com.intecular.invis.data.repositories.AccountRepositories
import com.intecular.invis.data.repositories.UserInfoRepository
import com.slack.eithernet.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val accountRepositories: AccountRepositories,
    private val protoDataStore: ProtoDataStore,
    private val userInfoRepository: UserInfoRepository,
    errorHandlerDelegate: ErrorHandlerDelegate
) : ViewModel(), ErrorHandlerDelegate by errorHandlerDelegate {

    private val _showLoading = Channel<Boolean>()
    val showLoading = _showLoading.receiveAsFlow()

    private val _logOutSuccess = Channel<Boolean>()
    val logOutSuccess = _logOutSuccess.receiveAsFlow()

    fun logOut() {
        viewModelScope.launch {
            _showLoading.send(true)
            protoDataStore.saveApiHeader(ApiHeader.LOGOUT.content.formatApiHeader())
            val userInfo = getUserInfo()
            when (val result = accountRepositories.logOut(LogOutRequest(userInfo.refreshToken))) {
                is ApiResult.Success -> {
                    clearAccountInfoAndLogout(userInfo)
                    _showLoading.send(false)
                }

                is ApiResult.Failure -> {
                    _showLoading.send(false)
                    extractErrorMessage(result)
                }
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _showLoading.send(true)
            protoDataStore.saveApiHeader(ApiHeader.DELETE_ACCOUNT.content.formatApiHeader())
            val userInfo = getUserInfo()
            when (val result =
                accountRepositories.deleteAccount(DeleteAccountRequest(userInfo.accessToken))) {
                is ApiResult.Success -> {
                    clearAccountInfoAndLogout(userInfo)
                    _showLoading.send(false)
                }

                is ApiResult.Failure -> {
                    if (result is ApiResult.Failure.NetworkFailure && result.error.message?.contains("End of input") == true) {
                        clearAccountInfoAndLogout(userInfo)
                        _logOutSuccess.send(true)
                    } else {
                        extractErrorMessage(result)
                    }
                    _showLoading.send(false)

                }
            }
        }
    }

    private suspend fun getUserInfo(): UserInfoEntity {
        val email = protoDataStore.getUserEmail().first()
        return userInfoRepository.getUserInfo(email).first()
    }

    private suspend fun clearAccountInfoAndLogout(userInfo: UserInfoEntity) {
        userInfoRepository.deleteUserInfo(userInfo)
        protoDataStore.saveUserEmail("")
        _logOutSuccess.send(true)
    }
}

