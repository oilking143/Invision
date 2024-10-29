package com.intecular.invis.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intecular.invis.data.ErrorHandlerDelegate
import com.intecular.invis.base.ApiHeader
import com.intecular.invis.base.ext.formatApiHeader
import com.intecular.invis.data.datastore.ProtoDataStore
import com.intecular.invis.data.entities.request.ForgetPasswordRequest
import com.intecular.invis.data.repositories.AccountRepositories
import com.intecular.invis.signin.data.VerificationInfo
import com.slack.eithernet.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val accountRepositories: AccountRepositories,
    private val protoDataStore: ProtoDataStore,
    errorHandlerDelegate: ErrorHandlerDelegate
) : ViewModel(), ErrorHandlerDelegate by errorHandlerDelegate {

    private val _requestVerifyCodeSuccess = Channel<String>()
    val requestVerifyCodeSuccess = _requestVerifyCodeSuccess.receiveAsFlow()

    private val _showLoading = Channel<Boolean>()
    val showLoading = _showLoading.receiveAsFlow()

    fun forgotPassword(forgetPasswordRequest: ForgetPasswordRequest) {
        viewModelScope.launch {
            _showLoading.send(true)
            protoDataStore.saveApiHeader(ApiHeader.FORGOT_PASSWORD.content.formatApiHeader())
            when (val result = accountRepositories.forgetPassword(forgetPasswordRequest)) {
                is ApiResult.Success -> {
                    _requestVerifyCodeSuccess.send(forgetPasswordRequest.userName)
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