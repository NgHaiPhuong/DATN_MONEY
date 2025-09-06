package com.nmh.base.project

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.nmh.base.project.sharepref.DataLocalManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NMHApp : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var ctx: Context

        @SuppressLint("StaticFieldLeak")
        var w = 0f
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        DataLocalManager.init(applicationContext)

        ctx = applicationContext
        w = resources.displayMetrics.widthPixels / 100f
    }
}