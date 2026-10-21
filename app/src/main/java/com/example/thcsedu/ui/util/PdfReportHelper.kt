package com.example.thcsedu.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.thcsedu.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PdfSubjectScore(
    val subjectName: String,
    val diemTX: String,
    val diemGK: String,
    val diemCK: String,
    val diemTB: String,
    val nhanXet: String
)

data class StudentPdfReportData(
    val studentName: String = "Nguyễn Văn An",
    val studentId: String = "HS01",
    val className: String = "9A1",
    val dob: String = "12/05/2011",
    val teacherName: String = "Nguyễn Văn Trọng",
    val schoolYear: String = "2026-2027",
    val semesterName: String = "HỌC KỲ I",
    val subjects: List<PdfSubjectScore> = emptyList(),
    val overallGpa: Float = 8.5f,
    val academicRank: String = "Giỏi",
    val conduct: String = "Tốt",
    val generalComment: String = "Học sinh ngoan ngoãn, có tinh thần tự giác cao trong học tập, chấp hành tốt nội quy nhà trường."
)

object PdfReportHelper {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842

    fun generateGradeReportPdf(data: StudentPdfReportData): PdfDocument {
        return generateGradeReportPdfInternal(null, data)
    }

    fun generateGradeReportPdf(context: Context, data: StudentPdfReportData): PdfDocument {
        return generateGradeReportPdfInternal(context, data)
    }

