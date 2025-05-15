package com.nmh.base.project.customView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.PathParser
import com.nmh.base_lib.callback.OnSeekbarResult


class CustomSeekbarRating : View {
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

    var onSeekbarResult: OnSeekbarResult? = null

    private var path = Path()
    private var rectF = RectF()
    private var paint: Paint
    private var paintBg: Paint
    private var paintPr: Paint
    var rate = 0
    var isActive = false

    init {
        w = resources.displayMetrics.widthPixels / 100f

        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.FILL
        }
        paintBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
        paintPr = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //draw background
        for (i in arrPathBg.indices) {
            canvas.save()
            path.reset()
            path.addPath(PathParser.createPathFromPathData(arrPathBg[i]))
            paintBg.color = Color.parseColor(arrColorBg[i])
            path.computeBounds(rectF, true)
            canvas.translate(0f, height / 4f)
            canvas.scale(width / (rectF.width() * 5), height / (rectF.height() * 2))
            canvas.drawPath(path, paintBg)
            canvas.restore()
        }

        //draw progress rate
        if (isActive) {
            canvas.save()
            path.reset()
            path.addPath(PathParser.createPathFromPathData("M0 10C0 4.48 4.48 0 10 0h45c5.52 0 10 4.48 10 10s-4.48 10-10 10H10C4.48 20 0 15.52 0 10Z"))
            path.computeBounds(rectF, true)
            when(rate) {
                5 -> {
                    paintPr.color = Color.parseColor(arrColorPr[0])
                    canvas.translate(3.95f * width / 5f, height / 8f)
                }
                4 -> {
                    paintPr.color = Color.parseColor(arrColorPr[1])
                    canvas.translate(2.95f * width / 5f, height / 8f)
                }
                3 -> {
                    paintPr.color = Color.parseColor(arrColorPr[2])
                    canvas.translate(1.95f * width / 5, height / 8f)
                }
                2 -> {
                    paintPr.color = Color.parseColor(arrColorPr[3])
                    canvas.translate(0.95f * width / 5f, height / 8f)
                }
                1 -> {
                    paintPr.color = Color.parseColor(arrColorPr[4])
                    canvas.translate(0f, height / 8f)
                }
            }
            canvas.scale(width / (rectF.width() * 4.65f), height / (rectF.height() * 1.25f))
            canvas.drawPath(path, paintPr)
            canvas.restore()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        isActive = true
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                when(event.x) {
                    in 0f..0.95f * width / 5f -> rate = 1
                    in 0.95f * width / 5f..1.95f * width / 5f -> rate = 2
                    in 1.95f * width / 5f..2.95f * width / 5f -> rate = 3
                    in 2.95f * width / 5f..3.95f * width / 5f -> rate = 4
                    in 3.95f * width / 5f..width.toFloat() -> rate = 5
                }
                onSeekbarResult?.onMove(this, rate)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                when(event.x) {
                    in 0f..0.95f * width / 5f -> rate = 1
                    in 0.95f * width / 5f..1.95f * width / 5f -> rate = 2
                    in 1.95f * width / 5f..2.95f * width / 5f -> rate = 3
                    in 2.95f * width / 5f..3.95f * width / 5f -> rate = 4
                    in 3.95f * width / 5f..width.toFloat() -> rate = 5
                }
                onSeekbarResult?.onMove(this, rate)
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                when(event.x) {
                    in 0f..0.95f * width / 5f -> rate = 1
                    in 0.95f * width / 5f..1.95f * width / 5f -> rate = 2
                    in 1.95f * width / 5f..2.95f * width / 5f -> rate = 3
                    in 2.95f * width / 5f..3.95f * width / 5f -> rate = 4
                    in 3.95f * width / 5f..width.toFloat() -> rate = 5
                }
                onSeekbarResult?.onUp(this, rate)
                invalidate()
            }
        }
        return true
    }

    private val arrColorPr = arrayOf(
        "#EC2525",
        "#ECD825",
        "#7DEC25",
        "#2581EC",
        "#9525EC"
    )

    private val arrColorBg = arrayOf(
        "#80EC2525",
        "#80ECD825",
        "#807DEC25",
        "#802581EC",
        "#809525EC"
    )

    private val arrPathBg = arrayOf(
        "M305 6c0 3.31-2.69 6-6 6h-55V0h55c3.31 0 6 2.69 6 6Z",
        "M183 0H244V12H183z",
        "M122 0H183V12H122z",
        "M61 0H122V12H61z",
        "M61 12H6c-3.31 0-6-2.69-6-6s2.69-6 6-6h55v12Z"
    )
}