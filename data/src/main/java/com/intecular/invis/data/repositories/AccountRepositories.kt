package com.intecular.invis.data.repositories

import com.intecular.invis.base.di.IoDispatcher
import com.intecular.invis.data.api.AccountApi
import com.intecular.invis.data.entities.request.ChangePasswordRequest
import com.intecular.invis.data.entities.request.ConfirmSignupRequest
import com.intecular.invis.data.entities.request.DeleteAccountRequest
import com.intecular.invis.data.entities.request.ForgetPasswordRequest
import com.intecular.invis.data.entities.request.GetUserAttributeRequest
import com.intecular.invis.data.entities.request.LogOutRequest
import com.intecular.invis.data.entities.request.LoginByAccountRequest
import com.intecular.invis.data.entities.request.LoginByRefreshTokenRequest
import com.intecular.invis.data.entities.request.ResetPasswordRequest
import com.intecular.invis.data.entities.request.SignupRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepositories @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val accountApi: AccountApi,
) {

    suspend fun signup(signUpRequest: SignupRequest) = withContext(ioDispatcher) {
        accountApi.signup(signUpRequest)
    }

    suspend fun confirmSignup(confirmSignupRequest: ConfirmSignupRequest) =
        withContext(ioDispatcher) {
            accountApi.confirmSignup(confirmSignupRequest)
        }

    suspend fun loginByAccount(loginByAccountRequest: LoginByAccountRequest) =
        withContext(ioDispatcher) {
            accountApi.loginByAccount(loginByAccountRequest)
        }

    suspend fun loginByRefreshToken(loginByRefreshTokenRequest: LoginByRefreshTokenRequest) =
        withContext(ioDispatcher) {
            accountApi.loginByRefreshToken(loginByRefreshTokenRequest)
        }

    suspend fun getUserAttributes(getUserAttributeRequest: GetUserAttributeRequest) =
        withContext(ioDispatcher) {
            accountApi.getUserAttributes(getUserAttributeRequest)
        }

    suspend fun forgetPassword(forgetPasswordRequest: ForgetPasswordRequest) =
        withContext(ioDispatcher) {
            accountApi.forgetPassword(forgetPasswordRequest)
        }

    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest) =
        withContext(ioDispatcher) {
            accountApi.changePassword(changePasswordRequest)
        }

    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest) =
        withContext(ioDispatcher) {
            accountApi.resetPassword(resetPasswordRequest)
        }

    suspend fun logOut(logOutRequest: LogOutRequest) = withContext(ioDispatcher) {
        accountApi.logOut(logOutRequest)
    }

    suspend fun deleteAccount(deleteAccountRequest: DeleteAccountRequest) = withContext(ioDispatcher) {
        accountApi.deleteAccount(deleteAccountRequest)
    }

}