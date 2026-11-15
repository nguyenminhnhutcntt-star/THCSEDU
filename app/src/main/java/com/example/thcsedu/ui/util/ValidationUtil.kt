package com.example.thcsedu.ui.util

import com.example.thcsedu.data.local.dao.HocSinhDao
import java.text.SimpleDateFormat
import java.util.Locale

object ValidationUtil {

    /**
     * Kiểm tra điểm số phải trong khoảng từ 0.0 đến 10.0
     */
    fun isValidScore(score: Float?): Boolean {
        return score != null && score in 0.0f..10.0f
    }

    /**
     * Kiểm tra chuỗi điểm số hợp lệ trong khoảng 0.0 đến 10.0
     */
    fun isValidScoreString(scoreStr: String): Boolean {
        val score = scoreStr.toFloatOrNull() ?: return false
        return isValidScore(score)
    }

    /**
     * Kiểm tra định dạng và tính hợp lệ thực tế của ngày tháng (yyyy-MM-dd hoặc dd/MM/yyyy)
     */
    fun isValidDate(dateStr: String?): Boolean {
        if (dateStr.isNullOrBlank()) return false
        val formats = listOf("yyyy-MM-dd", "dd/MM/yyyy")
        for (pattern in formats) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.getDefault())
                sdf.isLenient = false
                sdf.parse(dateStr.trim())
                return true
            } catch (e: Exception) {
                // Thử format tiếp theo
            }
        }
        return false
    }

    /**
     * Kiểm tra khoảng ngày hợp lệ: từ ngày <= đến ngày
     */
    fun isValidDateRange(tuNgay: String?, denNgay: String?): Boolean {
        if (tuNgay.isNullOrBlank() || denNgay.isNullOrBlank()) return false
        if (!isValidDate(tuNgay) || !isValidDate(denNgay)) return false

        val patternTu = if (tuNgay.contains("-")) "yyyy-MM-dd" else "dd/MM/yyyy"
        val patternDen = if (denNgay.contains("-")) "yyyy-MM-dd" else "dd/MM/yyyy"

        return try {
            val sdf1 = SimpleDateFormat(patternTu, Locale.getDefault()).apply { isLenient = false }
            val sdf2 = SimpleDateFormat(patternDen, Locale.getDefault()).apply { isLenient = false }
            val d1 = sdf1.parse(tuNgay.trim())
            val d2 = sdf2.parse(denNgay.trim())
            d1 != null && d2 != null && !d1.after(d2)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Kiểm tra mã học sinh phải tồn tại trong cơ sở dữ liệu
     */
    suspend fun checkStudentExists(hocSinhDao: HocSinhDao, maHocSinh: String?): Boolean {
        if (maHocSinh.isNullOrBlank()) return false
        return hocSinhDao.getHocSinhByIdOnce(maHocSinh.trim().uppercase()) != null
    }
}
