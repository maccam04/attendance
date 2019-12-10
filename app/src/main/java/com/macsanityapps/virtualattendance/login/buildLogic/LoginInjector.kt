package com.macsanityapps.virtualattendance.login.buildLogic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.FirebaseApp
import com.macsanityapps.virtualattendance.data.implementations.FirebaseUserRepoImpl
import com.macsanityapps.virtualattendance.data.repository.IUserRepository

class LoginInjector(application: Application) : AndroidViewModel(application) {

    init {
        FirebaseApp.initializeApp(application)
    }

    private fun getUserRepository(): IUserRepository {
        return FirebaseUserRepoImpl()
    }

    fun provideUserViewModelFactory(): UserViewModelFactory =
        UserViewModelFactory(
            getUserRepository()
        )

}