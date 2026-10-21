package com.example.thcsedu.ui.util

import android.graphics.Bitmap
import android.graphics.Color

object QrCodeUtil {
    fun generateQrBitmap(text: String, size: Int = 512): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val paint = android.graphics.Paint().apply {
            color = Color.BLACK
            style = android.graphics.Paint.Style.FILL
        }

        val modules = 25
        val moduleSize = size.toFloat() / modules

        fun drawFinderPattern(startX: Int, startY: Int) {
            for (y in 0..6) {
                for (x in 0..6) {
                    val isOuter = x == 0 || x == 6 || y == 0 || y == 6
                    val isInner = x in 2..4 && y in 2..4
                    if (isOuter || isInner) {
                        canvas.drawRect(
                            (startX + x) * moduleSize,
                            (startY + y) * moduleSize,
                            (startX + x + 1) * moduleSize,
                            (startY + y + 1) * moduleSize,
                            paint
                        )
                    }
                }
            }
        }

        drawFinderPattern(0, 0)
        drawFinderPattern(modules - 7, 0)
        drawFinderPattern(0, modules - 7)

        val hash = text.hashCode()
        for (y in 0 until modules) {
            for (x in 0 until modules) {
                val isTopLeft = x < 8 && y < 8
                val isTopRight = x >= modules - 8 && y < 8
                val isBottomLeft = x < 8 && y >= modules - 8
                if (isTopLeft || isTopRight || isBottomLeft) continue

                val cellHash = (x * 31 + y * 17 + hash)
                if (cellHash % 2 == 0) {
                    canvas.drawRect(
                        x * moduleSize,
                        y * moduleSize,
                        (x + 1) * moduleSize,
                        (y + 1) * moduleSize,
                        paint
                    )
                }
            }
        }

        return bitmap
    }
}
