package com.intecular.invis.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intecular.invis.data.ErrorHandlerDelegate
import com.intecular.invis.base.ApiHeader
import com.intecular.invis.base.ext.formatApiHeader
import com.intecular.invis.data.datastore.ProtoDataStore
import com.intecular.invis.data.db.entity.UserInfoEntity
import com.intecular.invis.data.entities.request.ConfirmSignupRequest
import com.intecular.invis.data.repositories.AccountRepositories
import com.intecular.invis.data.repositories.UserInfoRepository
import com.slack.eithernet.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val accountRepositories: AccountRepositories,
    private val userInfoRepository: UserInfoRepository,
    private val protoDataStore: ProtoDataStore,
    errorHandlerDelegate: ErrorHandlerDelegate
) : ViewModel(), ErrorHandlerDelegate by errorHandlerDelegate {

    private val _verifySuccess = Channel<Boolean>()
    val verifySuccess = _verifySuccess.receiveAsFlow()

    private val _showLoading = Channel<Boolean>()
    val showLoading = _showLoading.receiveAsFlow()

    fun confirmSignup(confirmSignupRequest: ConfirmSignupRequest) {
        viewModelScope.launch {
            _showLoading.send(true)
            protoDataStore.saveApiHeader(ApiHeader.CONFIRM_SIGN_UP.content.formatApiHeader())
            when (val result = accountRepositories.confirmSignup(
                confirmSignupRequest
            )) {
                is ApiResult.Success -> {
                    userInfoRepository.insertUserInfo(
                        UserInfoEntity(
                            confirmSignupRequest.userName
                        )
                    )
                    _verifySuccess.send(true)
                    _showLoading.send(false)
                }

                is ApiResult.Failure -> {
                    _verifySuccess.send(false)
                    _showLoading.send(false)
                    extractErrorMessage(result)

                }
            }
        }
    }
}