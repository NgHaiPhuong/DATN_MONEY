package com.nghp.project.moneyapp.customView

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import com.nghp.project.moneyapp.helpers.CHART_DETAIL
import com.nghp.project.moneyapp.sharepref.DataLocalManager
import com.nghp.project.moneyapp.utils.UtilsBitmap

class CustomBarChartRenderer(
    private val barChart: BarChart,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : BarChartRenderer(barChart, animator, viewPortHandler) {

    private val barRadius = 50f
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val barRect = RectF()

    init {
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 2f
    }

    override fun drawData(c: Canvas?) {
        initBuffers()

        super.drawData(c)
    }

    public override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        if (!DataLocalManager.getBoolean(CHART_DETAIL, false)) {
            val barWidth = 20f

            val buffer = mBarBuffers[index]
            buffer.feed(dataSet)
            barChart.getTransformer(dataSet.axisDependency).pointValuesToPixel(buffer.buffer)

            var j = 0
            while (j < buffer.buffer.size) {
                val centerX = (buffer.buffer[j] + buffer.buffer[j + 2]) / 2
                val left = centerX - barWidth / 2
                val right = centerX + barWidth / 2
                val top = buffer.buffer[j + 1]
                val bottom = buffer.buffer[j + 3]

                if (!mViewPortHandler.isInBoundsLeft(right)) {
                    j += 4
                    continue
                }
                if (!mViewPortHandler.isInBoundsRight(left)) break

                val color = dataSet.getColor(j / 4)
                mRenderPaint.shader = LinearGradient(
                    centerX,
                    top,
                    centerX,
                    bottom,
                    intArrayOf(getColorAlpha(color), color),
                    null,
                    Shader.TileMode.CLAMP
                )

                val rectF = RectF(left, top, right, bottom)
                val path = Path().apply {
                    addRoundRect(
                        rectF,
                        floatArrayOf(barRadius, barRadius, barRadius, barRadius, 0f, 0f, 0f, 0f),
                        Path.Direction.CW
                    )
                }
                c.drawPath(path, mRenderPaint)

                borderPaint.color = dataSet.getColor(j / 4)
                c.drawPath(path, borderPaint)
                j += 4
            }
        } else {
            val trans = mChart.getTransformer(dataSet.axisDependency)
            mBarBorderPaint.color = dataSet.barBorderColor
            mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)

            val drawBorder = dataSet.barBorderWidth > 0f

            val phaseX = mAnimator.phaseX
            val phaseY = mAnimator.phaseY

            val barData = mChart.barData
            val barWidth = barData.barWidth
            val isSingleColor = dataSet.colors.size == 1

            val buffer = mBarBuffers[index]
            buffer.setPhases(phaseX, phaseY)
            buffer.setDataSet(index)
            buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
            buffer.setBarWidth(barWidth)
            buffer.feed(dataSet)

            trans.pointValuesToPixel(buffer.buffer)

            val radius = Utils.convertDpToPixel(5f) // Độ bo góc

            for (j in 0 until buffer.size() step 4) {
                val left = buffer.buffer[j]
                val top = buffer.buffer[j + 1]
                val right = buffer.buffer[j + 2]
                val bottom = buffer.buffer[j + 3]

                barRect.set(left, top, right, bottom)

                // 👇 Set màu cho từng cột
                val color = if (isSingleColor)
                    dataSet.color else dataSet.getColor(j / 4)
                mRenderPaint.color = color

                val path = Path().apply {
                    addRoundRect(barRect, floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f), Path.Direction.CW)
                }

                // Vẽ cột bo góc (chỉ bo góc trên trái và phải)
                c.drawPath(path, mRenderPaint)

                if (drawBorder) {
                    // Vẽ border cho cột bo góc
                    c.drawPath(path, mBarBorderPaint)
                }
            }
        }
    }

    private fun getColorAlpha(color: Int): Int {
        return Color.parseColor(UtilsBitmap.toRGBString(color).replace("#", "#01"))
    }
}