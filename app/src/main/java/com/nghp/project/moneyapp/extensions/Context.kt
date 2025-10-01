package com.nghp.project.moneyapp.extensions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService

fun Context.openSettingPermission(action: String) {
    val intent = Intent(action).apply { data = Uri.fromParts("package", packageName, null) }
    startActivity(intent)
}

fun Context.haveNetworkConnection(): Boolean {
    var haveConnectedWifi = false
    var haveConnectedMobile = false
    val cm =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = cm.allNetworkInfo
    for (ni in netInfo) {
        if (ni.typeName.equals("WIFI", ignoreCase = true))
            if (ni.isConnected) haveConnectedWifi = true
        if (ni.typeName.equals("MOBILE", ignoreCase = true))
            if (ni.isConnected) haveConnectedMobile = true
    }
    return haveConnectedWifi || haveConnectedMobile
}

fun Context.showToast(msg: String, gravity: Int) {
    val toast: Toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
    toast.setGravity(gravity, 0, 0)
    toast.show()
}

fun Context.checkPer(str: Array<String>): Boolean {
    if (str[0] == "") return true
    var isCheck = true
    for (i in str) {
        if (ContextCompat.checkSelfPermission(this, i) != PackageManager.PERMISSION_GRANTED)
            isCheck = false
    }

    return isCheck
}

fun Context.checkAllPerGrand(): Boolean {
    val storagePer = checkPer(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO)
    else arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE))
    val overlayPer = Settings.canDrawOverlays(this)
    val cameraPer = checkPer(arrayOf(Manifest.permission.CAMERA))
    val recordPer = checkPer(arrayOf(Manifest.permission.RECORD_AUDIO))

    return storagePer && overlayPer && cameraPer && recordPer
}