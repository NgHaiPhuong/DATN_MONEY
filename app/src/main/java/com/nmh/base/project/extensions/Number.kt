package com.nmh.base.project.extensions

import android.content.Context

fun Float.dpToPx(context: Context) : Float{
    val density = context.resources.displayMetrics.density
    return this * density
}
