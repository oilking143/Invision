package com.intecular.invis.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intecular.invis.data.ErrorHandlerDelegate
import com.intecular.invis.base.ApiHeader
import com.intecular.invis.base.ext.formatApiHeader
import com.intecular.invis.data.datastore.ProtoDataStore
import com.intecular.invis.data.db.entity.UserInfoEntity
import com.intecular.invis.data.entities.request.GetUserAttributeRequest
import com.intecular.invis.data.entities.request.LoginByAccountRequest
import com.intecular.invis.data.repositories.AccountRepositories
import com.intecular.invis.data.repositories.UserInfoRepository
import com.slack.eithernet.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountRepositories: AccountRepositories,
    private val userInfoRepository: UserInfoRepository,
    private val protoDataStore: ProtoDataStore,
    errorHandlerDelegate: ErrorHandlerDelegate
) : ViewModel(), ErrorHandlerDelegate by errorHandlerDelegate {

    private val _signInSuccess = Channel<String>()
    val signInSuccess = _signInSuccess.receiveAsFlow()

    private val _showLoading = Channel<Boolean>()
    val showLoading = _showLoading.receiveAsFlow()
    fun loginByAccount(loginByAccountRequest: LoginByAccountRequest) {
        viewModelScope.launch {
            _showLoading.send(true)
            protoDataStore.saveApiHeader(ApiHeader.LOGIN.content.formatApiHeader())
            when (val result = accountRepositories.loginByAccount(loginByAccountRequest)) {
                is ApiResult.Success -> {
                    val data = result.value.authenticationResult
                    getUserAttribute(
                        GetUserAttributeRequest(data.accessToken),
                        data.refreshToken,
                        data.accessToken
                    )
                }

                is ApiResult.Failure -> {
                    _showLoading.send(false)
                    extractErrorMessage(result)
                }
            }
        }
    }

    private fun getUserAttribute(
        getUserAttributeRequest: GetUserAttributeRequest,
        refreshToken: String,
        accessToken: String
    ) {
        viewModelScope.launch {
            protoDataStore.saveApiHeader(ApiHeader.GET_USER_INFO.content.formatApiHeader())
            when (val result = accountRepositories.getUserAttributes(getUserAttributeRequest)) {
                is ApiResult.Success -> {
                    val attributeList = result.value.userAttributesList
                    Timber.d("UserAttributes:${attributeList}")
                    val email =
                        attributeList.find { it.attributeName == "email" }?.attributeValue ?: ""
                    val name =
                        attributeList.find { it.attributeName == "name" }?.attributeValue ?: ""

                    if (attributeList.isNotEmpty() && email.isNotEmpty()) {
                        val userInfoEntity = UserInfoEntity(
                            email,
                            refreshToken,
                            accessToken
                        )
                        if (protoDataStore.getUserEmail().first().isEmpty()) {
                            userInfoRepository.insertUserInfo(userInfoEntity)
                        } else {
                            userInfoRepository.updateUserInfo(userInfoEntity)
                        }

                        protoDataStore.saveUserEmail(email)
                        _signInSuccess.send(name)
                    }
                    _showLoading.send(false)
                }

                is ApiResult.Failure -> {
                    _showLoading.send(false)
                    extractErrorMessage(result)
                }
            }
        }
    }
}