    private fun generateGradeReportPdfInternal(context: Context?, data: StudentPdfReportData): PdfDocument {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }
        val strokePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = Color.BLACK
            strokeWidth = 1f
        }

        val startX = 40f
        val endX = 555f

        // Draw Logo if available
        var textStartX = startX
        if (context != null) {
            try {
                val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_app_logo)
                if (logoBitmap != null) {
                    val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 45, 45, true)
                    canvas.drawBitmap(scaledLogo, startX, 32f, paint)
                    textStartX = startX + 55f
                }
            } catch (_: Exception) {}
        }

        // 1. Header Section
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 10f
        paint.color = Color.rgb(30, 58, 138)
        canvas.drawText("SỞ GD&ĐT TP. HỒ CHÍ MINH", textStartX, 45f, paint)
        canvas.drawText("TRƯỜNG THCS EDU", textStartX, 60f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 9f
        paint.color = Color.DKGRAY
        val currentDateStr = SimpleDateFormat("'Ngày' dd 'tháng' MM 'năm' yyyy", Locale.forLanguageTag("vi-VN")).format(Date())
        val dateWidth = paint.measureText("TP. HCM, $currentDateStr")
        canvas.drawText("TP. HCM, $currentDateStr", endX - dateWidth, 45f, paint)

        // Title
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 16f
        paint.color = Color.rgb(30, 58, 138)
        val titleText = "PHIẾU LIÊN LẠC ${data.semesterName.uppercase()}"
        val subTitleText = "NĂM HỌC ${data.schoolYear}"
        val titleWidth = paint.measureText(titleText)
        canvas.drawText(titleText, (PAGE_WIDTH - titleWidth) / 2f, 95f, paint)

        paint.textSize = 12f
        val subTitleWidth = paint.measureText(subTitleText)
        canvas.drawText(subTitleText, (PAGE_WIDTH - subTitleWidth) / 2f, 115f, paint)

        // Divider
        strokePaint.strokeWidth = 1.5f
        strokePaint.color = Color.rgb(30, 58, 138)
        canvas.drawLine(startX, 125f, endX, 125f, strokePaint)

        // 2. Student Info Box
        val infoBoxTop = 135f
        val infoBoxBottom = 195f
        val bgPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.rgb(243, 244, 246)
        }
        canvas.drawRoundRect(RectF(startX, infoBoxTop, endX, infoBoxBottom), 8f, 8f, bgPaint)

        strokePaint.color = Color.rgb(209, 213, 219)
        strokePaint.strokeWidth = 1f
        canvas.drawRoundRect(RectF(startX, infoBoxTop, endX, infoBoxBottom), 8f, 8f, strokePaint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10f
        paint.color = Color.BLACK

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Họ và tên học sinh:", startX + 15f, infoBoxTop + 22f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(data.studentName, startX + 115f, infoBoxTop + 22f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Mã HS:", startX + 280f, infoBoxTop + 22f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(data.studentId, startX + 325f, infoBoxTop + 22f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Lớp:", startX + 410f, infoBoxTop + 22f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(data.className, startX + 440f, infoBoxTop + 22f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Ngày sinh:", startX + 15f, infoBoxTop + 45f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(data.dob, startX + 80f, infoBoxTop + 45f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Giáo viên chủ nhiệm:", startX + 280f, infoBoxTop + 45f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(data.teacherName, startX + 395f, infoBoxTop + 45f, paint)

        // 3. Grade Table
        val tableTop = 210f
        val colWidths = floatArrayOf(30f, 120f, 90f, 55f, 55f, 55f, 110f)
        val colHeaders = arrayOf("STT", "Môn Học", "ĐĐGtx", "ĐGK", "ĐCK", "ĐTBmhk", "Nhận Xét")

        val headerHeight = 26f
        val headerBg = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.rgb(30, 58, 138)
        }
        canvas.drawRect(RectF(startX, tableTop, endX, tableTop + headerHeight), headerBg)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 9f
        paint.color = Color.WHITE

        var currentX = startX
        for (i in colHeaders.indices) {
            val width = colWidths[i]
            val text = colHeaders[i]
            val tWidth = paint.measureText(text)
            canvas.drawText(text, currentX + (width - tWidth) / 2f, tableTop + 17f, paint)
            currentX += width
        }

        var currentY = tableTop + headerHeight
        val rowHeight = 22f

        strokePaint.color = Color.rgb(156, 163, 175)
        strokePaint.strokeWidth = 0.8f

        val rowBgEven = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.WHITE
        }
        val rowBgOdd = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.rgb(249, 250, 251)
        }

        paint.color = Color.BLACK
        paint.textSize = 8.5f

        data.subjects.forEachIndexed { index, item ->
            val rowTop = currentY
            val rowBottom = currentY + rowHeight

            canvas.drawRect(RectF(startX, rowTop, endX, rowBottom), if (index % 2 == 0) rowBgEven else rowBgOdd)

            currentX = startX

            // STT
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            val sttStr = (index + 1).toString()
            canvas.drawText(sttStr, currentX + (colWidths[0] - paint.measureText(sttStr)) / 2f, rowTop + 15f, paint)
            currentX += colWidths[0]

            // Mon hoc
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(item.subjectName, currentX + 6f, rowTop + 15f, paint)
            currentX += colWidths[1]

            // Diem TX
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(item.diemTX, currentX + (colWidths[2] - paint.measureText(item.diemTX)) / 2f, rowTop + 15f, paint)
            currentX += colWidths[2]

            // Diem GK
            canvas.drawText(item.diemGK, currentX + (colWidths[3] - paint.measureText(item.diemGK)) / 2f, rowTop + 15f, paint)
            currentX += colWidths[3]

            // Diem CK
            canvas.drawText(item.diemCK, currentX + (colWidths[4] - paint.measureText(item.diemCK)) / 2f, rowTop + 15f, paint)
            currentX += colWidths[4]

            // Diem TB
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.color = Color.rgb(30, 58, 138)
            canvas.drawText(item.diemTB, currentX + (colWidths[5] - paint.measureText(item.diemTB)) / 2f, rowTop + 15f, paint)
            currentX += colWidths[5]

            // Nhan xet
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.color = Color.DKGRAY
            var comment = item.nhanXet
            if (paint.measureText(comment) > colWidths[6] - 8f) {
                comment = comment.take(18) + "..."
            }
            canvas.drawText(comment, currentX + 4f, rowTop + 15f, paint)

            paint.color = Color.BLACK
            currentY += rowHeight
        }

        val tableBottom = currentY
        canvas.drawRect(RectF(startX, tableTop, endX, tableBottom), strokePaint)

        currentX = startX
        for (i in 0 until colWidths.size - 1) {
            currentX += colWidths[i]
            canvas.drawLine(currentX, tableTop, currentX, tableBottom, strokePaint)
        }

        var gridY = tableTop + headerHeight
        for (i in data.subjects.indices) {
            canvas.drawLine(startX, gridY, endX, gridY, strokePaint)
            gridY += rowHeight
        }

        // 4. Summary Section
        val summaryTop = tableBottom + 15f
        val summaryBottom = summaryTop + 50f

        val summaryBg = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.rgb(238, 242, 255)
        }
        canvas.drawRoundRect(RectF(startX, summaryTop, endX, summaryBottom), 6f, 6f, summaryBg)

        strokePaint.color = Color.rgb(199, 210, 254)
        canvas.drawRoundRect(RectF(startX, summaryTop, endX, summaryBottom), 6f, 6f, strokePaint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 10f
        paint.color = Color.rgb(30, 58, 138)

        canvas.drawText("ĐIỂM TRUNG BÌNH CHUNG: ${data.overallGpa}", startX + 15f, summaryTop + 20f, paint)
        canvas.drawText("HỌC LỰC: ${data.academicRank.uppercase()}", startX + 220f, summaryTop + 20f, paint)
        canvas.drawText("HẠNH KIỂM: ${data.conduct.uppercase()}", startX + 380f, summaryTop + 20f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 9f
        paint.color = Color.DKGRAY
        canvas.drawText("Đánh giá chung: ${data.generalComment}", startX + 15f, summaryTop + 38f, paint)

        // 5. Footer & Signatures
        val footerY = summaryBottom + 30f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 10f
        paint.color = Color.BLACK

        val leftSigX = startX + 40f
        canvas.drawText("CHỮ KÝ PHỤ HUYNH", leftSigX, footerY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        paint.textSize = 8.5f
        paint.color = Color.GRAY
        canvas.drawText("(Ký và ghi rõ họ tên)", leftSigX - 5f, footerY + 15f, paint)

        val rightSigX = endX - 150f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 10f
        paint.color = Color.BLACK
        canvas.drawText("GIÁO VIÊN CHỦ NHIỆM", rightSigX, footerY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        paint.textSize = 8.5f
        paint.color = Color.GRAY
        canvas.drawText("(Ký và ghi rõ họ tên)", rightSigX + 10f, footerY + 15f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 10f
        paint.color = Color.BLACK
        canvas.drawText(data.teacherName, rightSigX + 10f, footerY + 75f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 7.5f
        paint.color = Color.LTGRAY
        val watermark = "THCSEDU System - Phiếu liên lạc điện tử chính thức. Mã xác thực: ${data.studentId}-${data.schoolYear}"
        val wWidth = paint.measureText(watermark)
        canvas.drawText(watermark, (PAGE_WIDTH - wWidth) / 2f, PAGE_HEIGHT - 25f, paint)

        pdfDocument.finishPage(page)
        return pdfDocument
    }
}
