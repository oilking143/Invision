package com.intecular.invis.data.api

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
import com.intecular.invis.data.entities.response.ErrorResponse
import com.intecular.invis.data.entities.response.GetUserAttributeResponse
import com.intecular.invis.data.entities.response.LoginByAccountResponse
import com.intecular.invis.data.entities.response.LoginByRefreshTokenResponse
import com.intecular.invis.data.entities.response.SignupResponse
import com.slack.eithernet.ApiResult
import com.slack.eithernet.DecodeErrorBody
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountApi {

    @DecodeErrorBody
    @POST("/")
    suspend fun signup(
        @Body signUpRequest: SignupRequest
    ): ApiResult<SignupResponse, ErrorResponse>

    @DecodeErrorBody
    @POST("/")
    suspend fun confirmSignup(
        @Body confirmSignupRequest: ConfirmSignupRequest
    ): ApiResult<Any, ErrorResponse>

    @DecodeErrorBody
    @POST("/")
    suspend fun loginByAccount(
        @Body loginByAccountRequest: LoginByAccountRequest
    ): ApiResult<LoginByAccountResponse, ErrorResponse>

    @DecodeErrorBody
    @POST("/")
    suspend fun loginByRefreshToken(
        @Body loginByRefreshTokenRequest: LoginByRefreshTokenRequest
    ): ApiResult<LoginByRefreshTokenResponse, ErrorResponse>

    @DecodeErrorBody
    @POST("/")
    suspend fun getUserAttributes(
        @Body getUserAttributeRequest: GetUserAttributeRequest
    ): ApiResult<GetUserAttributeResponse, ErrorResponse>

    @DecodeErrorBody
    @POST("/")
    suspend fun forgetPassword(
        @Body forgetPasswordRequest: ForgetPasswordRequest
    ): ApiResult<Any, ErrorResponse>

    @DecodeErrorBody
    @POST("/")
    suspend fun changePassword(
        @Body changePasswordRequest: ChangePasswordRequest
    ): ApiResult<Any, ErrorResponse>

    @DecodeErrorBody
    @POST("/")
    suspend fun resetPassword(
        @Body resetPasswordRequest: ResetPasswordRequest
    ): ApiResult<Any, ErrorResponse>

    @DecodeErrorBody
    @POST("/")
    suspend fun logOut(
        @Body logOutRequest: LogOutRequest
    ): ApiResult<Any, ErrorResponse>

    @DecodeErrorBody
    @POST("/")
    suspend fun deleteAccount(
        @Body deleteAccountRequest: DeleteAccountRequest
    ): ApiResult<Any, ErrorResponse>
}