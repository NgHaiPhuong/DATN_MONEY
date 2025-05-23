package com.nmh.base.project.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

fun Resources.getActionBarHeight(context: Context): Int {
    val tv = TypedValue()
    return if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true))
        TypedValue.complexToDimensionPixelSize(tv.data, displayMetrics)
    else 0
}

@SuppressLint("InternalInsetResource", "DiscouragedApi")
fun Resources.getStatusBarHeight(): Int {
    val id = getIdentifier("status_bar_height", "dimen", "android")
    return if (id > 0) getDimensionPixelSize(id) else 0
}

@SuppressLint("InternalInsetResource", "DiscouragedApi")
fun Resources.getNavBarHeight(): Int {
    val id = getIdentifier("navigation_bar_height", "dimen", "android")
    return if (id > 0) getDimensionPixelSize(id) else 0
}
