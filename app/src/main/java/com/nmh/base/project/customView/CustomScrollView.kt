package com.nmh.base.project.customView

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class CustomScrollView: ScrollView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var isEnableScroll = true

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (isEnableScroll) super.onInterceptTouchEvent(ev)
        else false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if (isEnableScroll) super.onTouchEvent(ev)
        else false
    }
}