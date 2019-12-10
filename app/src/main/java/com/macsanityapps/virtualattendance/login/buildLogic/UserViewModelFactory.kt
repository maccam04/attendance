package com.macsanityapps.virtualattendance.login.buildLogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.macsanityapps.virtualattendance.data.repository.IUserRepository
import com.macsanityapps.virtualattendance.login.LoginViewModel
import kotlinx.coroutines.Dispatchers

class UserViewModelFactory(
    private val userRepo: IUserRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return LoginViewModel(userRepo, Dispatchers.Main) as T
    }

}