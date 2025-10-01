package com.nghp.project.moneyapp.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.nghp.project.moneyapp.R

class CustomProgressStatistic : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var rectF = RectF()
    private var path = Path()
    private var w = 0f
    private var radius = 0f
    private var isCreate = true
    private var max = 100
    private var currentProgress = 0L
    private var sizeBg = 0f
    private var sizePos = 0f
    private var colorBg = ContextCompat.getColor(context, R.color.color_F4F9FF)
    private var colorPr = ContextCompat.getColor(context, R.color.main_color)

    init {
        w = resources.displayMetrics.widthPixels / 100f
        radius = 2f * w
        sizeBg = 2f * w
        sizePos = 2f * w
        paint.apply {
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            color = ContextCompat.getColor(context, R.color.main_color)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (isCreate) {
            isCreate = false
            rectF.set(radius, (height - radius) / 2f, width - radius, (height + radius) / 2f)
            path.addRoundRect(rectF, radius / 2f, radius / 2f, Path.Direction.CW)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.clipPath(path)

        //draw bg
        paint.color = colorBg
        canvas.drawLine(radius, height / 2f, width - radius, height / 2f, paint)

        // draw load
        paint.color = colorPr
        canvas.drawLine((currentProgress + radius), height / 2f, (currentProgress + width - radius), height / 2f, paint)

        paint.apply {
            clearShadowLayer()
            color = colorBg
            strokeWidth = sizeBg
        }
        canvas.drawLine(radius, height / 2f, width - radius, height / 2f, paint)

        paint.apply {
            color = colorPr
            strokeWidth = sizePos
        }
        val p = (width - radius) * currentProgress / max + radius
        canvas.drawLine(radius / 2f, height / 2f, p, height / 2f, paint)
    }

    fun setRadius(radius: Float) {
        this.radius = radius

        invalidate()
    }

    fun setMax(max: Int) {
        this.max = max

        invalidate()
    }

    fun setCurrentProgress(progress: Long) {
        this.currentProgress = progress

        invalidate()
    }

    fun setColorBackground(color: Int) {
        this.colorBg = color

        invalidate()
    }

    fun setColorProgress(color: Int) {
        this.colorPr = color

        invalidate()
    }
}