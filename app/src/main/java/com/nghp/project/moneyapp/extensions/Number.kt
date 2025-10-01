package com.nghp.project.moneyapp.extensions

import android.content.Context

fun Float.dpToPx(context: Context) : Float{
    val density = context.resources.displayMetrics.density
    return this * density
}
