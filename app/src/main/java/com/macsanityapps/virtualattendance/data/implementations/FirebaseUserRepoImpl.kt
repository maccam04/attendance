package com.macsanityapps.virtualattendance.data.implementations

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.macsanityapps.virtualattendance.common.ResultWrapper
import com.macsanityapps.virtualattendance.common.awaitTaskCompletable
import com.macsanityapps.virtualattendance.data.User
import com.macsanityapps.virtualattendance.data.repository.IUserRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseUserRepoImpl(val auth: FirebaseAuth = FirebaseAuth.getInstance()) : IUserRepository {

    override suspend fun signInGoogleUser(idToken: String): ResultWrapper<Exception, Unit> =
        withContext(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                awaitTaskCompletable(auth.signInWithCredential(credential))

                ResultWrapper.build { Unit }
            } catch (exception: Exception) {
                ResultWrapper.build { throw exception }
            }

        }


    override suspend fun signOutCurrentUser(): ResultWrapper<Exception, Unit> {
        return ResultWrapper.build {
            auth.signOut()
        }
    }

    override suspend fun getCurrentUser(): ResultWrapper<Exception, User?> {
        val firebaseUser = auth.currentUser
        return if (firebaseUser == null) {
            ResultWrapper.build { null }
        } else {
            ResultWrapper.build {
                User(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    phoneNumber = firebaseUser.phoneNumber ?: "",
                    isLogin = true
                )
            }
        }
    }
}