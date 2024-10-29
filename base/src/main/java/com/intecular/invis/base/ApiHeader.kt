package com.intecular.invis.base

enum class ApiHeader(val content: String) {
    COMMON_HEADER_PREFIX("AWSCognitoIdentityProviderService."),
    SIGN_UP("SignUp"),
    CONFIRM_SIGN_UP("ConfirmSignUp"),
    LOGIN("InitiateAuth"),
    FORGOT_PASSWORD("ForgotPassword"),
    CHANGE_PASSWORD("ChangePassword"),
    CONFIRM_FORGOT_PASSWORD("ConfirmForgotPassword"),
    GET_USER_INFO("GetUser"),
    LOGOUT("RevokeToken"),
    DELETE_ACCOUNT("DeleteUser")
}