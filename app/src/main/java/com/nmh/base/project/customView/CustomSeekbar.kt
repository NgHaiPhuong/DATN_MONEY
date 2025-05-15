package com.nmh.base.project.customView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.nmh.base.project.R
import com.nmh.base.project.callback.ICallBackCheck
import com.nmh.base_lib.callback.OnSeekbarResult


class CustomSeekbar : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    companion object {
        var w = 0f
    }

    private var paint: Paint
    private var paintProgress: Paint
    private var progress = 0
    private var max = 0
    private var sizeThumb = 0f
    private var sizeBg = 0f
    private var sizePos = 0f

    var isActive = true
    private var colorBg = ContextCompat.getColor(context, R.color.color_DFDFDF)
    private var colorThumb = ContextCompat.getColor(context, R.color.main_color)
    private var colorPr = ContextCompat.getColor(context, R.color.main_color)

    var onSeekbarResult: OnSeekbarResult? = null
    var isSwipe: ICallBackCheck? = null

    init {
        w = resources.displayMetrics.widthPixels / 100f
        sizeThumb = 7.78f * w
        sizeBg = 1.389f * w
        sizePos = 1.389f * w

        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.FILL
        }
        paintProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.apply {
            clearShadowLayer()
            color = colorBg
            strokeWidth = sizeBg
        }
        canvas.drawLine(sizeThumb / 2, height / 2f, width - sizeThumb / 2, height / 2f, paint)

        paintProgress.apply {
            color = colorPr
            strokeWidth = sizePos
        }
        val p = (width - sizeThumb) * progress / max + sizeThumb / 2f
        canvas.drawLine(sizeThumb / 2f, height / 2f, p, height / 2f, paintProgress)

        paint.color = Color.WHITE
        canvas.drawCircle(p, height / 2f, sizeThumb / 2, paint)
        paint.color = colorThumb
        canvas.drawCircle(p, height / 2f, 2.5f * w, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        if (!isActive) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isSwipe?.check(true)
                onSeekbarResult?.onDown(this)
            }
            MotionEvent.ACTION_MOVE -> {
                progress = ((x - sizeThumb / 2) * max / (width - sizeThumb)).toInt()

                if (progress < 0) progress = 0
                else if (progress > max) progress = max
                invalidate()

                onSeekbarResult?.onMove(this, progress)
            }

            MotionEvent.ACTION_UP -> {
                isSwipe?.check(false)
                onSeekbarResult?.onUp(this, progress)
            }
        }
        return true
    }

    fun setColorThumb(color: Int) {
        this.colorThumb = color

        invalidate()
    }

    fun setColorProgress(color: Int) {
        this.colorPr = color

        invalidate()
    }

    fun setColorBg(color: Int) {
        this.colorBg = color

        invalidate()
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    fun getProgress(): Int {
        return progress
    }

    fun setMax(max: Int) {
        this.max = max
        invalidate()
    }
}