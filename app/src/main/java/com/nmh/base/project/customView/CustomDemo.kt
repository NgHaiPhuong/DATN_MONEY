package com.nmh.base.project.customView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.nmh.base.project.R

@SuppressLint("ViewConstructor")
class CustomDemo : View {
    // dùng trong code
    constructor(context: Context?) : super(context)

    // dùng trong code và XML
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    // dùng trong code, XML và hỗ trợ kiểu style/theme
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    /* Quá trình hiển th View
        1. Measure → gọi onMeasure() → đo kích thước
        2. Layout → gọi onLayout() → xác định vị trí
        3. Draw → gọi onDraw() → vẽ */

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var path = Path()
    private var radius = 0f
    private var currentProgress = 0
    private var max = 100
    private var rectF = RectF() // tạo 1 hình chữ nhật
    private var colorBg = ContextCompat.getColor(context, R.color.color_F4F9FF)
    private var colorProgress = ContextCompat.getColor(context, R.color.main_color)
    private var sizeBg = 0f
    private var sizeProgress = 0f
    private var w = 0f
    private var isCreate = true

    init {
        w = resources.displayMetrics.widthPixels / 100f
        radius = 2f * w
        sizeBg = 2f * w
        sizeProgress = 2f * w
        paint.apply {
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            color = ContextCompat.getColor(context, R.color.main_color)
        }
    }

    // callback hệ thống gọi khi layout thay đổi
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (isCreate) {
            isCreate = false
            rectF.set(radius, radius, width - radius, height - radius)
            // thêm bo góc ở cuối cho hình chữ nhật
            path.addRoundRect(rectF, radius / 2f, radius / 2f, Path.Direction.CW)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // chỉ được phép vẽ trong giới hạn của path, ngoài thì sẽ bị xóa
        canvas.clipPath(path)

        paint.apply {
            clearShadowLayer()
            color = colorBg
            strokeWidth = sizeBg
        }
        canvas.drawLine(radius, height / 2f, width - radius, height / 2f, paint)

        paint.apply {
            color = colorProgress
            strokeWidth = sizeProgress
        }
        val p = (width - radius) * currentProgress / max + radius
        canvas.drawLine(radius, height / 2f, p, height / 2f, paint)

        if (currentProgress <= 100) {
            currentProgress ++
            postInvalidateDelayed(34)
        }
    }
}