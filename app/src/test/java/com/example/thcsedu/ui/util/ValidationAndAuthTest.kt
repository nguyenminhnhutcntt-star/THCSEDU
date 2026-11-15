package com.example.thcsedu.ui.util

import com.example.thcsedu.domain.model.XepLoaiEnum
import com.example.thcsedu.domain.usecase.TinhDiemVaXepLoaiUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationAndAuthTest {

    // 1. Kiểm thử băm mật khẩu và xác thực
    @Test
    fun `hashPassword produces consistent SHA-256 hash and verifies correctly`() {
        val rawPassword = "secretPassword123"
        val hash = PasswordUtil.hashPassword(rawPassword)

        assertTrue(hash.isNotEmpty())
        assertEquals(64, hash.length) // SHA-256 hex string length is 64 chars

        // Verify correct password
        assertTrue(PasswordUtil.verifyPassword("secretPassword123", hash))

        // Verify incorrect password
        assertFalse(PasswordUtil.verifyPassword("wrongPassword", hash))
        assertFalse(PasswordUtil.verifyPassword("secretPassword1234", hash))
    }

    @Test
    fun `hashPassword produces different hashes for different passwords`() {
        val hash1 = PasswordUtil.hashPassword("123456")
        val hash2 = PasswordUtil.hashPassword("123457")
        assertNotEquals(hash1, hash2)
    }

    // 2. Kiểm thử kiểm tra điểm trong khoảng 0-10
    @Test
    fun `isValidScore validates score correctly within 0 to 10 range`() {
        assertTrue(ValidationUtil.isValidScore(0.0f))
        assertTrue(ValidationUtil.isValidScore(5.5f))
        assertTrue(ValidationUtil.isValidScore(10.0f))

        assertFalse(ValidationUtil.isValidScore(-0.1f))
        assertFalse(ValidationUtil.isValidScore(10.1f))
        assertFalse(ValidationUtil.isValidScore(15.0f))
        assertFalse(ValidationUtil.isValidScore(null))
    }

    @Test
    fun `isValidScoreString validates score strings correctly`() {
        assertTrue(ValidationUtil.isValidScoreString("0"))
        assertTrue(ValidationUtil.isValidScoreString("8.5"))
        assertTrue(ValidationUtil.isValidScoreString("10"))

        assertFalse(ValidationUtil.isValidScoreString("-1"))
        assertFalse(ValidationUtil.isValidScoreString("10.5"))
        assertFalse(ValidationUtil.isValidScoreString("abc"))
        assertFalse(ValidationUtil.isValidScoreString(""))
    }

    // 3. Kiểm thử ngày hợp lệ và khoảng ngày
    @Test
    fun `isValidDate validates real calendar dates`() {
        assertTrue(ValidationUtil.isValidDate("2026-10-14"))
        assertTrue(ValidationUtil.isValidDate("2024-02-29")) // Leap year
        assertTrue(ValidationUtil.isValidDate("14/10/2026"))

        // Invalid dates
        assertFalse(ValidationUtil.isValidDate("2026-02-29")) // 2026 is not leap year
        assertFalse(ValidationUtil.isValidDate("2026-02-30")) // Feb 30 does not exist
        assertFalse(ValidationUtil.isValidDate("2026-13-01")) // Month 13 does not exist
        assertFalse(ValidationUtil.isValidDate("2026-00-10"))
        assertFalse(ValidationUtil.isValidDate("invalid-date"))
        assertFalse(ValidationUtil.isValidDate(""))
        assertFalse(ValidationUtil.isValidDate(null))
    }

    @Test
    fun `isValidDateRange validates start date less than or equal to end date`() {
        assertTrue(ValidationUtil.isValidDateRange("2026-10-10", "2026-10-12"))
        assertTrue(ValidationUtil.isValidDateRange("2026-10-10", "2026-10-10")) // Same day

        assertFalse(ValidationUtil.isValidDateRange("2026-10-15", "2026-10-10")) // Start after end
        assertFalse(ValidationUtil.isValidDateRange("2026-02-30", "2026-03-01")) // Invalid start date
    }

    // 4. Kiểm thử xếp loại học lực 4 mức theo Thông tư 22
    @Test
    fun `xepLoaiHocLuc returns correct 4 levels according to Thong Tu 22`() {
        val useCase = TinhDiemVaXepLoaiUseCase()

        // Mức Tốt: Tất cả môn nhận xét Đạt, tất cả ĐTB >= 6.5, ít nhất 6 môn >= 8.0
        val scoresTot = listOf(8.5f, 9.0f, 8.0f, 8.2f, 8.0f, 8.8f, 7.0f, 6.8f)
        val passAll = List(8) { true }
        assertEquals(XepLoaiEnum.TOT, useCase.xepLoaiHocLuc(scoresTot, passAll))

        // Mức Khá: Tất cả môn nhận xét Đạt, tất cả ĐTB >= 5.0, ít nhất 6 môn >= 6.5
        val scoresKha = listOf(7.0f, 6.8f, 6.5f, 6.5f, 6.5f, 6.6f, 5.5f, 5.0f)
        assertEquals(XepLoaiEnum.KHA, useCase.xepLoaiHocLuc(scoresKha, passAll))

        // Mức Đạt: Tối đa 1 môn nhận xét Chưa đạt, ít nhất 6 môn >= 5.0, tất cả >= 3.5
        val scoresDat = listOf(5.5f, 5.0f, 5.2f, 5.0f, 5.0f, 5.0f, 4.0f, 3.8f)
        val passWithOneFail = listOf(true, true, true, true, true, true, true, false)
        assertEquals(XepLoaiEnum.DAT, useCase.xepLoaiHocLuc(scoresDat, passWithOneFail))

        // Mức Chưa Đạt
        val scoresChuaDat = listOf(4.0f, 4.5f, 3.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f)
        assertEquals(XepLoaiEnum.CHUA_DAT, useCase.xepLoaiHocLuc(scoresChuaDat, passAll))
    }
}
