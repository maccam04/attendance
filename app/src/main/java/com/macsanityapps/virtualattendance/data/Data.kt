package com.macsanityapps.virtualattendance.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Rooms(
    val id : String,
    val course : String,
    val profId: String,
    val desc : String,
    val students : List<User>? = emptyList()

) {
    constructor() : this("", "", "","", emptyList())
}

data class User(
    val id : String?,
    val name : String?,
    val email : String?,
    val mobileNo: String?,
    val course : String?,
    val type : Int? = 0,
    val register : Boolean? = false,
    val status : String = "Present"

) {
    constructor() : this("", "", "","", "", 0, false)
}

data class UserList(
    val list : ArrayList<User>
)

@Parcelize
data class AuthUser(val uid: String,
                    val name: String,
                    val email: String,
                    val isLogin : Boolean = false) : Parcelable

