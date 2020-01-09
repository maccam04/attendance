package com.macsanityapps.virtualattendance.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import com.google.gson.annotations.Expose


data class Rooms(
    val id: String = "",
    val course: String = "",
    val profId: String = "",
    val desc: String = "",
    val students: List<User>? = emptyList()

) {
    constructor() : this("", "", "", "", emptyList())
}

data class User(
    val id: String?,
    val name: String?,
    val email: String?,
    val mobileNo: String?,
    val course: String?,
    val type: Int? = 0,
    val register: Boolean? = false,
    val status: String = "Present",
    val token : String = ""

) {
    constructor() : this("", "", "", "", "", 0, false)
}

data class UserList(
    val list: ArrayList<User>
)

@Parcelize
data class AuthUser(
    val uid: String,
    val name: String,
    val email: String,
    val isLogin: Boolean = false
) : Parcelable


data class Attendance(
    val id: String = "",
    val course: String = "",
    val date: String = "",
    val userId: String = "",
    val present: Boolean = false
)


data class NotificationResponse(
    @SerializedName("data")  @Expose var data: NotificationData,
    @SerializedName("to")  @Expose var to: String
)


data class NotificationData(
    @SerializedName("title")
    @Expose
    private val title: String? = null,
    @SerializedName("content")
    @Expose
    private val content: String? = null,
    @SerializedName("imageUrl")
    @Expose
    private val imageUrl: String? = null,
    @SerializedName("gameUrl")
    @Expose
    private val gameUrl: String? = null
)


data class ParentData( var date : String, var child : List<ChildData>)

data class ChildData(var absentDate: String)



