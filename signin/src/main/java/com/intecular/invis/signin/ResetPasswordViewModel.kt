package com.intecular.invis.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intecular.invis.base.ApiHeader
import com.intecular.invis.base.ext.formatApiHeader
import com.intecular.invis.data.ErrorHandlerDelegate
import com.intecular.invis.data.datastore.ProtoDataStore
import com.intecular.invis.data.entities.request.ResetPasswordRequest
import com.intecular.invis.data.repositories.AccountRepositories
import com.slack.eithernet.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val accountRepositories: AccountRepositories,
    private val protoDataStore: ProtoDataStore,
    errorHandlerDelegate: ErrorHandlerDelegate
) : ViewModel(), ErrorHandlerDelegate by errorHandlerDelegate {

    private val _showLoading = Channel<Boolean>()
    val showLoading = _showLoading.receiveAsFlow()

    private val _passwordNotMatch = Channel<Boolean>()
    val passwordNotMatch = _passwordNotMatch.receiveAsFlow()

    private val _resetPasswordSuccess = Channel<Boolean>()
    val resetPasswordSuccess = _resetPasswordSuccess.receiveAsFlow()

    fun checkNewPassword(
        email: String,
        password: String,
        verifyPassword: String,
        confirmationCode: String
    ) {
        viewModelScope.launch {
            _showLoading.send(true)
            if (password == verifyPassword) {
                resetPassword(
                    ResetPasswordRequest(
                        email,
                        password,
                        confirmationCode
                    )
                )
            } else {
                _passwordNotMatch.send(true)
                _showLoading.send(false)
            }
        }

    }

    private fun resetPassword(resetPasswordRequest: ResetPasswordRequest) {
        viewModelScope.launch {
            protoDataStore.saveApiHeader(ApiHeader.CONFIRM_FORGOT_PASSWORD.content.formatApiHeader())
            when (val result = accountRepositories.resetPassword(resetPasswordRequest)) {
                is ApiResult.Success -> {
                    _resetPasswordSuccess.send(true)
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