package com.nmh.base.project.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.nmh.base.project.R

class JarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var jarColor: Int = Color.YELLOW
    private var cornerRadius: Float = 20f
    private var shadowRadius: Float = 5f

    private val rectF = RectF()
    private val barRectF = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    init {
        // Đọc các thuộc tính từ XML
        context.withStyledAttributes(attrs, R.styleable.JarView) {
            jarColor = getColor(R.styleable.JarView_jarColor, Color.YELLOW)
            cornerRadius = getDimension(R.styleable.JarView_cornerRadius, 20f)
            shadowRadius = getDimension(R.styleable.JarView_shadowRadius, 5f)
        }

        // Thiết lập để hỗ trợ đổ bóng
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Tính toán kích thước của jar
        val padding = shadowRadius * 2
        val jarWidth = width - padding * 2
        val jarHeight = height - padding * 2 - 10f  // 10f for the upper bar space

        // Thiết lập vị trí hình vuông chính
        rectF.set(
            padding,
            padding + 10f,  // Extra space for the upper bar
            padding + jarWidth,
            padding + 10f + jarHeight
        )

        // Vẽ viền trắng với đổ bóng có màu giống màu jar
        paint.color = Color.WHITE
        paint.setShadowLayer(shadowRadius, 0f, 0f, jarColor)
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)

        // Vẽ hình vuông bên trong với màu jar
        val innerPadding = 5f
        rectF.inset(innerPadding, innerPadding)
        paint.clearShadowLayer()
        paint.color = jarColor
        canvas.drawRoundRect(rectF, cornerRadius - innerPadding, cornerRadius - innerPadding, paint)

        // Vẽ thanh trên cùng
        val barWidth = width * 0.4f
        val barHeight = 10f
        barRectF.set(
            width / 2 - barWidth / 2,
            padding - barHeight / 2,
            width / 2 + barWidth / 2,
            padding + barHeight / 2
        )

        // Vẽ thanh trên với đổ bóng
        paint.color = Color.WHITE
        paint.setShadowLayer(shadowRadius, 0f, 0f, jarColor)
        canvas.drawRoundRect(barRectF, barHeight / 2, barHeight / 2, paint)
    }

    // Phương thức để đặt màu jar qua code
    fun setJarColor(color: Int) {
        jarColor = color
        invalidate() // Yêu cầu vẽ lại view
    }

    // Phương thức để đặt độ cong góc
    fun setCornerRadius(radius: Float) {
        cornerRadius = radius
        invalidate()
    }

    // Phương thức để đặt độ rộng bóng
    fun setShadowRadius(radius: Float) {
        shadowRadius = radius
        invalidate()
    }
}