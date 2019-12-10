package com.macsanityapps.virtualattendance.login

sealed class LoginEvent<out T> {

    object OnSignInClick : LoginEvent<Nothing>()
    object OnStart : LoginEvent<Nothing>()

    data class OnLoginSignInResult<out LoginResult>(val result : LoginResult) : LoginEvent<LoginResult>()

}