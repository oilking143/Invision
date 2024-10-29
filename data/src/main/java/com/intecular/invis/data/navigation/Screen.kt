package com.intecular.invis.data.navigation

sealed class Screen(val route: String) {
    data object HomeScreen : Screen("home-screen")
    data object AutomationScreen : Screen("automation-screen")
    data object DviceSettingsScreen : Screen("device-settings-screen")
    data object SettingsScreen : Screen("settings-screen")

    data object SignInScreen : Screen("sign-in-screen")
    data object ForgotPasswordScreen : Screen("forgot-password")
    data object RegisterScreen : Screen("register-screen")

    data object VerificationScreen : Screen("verification-screen")
    data object UserInfoScreen : Screen("user-info-screen")
    data object ChangeNameScreen : Screen("change-name-screen")
    data object ChangePasswordScreen : Screen("change-password-screen")
    data object EditDeviceNameScreen : Screen("edit_device_name")
    data object EditDeviceDetailsScreen : Screen("edit_device_details")
    data object CustomizationsScreen : Screen("customizations")
    data object MQTTScreen : Screen("mqtt_screen")
    data object BrokeScreen : Screen("broke_ip")
    data object TopicScreen : Screen("topic")
    data object DeviceInfoScreen : Screen("device_info")
    data object UpdateScreen : Screen("soft_update")
    data object ProgressScreen : Screen("update_progress")
    data object DeviceManagerScreen : Screen("device_manager")

    data object ResetPasswordScreen : Screen("reset-password-screen")

    object AddDeviceScreen : Screen("add-device")
    data object AddHouseScreen : Screen("add-house")
    data object AddRoomScreen : Screen("add-room")
    data object EditHomeScreen : Screen("edit-home")
}