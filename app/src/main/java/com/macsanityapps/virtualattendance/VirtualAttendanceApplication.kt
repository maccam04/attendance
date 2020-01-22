package com.macsanityapps.virtualattendance

import android.app.Application
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId

class VirtualAttendanceApplication : Application() {



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

                Log.e("FCM TOKEN ", task.result?.token)

            })
    }
}