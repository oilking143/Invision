package com.intecular.invis.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intecular.invis.base.GetPublicIPUtils
import com.intecular.invis.data.ErrorHandlerDelegate
import com.intecular.invis.base.ApiHeader
import com.intecular.invis.base.ext.formatApiHeader
import com.intecular.invis.data.datastore.ProtoDataStore
import com.intecular.invis.data.entities.request.SignupRequest
import com.intecular.invis.data.entities.request.UserAttribute
import com.intecular.invis.data.repositories.AccountRepositories
import com.intecular.invis.signin.data.VerificationInfo
import com.slack.eithernet.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val protoDataStore: ProtoDataStore,
    private val accountRepositories: AccountRepositories,
    private val getPublicIPUtils: GetPublicIPUtils,
    errorHandlerDelegate: ErrorHandlerDelegate
) : ViewModel(), ErrorHandlerDelegate by errorHandlerDelegate {

    private val _signupSuccess = Channel<VerificationInfo>()
    val signupSuccess = _signupSuccess.receiveAsFlow()

    private val _showLoading = Channel<Boolean>()
    val showLoading = _showLoading.receiveAsFlow()

    fun signupAccount(signupRequest: SignupRequest) {
        viewModelScope.launch {
            _showLoading.send(true)
            protoDataStore.saveApiHeader(ApiHeader.SIGN_UP.content.formatApiHeader())
            val publicIp = getPublicIPUtils.getPublicIp()
            Timber.d("Ip:$publicIp")
            val attributeList = signupRequest.userAttributes.toMutableList()
            attributeList.add(UserAttribute("zoneinfo", publicIp))

            when (val result =
                accountRepositories.signup(signupRequest.copy(userAttributes = attributeList.toList()))) {
                is ApiResult.Success -> {
                    val response = result.value
                    Timber.d("Response:${response.codeDeliveryDetails}")
                    _signupSuccess.send(VerificationInfo(signupRequest.userName))
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