package com.nmh.base.project.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View


class WaveformView: View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var paint = Paint()
    private var amplitudes = ArrayList<Float>()
    private var spikes = ArrayList<RectF>()

    private var radius = 6f
    private var w = 9f
    private var d = 6f

    private var sw = 0f
    private var sh = 0f

    private var maxSpikes = 0

    init {
        paint.color = Color.BLACK
        val maxScreen = resources.displayMetrics.widthPixels / 100F

        w = 0.83f * maxScreen
        d = 0.55f * maxScreen
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        sw = width.toFloat()
        sh = height.toFloat()

        maxSpikes = (sw / (w + d)).toInt()
    }

    fun setDataAudio(amp: Float) {
        val norm = (amp.toInt() / 34).coerceAtMost(height).toFloat()
        amplitudes.add(norm)

        spikes.clear()
        val amps = amplitudes.takeLast(maxSpikes)
        for (i in amps.indices) {
            val left = sw - i * (w + d)
            val top = sh / 2 - amps[i] / 2
            val right = left + w
            val bottom = top + amps[i]
            spikes.add(RectF(left, top, right, bottom))
        }
        invalidate()
    }

    fun clear(): ArrayList<Float> {
        val amps = amplitudes.clone() as ArrayList<Float>
        amplitudes.clear()
        spikes.clear()
        invalidate()

        return amps
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        spikes.forEach {
            canvas.drawRoundRect(it, 0f, 0f, paint)
        }
    }

    fun setColor(color: Int) {
        paint.color = color
        invalidate()
    }


}