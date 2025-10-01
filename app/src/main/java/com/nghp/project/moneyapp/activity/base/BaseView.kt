package com.nghp.project.moneyapp.activity.base

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.IdRes

interface BaseView {
    fun startIntent(nameActivity: String, isFinish: Boolean)
    fun startIntent(intent: Intent, isFinish: Boolean)
    fun startIntentForResult(startForResult: ActivityResultLauncher<Intent>, nameActivity: String, isFinish: Boolean)
    fun startIntentForResult(startForResult: ActivityResultLauncher<Intent>, intent: Intent, isFinish: Boolean)
    fun showLoading()
    fun showLoading(cancelable: Boolean)
    fun hideLoading()
}