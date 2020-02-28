package com.macsanityapps.virtualattendance.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    companion object {
        var URL_BASE = "https://fcm.googleapis.com/"
    }

    @Headers(
        "Authorization: key=AIzaSyBQlmsQSoQwYGW_LDVBcqSlOQx16ElW-nk",
        "Content-Type: application/json"
    )
    @POST("fcm/send")
    fun sendData(@Body notif: String): Call<String>

}