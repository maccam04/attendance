package com.macsanityapps.virtualattendance.data.repository

import com.macsanityapps.virtualattendance.common.ResultWrapper
import com.macsanityapps.virtualattendance.data.User
import java.lang.Exception

interface IUserRepository {

    suspend fun getCurrentUser() : ResultWrapper<Exception, User?>

    suspend fun signOutCurrentUser() : ResultWrapper<Exception, Unit>

    suspend fun signInGoogleUser(idToken: String): ResultWrapper<Exception, Unit>

}