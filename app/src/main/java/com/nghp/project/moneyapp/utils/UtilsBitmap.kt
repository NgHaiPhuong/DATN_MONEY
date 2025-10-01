package com.nghp.project.moneyapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.callback.ICallBackItem
import com.nghp.project.moneyapp.extensions.dpToPx
import com.nghp.project.moneyapp.extensions.modifyOrientation
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


object UtilsBitmap {

    fun getIconWithCircleBackground(context: Context, @DrawableRes iconRes: Int): Drawable {
        val icon = ContextCompat.getDrawable(context, iconRes) ?: return ColorDrawable(Color.GRAY)
        val iconSize = maxOf(icon.intrinsicWidth, icon.intrinsicHeight)
        val background = ShapeDrawable(OvalShape()).apply {
            paint.color = ContextCompat.getColor(context, R.color.color_F4F9FF)
        }
        val padding = 4f.dpToPx(context).toInt()
        val size = iconSize + padding * 2
        background.setBounds(0, 0, size, size)
        icon.setBounds(padding, padding, size - padding, size - padding)
        val layerDrawable = LayerDrawable(arrayOf(background, icon))
        return layerDrawable
    }

    fun drawIconWithPath(canvas: Canvas, path: Path, paint: Paint?, size: Float, x: Int, y: Int) {
        val rectF = RectF()
        path.computeBounds(rectF, true)
        val scale = if (rectF.width() >= rectF.height()) size / rectF.width()
        else size / rectF.height()
        canvas.translate(x.toFloat(), y.toFloat())
        canvas.scale(scale, scale)
        canvas.drawPath(path, paint!!)
    }

    fun toRGBString(color: Int): String {
        // format: #RRGGBB
        var red = Integer.toHexString(Color.red(color))
        var green = Integer.toHexString(Color.green(color))
        var blue = Integer.toHexString(Color.blue(color))
        if (red.length == 1) red = "0$red"
        if (green.length == 1) green = "0$green"
        if (blue.length == 1) blue = "0$blue"
        return "#$red$green$blue"
    }

    fun toARGBString(alpha: Int, color: Int): String {
        // format: #AARRGGBB
        var hex = Integer.toHexString(alpha).uppercase()
        var red = Integer.toHexString(Color.red(color))
        var green = Integer.toHexString(Color.green(color))
        var blue = Integer.toHexString(Color.blue(color))
        if (hex.length == 1) hex = "0$hex"
        if (red.length == 1) red = "0$red"
        if (green.length == 1) green = "0$green"
        if (blue.length == 1) blue = "0$blue"
        return "#$hex$red$green$blue"
    }

    fun lightenColorIntToHex(color: Int, factor: Float = 0.3f): String {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        val newR = (r + (255 - r) * factor).toInt().coerceIn(0, 255)
        val newG = (g + (255 - g) * factor).toInt().coerceIn(0, 255)
        val newB = (b + (255 - b) * factor).toInt().coerceIn(0, 255)

        return String.format("#%02X%02X%02X", newR, newG, newB)
    }

    fun getRandomPastelColor(): Int {
        val random = java.util.Random()

        // Màu pastel là màu sáng -> giới hạn RGB từ 150 đến 255
        val red = 150 + random.nextInt(106) // 150..255
        val green = 150 + random.nextInt(106)
        val blue = 150 + random.nextInt(106)

        return Color.rgb(red, green, blue)
    }

    fun drawableToBitmap(context: Context, width: Int, height: Int, drawableId: Int): Bitmap? {
        if (width <= 0 || height <= 0 || drawableId == 0) return null
        return drawableToBitmap(width, height, ContextCompat.getDrawable(context, drawableId))
    }

    private fun drawableToBitmap(width: Int, height: Int, drawable: Drawable?): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable == null) return null
        try {
            if (drawable is BitmapDrawable) {
                bitmap = drawable.bitmap
                if (bitmap != null && bitmap.height > 0) {
                    val matrix = Matrix()
                    val scaleWidth = width * 1.0f / bitmap.width
                    val scaleHeight = height * 1.0f / bitmap.height
                    matrix.postScale(scaleWidth, scaleHeight)
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    return bitmap
                }
            }
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.apply {
                setBounds(0, 0, canvas.width, canvas.height)
                draw(canvas)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return bitmap
    }


    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        drawable?.let {
            val bitmap = Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
            if (bitmap != null) {
                val canvas = Canvas(bitmap)
                it.apply {
                    setBounds(0, 0, canvas.width, canvas.height)
                    draw(canvas)
                }
                return bitmap
            }
        }
        return null
    }

    fun getImageSize(context: Context, uri: Uri?): FloatArray {
        try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            val input = context.contentResolver.openInputStream(uri!!)
            BitmapFactory.decodeStream(input, null, options)
            input!!.close()
            return floatArrayOf(options.outWidth.toFloat(), options.outHeight.toFloat())
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
        return floatArrayOf(0f, 0f)
    }

    fun createByteImage(context: Context, uriPic: String?, callBack: ICallBackItem) {
        val bmp: Bitmap
        try {
            bmp = getBitmapFromUri(context, Uri.parse(uriPic))!!.modifyOrientation(context, Uri.parse(uriPic))
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 84, stream)
            callBack.callBack(stream.toByteArray(), -1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun createByteAudio(uriAudio: String?, callBack: ICallBackItem) {
        try {
            val inputStream = FileInputStream(uriAudio)
            val os = ByteArrayOutputStream()
            val buffer = ByteArray(0xFFFF)
            var len = inputStream.read(buffer)
            while (len != -1) {
                os.write(buffer, 0, len)
                len = inputStream.read(buffer)
            }
            callBack.callBack(os.toByteArray(), -1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveBitmapToApp(
        context: Context?,
        bitmap: Bitmap,
        nameFolder: String,
        nameFile: String
    ): String {
        val directory = Utils.getStore(context!!) + "/" + nameFolder + "/"
        val myPath = File(directory, "$nameFile.png")
        try {
            val fos = FileOutputStream(myPath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
            return myPath.path
        } catch (e: Exception) {
            Log.e("SAVE_IMAGE", e.message, e)
        }
        return ""
    }

    fun getBitmapFromUri(context: Context, selectedFileUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(selectedFileUri!!, "r")
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    fun getBitmapFromAsset(context: Context, uriImg: String): Bitmap? {
        val istr: InputStream
        var bitmap: Bitmap? = null
        try {
            istr = context.assets.open(uriImg)
            bitmap = BitmapFactory.decodeStream(istr)
        } catch (e: IOException) {
            // handle exception
            e.printStackTrace()
        }
        return bitmap
    }

    @SuppressLint("Recycle")
    @Throws(IOException::class)
    fun modifyOrientation(context: Context, bitmap: Bitmap, uri: Uri?): Bitmap {
        val `is` = context.contentResolver.openInputStream(uri!!)
        val ei = ExifInterface(`is`!!)
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }

    @Throws(IOException::class)
    fun modifyOrientation(bitmap: Bitmap, fileDescriptor: FileDescriptor?): Bitmap {
        val ei = ExifInterface(fileDescriptor!!)
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }

    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun createFlippedBitmap(source: Bitmap, xFlip: Boolean, yFlip: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.postScale(if (xFlip) -1F else 1F, if (yFlip) -1F else 1F, source.width / 2f, source.height / 2f)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}