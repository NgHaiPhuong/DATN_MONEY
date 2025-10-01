package com.nghp.project.moneyapp.customView

import android.graphics.Canvas
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.renderer.PieChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.cos
import kotlin.math.sin

class CustomPieChartRenderer(
    chart: PieChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : PieChartRenderer(chart, animator, viewPortHandler) {

    override fun drawExtras(c: Canvas) {
        super.drawExtras(c)

        val dataSet = mChart.data.getDataSetByIndex(0)
        val center = mChart.centerCircleBox
        val radius = mChart.radius

        val iconsOffset = 40f  // khoảng cách icon ra ngoài vòng
        val iconSize = 50f    // kích thước icon

        val rotationAngle = mChart.rotationAngle
        val absoluteAngles = FloatArray(dataSet.entryCount)
        var angle = 0f

        // Tính toán góc bắt đầu từng PieEntry
        for (i in 0 until dataSet.entryCount) {
            absoluteAngles[i] = angle
            angle += dataSet.getEntryForIndex(i).value / mChart.data.yValueSum * 360f
        }

        for (i in 0 until dataSet.entryCount) {
            val entry = dataSet.getEntryForIndex(i)
            val sliceAngle = entry.value / mChart.data.yValueSum * 360f
            val angleMiddle = absoluteAngles[i] + sliceAngle / 2f + rotationAngle
            val angleRad = Math.toRadians(angleMiddle.toDouble())

            val x = (center.x + (radius + iconsOffset) * cos(angleRad)).toFloat()
            val y = (center.y + (radius + iconsOffset) * sin(angleRad)).toFloat()

            entry.icon?.let { icon ->
                icon.setBounds(
                    (x - iconSize / 2).toInt(),
                    (y - iconSize / 2).toInt(),
                    (x + iconSize / 2).toInt(),
                    (y + iconSize / 2).toInt()
                )
                icon.draw(c)
            }
        }
    }
}
