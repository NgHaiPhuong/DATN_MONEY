package com.nghp.project.moneyapp.customView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.nghp.project.moneyapp.R
import android.graphics.RectF
import android.graphics.Typeface

class CustomPillProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Các biến màu sắc
    private var lightColor = Color.parseColor("#FFCCCC")  // Màu nhạt (bên trong)
    private var darkColor = Color.parseColor("#FF7F7F")   // Màu đậm (viền và thanh)
    private var textColor = Color.BLUE                    // Màu text

    // Phần trăm tiến độ (mặc định là 100%)
    private var progress = 100

    // Các paint để vẽ
    private val darkPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val lightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Kích thước các thành phần
    private val barHeight: Float
    private val cornerRadius: Float

    // Text được hiển thị
    private var progressText = "$progress%"

    private var isPercent = true

    init {
        darkPaint.style = Paint.Style.FILL
        lightPaint.style = Paint.Style.FILL
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER

        // Thiết lập text in đậm
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        barHeight = dpToPx(8f)
        cornerRadius = dpToPx(15f)

        // Đọc các thuộc tính từ XML nếu có
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.BatteryProgressView, defStyleAttr, 0)

            lightColor = typedArray.getColor(R.styleable.BatteryProgressView_lightColor, lightColor)
            darkColor = typedArray.getColor(R.styleable.BatteryProgressView_darkColor, darkColor)
            textColor = typedArray.getColor(R.styleable.BatteryProgressView_textColor, textColor)
            progress = typedArray.getInt(R.styleable.BatteryProgressView_progress, progress)

            typedArray.recycle()
        }

        updateColors()
        updateProgressText()
    }

    private fun updateColors() {
        darkPaint.color = darkColor
        lightPaint.color = lightColor
        textPaint.color = textColor
        invalidate()
    }

    private fun updateProgressText() {
        progressText = if (isPercent) "$progress%" else "$progress"
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        // Tính toán kích thước
        val barMargin = width * 0.1f  // Giảm margin xuống chỉ còn 10% mỗi bên
        val barStartX = barMargin
        val barEndX = width - barMargin

        val boxTop = barHeight + dpToPx(5f)  // Tạo khoảng cách giữa thanh và box
        val boxBottom = height

        // Sử dụng barHeight làm độ rộng của viền
        val strokeWidth = barHeight

        // 1. Vẽ thanh ngang ở trên cùng
        val barRect = RectF(barStartX, 0f, barEndX, barHeight)
        canvas.drawRoundRect(barRect, barHeight/2, barHeight/2, darkPaint)

        // 2. Vẽ hình chữ nhật viền bên ngoài
        val boxOuterRect = RectF(0f, boxTop, width, boxBottom)
        canvas.drawRoundRect(boxOuterRect, cornerRadius, cornerRadius, darkPaint)

        // 3. Vẽ hình chữ nhật bên trong (màu nhạt hơn)
        val boxInnerRect = RectF(
            strokeWidth,
            boxTop + strokeWidth,
            width - strokeWidth,
            boxBottom - strokeWidth
        )
        canvas.drawRoundRect(boxInnerRect, cornerRadius - strokeWidth/2, cornerRadius - strokeWidth/2, lightPaint)

        textPaint.textSize = (boxBottom - boxTop) * 0.3f
        val textX = width / 2f
        val textY = boxTop + (boxBottom - boxTop) / 2f + textPaint.textSize / 3  // Căn giữa text
        canvas.drawText(progressText, textX, textY, textPaint)
    }

    // Setter để thiết lập từ activity
    fun setLightColor(color: Int) {
        lightColor = color
        updateColors()
    }

    fun setDarkColor(color: Int) {
        darkColor = color
        updateColors()
    }

    fun setTextColor(color: Int) {
        textColor = color
        updateColors()
    }

    fun setFormPercentText(isCheck : Boolean) {
        isPercent = isCheck
        updateProgressText()
    }

    fun setProgress(value: Int) {
//        progress = value.coerceIn(0, 100)
        progress = value
        updateProgressText()
    }

    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
}