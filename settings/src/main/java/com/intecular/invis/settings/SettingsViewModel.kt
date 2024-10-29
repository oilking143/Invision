package com.intecular.invis.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intecular.invis.base.ApiHeader
import com.intecular.invis.base.ext.formatApiHeader
import com.intecular.invis.data.ErrorHandlerDelegate
import com.intecular.invis.data.SignInViewModelDelegate
import com.intecular.invis.data.data.DeviceHomeDrawerInfo
import com.intecular.invis.data.datastore.ProtoDataStore
import com.intecular.invis.data.entities.request.GetUserAttributeRequest
import com.intecular.invis.data.entities.request.LoginByRefreshParameters
import com.intecular.invis.data.entities.request.LoginByRefreshTokenRequest
import com.intecular.invis.data.repositories.AccountRepositories
import com.intecular.invis.data.repositories.UserInfoRepository
import com.slack.eithernet.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val accountRepositories: AccountRepositories,
    private val userInfoRepository: UserInfoRepository,
    private val protoDataStore: ProtoDataStore,
    signInViewModelDelegate: SignInViewModelDelegate,
    errorHandlerDelegate: ErrorHandlerDelegate
) : ViewModel(), SignInViewModelDelegate by signInViewModelDelegate,
    ErrorHandlerDelegate by errorHandlerDelegate {

    var deviceRoomInfo by mutableStateOf(mutableListOf(DeviceHomeDrawerInfo("")))
        private set

    private var _settingItemList = MutableStateFlow<List<SettingItemData>>(emptyList())
    val settingItemList = _settingItemList

    private val _showLoading = Channel<Boolean>()
    val showLoading = _showLoading.receiveAsFlow()

    private val _signInSuccess = Channel<Pair<String, String>>()
    val signInSuccess = _signInSuccess.receiveAsFlow()

    private val _userSignInClickEnable = Channel<Boolean>()
    val userSignInClickEnable = _userSignInClickEnable.receiveAsFlow()

    init {
        setDeviceRoomInfo()
        getSettingItemDataList()
    }

    fun loginByRefreshToken() {
        if (isUserSignedInValue) {
            viewModelScope.launch {
                protoDataStore.saveApiHeader(ApiHeader.LOGIN.content.formatApiHeader())
                _showLoading.send(true)
                val userEmail = protoDataStore.getUserEmail().first()
                val userInfo = userInfoRepository.getUserInfo(userEmail).first()
                val loginByRefreshTokenRequest =
                    LoginByRefreshTokenRequest(
                        authParameters = LoginByRefreshParameters(
                            refreshToken = userInfo.refreshToken,
                        )
                    )
                when (val result =
                    accountRepositories.loginByRefreshToken(loginByRefreshTokenRequest)) {
                    is ApiResult.Success -> {
                        Timber.d("RefreshToken SigIn Success")
                        getUserAttribute(
                            GetUserAttributeRequest(
                                result.value.authenticationResult.accessToken
                            )
                        )
                    }

                    is ApiResult.Failure -> {
                        _showLoading.send(false)
                        extractErrorMessage(result)
                        Timber.d("RefreshToken SigIn Fail")

                    }
                }
            }
        } else {
            _userSignInClickEnable.trySend(true)
        }
    }

    private fun getUserAttribute(
        getUserAttributeRequest: GetUserAttributeRequest,
    ) {
        viewModelScope.launch {
            protoDataStore.saveApiHeader(ApiHeader.GET_USER_INFO.content.formatApiHeader())
            when (val result = accountRepositories.getUserAttributes(getUserAttributeRequest)) {
                is ApiResult.Success -> {
                    val attributeList = result.value.userAttributesList
                    val email =
                        attributeList.find { it.attributeName == "email" }?.attributeValue ?: ""
                    val name =
                        attributeList.find { it.attributeName == "name" }?.attributeValue ?: ""
                    if (email.isNotEmpty() && name.isNotEmpty()) {
                        _signInSuccess.send((email to name))
                        _userSignInClickEnable.send(true)
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

    private fun setDeviceRoomInfo() {
        deviceRoomInfo.clear()
        deviceRoomInfo.addAll(
            listOf(DeviceHomeDrawerInfo("First Home"), DeviceHomeDrawerInfo("Second Home"))
        )
    }

    private fun getSettingItemDataList() {
        viewModelScope.launch {
            val titleList =
                context.resources.getStringArray(com.intecular.invis.common.ui.resource.R.array.setting_item_title_array)
                    .toList()
            val contentList =
                context.resources.getStringArray(com.intecular.invis.common.ui.resource.R.array.setting_item_content_array)
                    .toList()
            val iconIdList = listOf(
                com.intecular.invis.common.ui.resource.R.drawable.ic_linking,
                com.intecular.invis.common.ui.resource.R.drawable.ic_unit,
                com.intecular.invis.common.ui.resource.R.drawable.ic_notification,
                com.intecular.invis.common.ui.resource.R.drawable.ic_info
            )
            _settingItemList.emit(
                titleList.mapIndexed { index, title ->
                    SettingItemData(
                        title,
                        contentList[index],
                        iconIdList[index]
                    )
                }
            )
        }
    }

    fun reductionUserSignInClickEnable() {
        viewModelScope.launch {
            _userSignInClickEnable.send(false)
        }
    }

}