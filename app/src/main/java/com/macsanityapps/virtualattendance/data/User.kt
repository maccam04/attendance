package com.macsanityapps.virtualattendance.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(val uid: String,
                val name: String,
                val email: String,
                val phoneNumber: String,
                val isLogin : Boolean = false) : Parcelable