package com.intecular.invis.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intecular.invis.base.ApiHeader
import com.intecular.invis.base.ext.formatApiHeader
import com.intecular.invis.data.ErrorHandlerDelegate
import com.intecular.invis.data.datastore.ProtoDataStore
import com.intecular.invis.data.db.entity.UserInfoEntity
import com.intecular.invis.data.entities.request.ChangePasswordRequest
import com.intecular.invis.data.entities.request.ResetPasswordRequest
import com.intecular.invis.data.repositories.AccountRepositories
import com.intecular.invis.data.repositories.UserInfoRepository
import com.slack.eithernet.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val accountRepositories: AccountRepositories,
    private val protoDataStore: ProtoDataStore,
    private val userInfoRepository: UserInfoRepository,
    errorHandlerDelegate: ErrorHandlerDelegate
) : ViewModel(), ErrorHandlerDelegate by errorHandlerDelegate {


    private val _showLoading = Channel<Boolean>()
    val showLoading = _showLoading.receiveAsFlow()

    private val _changeSuccessful = Channel<Boolean>()
    val changeSuccessful = _changeSuccessful.receiveAsFlow()

    private val _passwordNotMatch = Channel<Boolean>()
    val passwordNotMatch = _passwordNotMatch.receiveAsFlow()

    fun checkNewPassword(
        oldPassword: String,
        newPassword: String,
        verifyPassword: String,
    ) {
        viewModelScope.launch {
            _showLoading.send(true)
           val email = protoDataStore.getUserEmail().first()
            val userInfo = userInfoRepository.getUserInfo(email).first()
            if (newPassword == verifyPassword) {
                applyChangePassword(
                    ChangePasswordRequest(
                        userInfo.accessToken,
                        oldPassword,
                        newPassword
                        )
                )
            } else {
                _passwordNotMatch.send(true)
                _showLoading.send(false)
            }
        }

    }

    private fun applyChangePassword(changePasswordRequest: ChangePasswordRequest) {
        viewModelScope.launch {
            _showLoading.send(true)
            protoDataStore.saveApiHeader(ApiHeader.CHANGE_PASSWORD.content.formatApiHeader())
            val userEmail = protoDataStore.getUserEmail().first()
            val userInfo = userInfoRepository.getUserInfo(userEmail).first()
            when (val result =
                accountRepositories.changePassword(changePasswordRequest.copy(accessToken = userInfo.accessToken))) {
                is ApiResult.Success -> {
                    userInfoRepository.deleteUserInfo(userInfo)
                    protoDataStore.saveUserEmail("")
                    _changeSuccessful.send(true)
                    _showLoading.send(false)
                }

                is ApiResult.Failure -> {
                    _showLoading.send(false)
                    extractErrorMessage(result)
                }
            }
        }
    }

    fun reductionPasswordNotMatchStatus() {
        viewModelScope.launch {
            _passwordNotMatch.send(false)
        }
    }

}