package com.macsanityapps.virtualattendance

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.protobuf.Api
import com.macsanityapps.virtualattendance.data.ApiService
import com.macsanityapps.virtualattendance.view.SeatPlanFragment
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class VirtualAttendanceApplication : Application() {

    var token: String? = null

    lateinit var apiInterface : ApiService

    override fun onCreate() {
        super.onCreate()

        val pref = applicationContext.getSharedPreferences(
            "Token",
            0
        )

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                val editor = pref?.edit()
                editor?.putString("token",  task.result?.token)
                editor?.apply()

                Log.e("FCM TOKEN ", task.result?.token)

                token = task.result?.token

            })


        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiService.URL_BASE)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

         apiInterface = retrofit.create(ApiService::class.java)


        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

    }


    fun getApi() : ApiService {
        return apiInterface
    }

    fun giveToken() : String = token!!
}