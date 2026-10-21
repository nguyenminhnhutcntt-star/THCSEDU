package com.example.thcsedu.domain.usecase

import com.example.thcsedu.domain.model.XepLoaiEnum
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TinhDiemVaXepLoaiUseCaseTest {

    private lateinit var useCase: TinhDiemVaXepLoaiUseCase

    @Before
    fun setUp() {
        useCase = TinhDiemVaXepLoaiUseCase()
    }

    // --- Tests for tinhDiemTBMonHK ---

    @Test
    fun `tinhDiemTBMonHK returns null when diemGK is null`() {
        val result = useCase.tinhDiemTBMonHK(
            dsDiemTX = listOf(8.0f, 7.5f),
            diemGK = null,
            diemCK = 8.0f
        )
        assertNull(result)
    }

    @Test
    fun `tinhDiemTBMonHK returns null when diemCK is null`() {
        val result = useCase.tinhDiemTBMonHK(
            dsDiemTX = listOf(8.0f, 7.5f),
            diemGK = 7.0f,
            diemCK = null
        )
        assertNull(result)
    }

    @Test
    fun `tinhDiemTBMonHK returns null when dsDiemTX is empty`() {
        val result = useCase.tinhDiemTBMonHK(
            dsDiemTX = emptyList(),
            diemGK = 7.0f,
            diemCK = 8.0f
        )
        assertNull(result)
    }

    @Test
    fun `tinhDiemTBMonHK calculates and rounds correctly`() {
        // sumTX = 33.0 (4 entries), 2*GK = 16.0, 3*CK = 25.5
        // total = 74.5, count = 4 + 5 = 9
        // 74.5 / 9 = 8.2777... -> 8.3
        val result1 = useCase.tinhDiemTBMonHK(
            dsDiemTX = listOf(8.0f, 9.0f, 7.5f, 8.5f),
            diemGK = 8.0f,
            diemCK = 8.5f
        )
        assertEquals(8.3f, result1!!, 0.001f)

        // Perfect scores: total = 20 + 20 + 30 = 70, count = 2 + 5 = 7
        // 70 / 7 = 10.0
        val result2 = useCase.tinhDiemTBMonHK(
            dsDiemTX = listOf(10.0f, 10.0f),
            diemGK = 10.0f,
            diemCK = 10.0f
        )
        assertEquals(10.0f, result2!!, 0.001f)

        // Test rounding: total = 15.6 + 12.0 + 24.0 = 51.6, count = 3 + 5 = 8
        // 51.6 / 8 = 6.45 -> rounds to 6.5
        val result3 = useCase.tinhDiemTBMonHK(
            dsDiemTX = listOf(5.0f, 5.0f, 5.6f),
            diemGK = 6.0f,
            diemCK = 8.0f
        )
        assertEquals(6.5f, result3!!, 0.001f)
    }

    // --- Tests for xepLoaiHocLuc ---

    @Test
    fun `xepLoaiHocLuc returns TOT when all conditions are satisfied`() {
        val dsNhanXet = listOf(true, true)
        // 8 subjects, all >= 6.5f, 6 subjects >= 8.0f
        val dsDTBMon = listOf(8.5f, 8.0f, 8.2f, 9.0f, 8.0f, 8.5f, 7.0f, 6.5f)

        val result = useCase.xepLoaiHocLuc(dsDTBMon, dsNhanXet)
        assertEquals(XepLoaiEnum.TOT, result)
    }

    @Test
    fun `xepLoaiHocLuc falls back to KHA when TOT count of 8_0 is less than 6`() {
        val dsNhanXet = listOf(true, true)
        // 8 subjects, all >= 6.5f, but only 5 subjects >= 8.0f
        val dsDTBMon = listOf(8.5f, 8.0f, 8.2f, 9.0f, 8.0f, 7.5f, 7.0f, 6.5f)

        val result = useCase.xepLoaiHocLuc(dsDTBMon, dsNhanXet)
        assertEquals(XepLoaiEnum.KHA, result)
    }

    @Test
    fun `xepLoaiHocLuc returns KHA when all conditions for KHA are satisfied`() {
        val dsNhanXet = listOf(true)
        // 8 subjects, all >= 5.0f, 6 subjects >= 6.5f
        val dsDTBMon = listOf(7.0f, 6.8f, 6.5f, 7.5f, 8.0f, 6.5f, 5.5f, 5.0f)

        val result = useCase.xepLoaiHocLuc(dsDTBMon, dsNhanXet)
        assertEquals(XepLoaiEnum.KHA, result)
    }

    @Test
    fun `xepLoaiHocLuc returns DAT when 1 comment is false and grade conditions met`() {
        val dsNhanXet = listOf(true, false) // 1 false comment (<= 1)
        // 8 subjects, count >= 5.0f is 6 (6.0, 5.5, 5.0, 5.2, 5.8, 5.0), all >= 3.5f
        val dsDTBMon = listOf(6.0f, 5.5f, 5.0f, 5.2f, 5.8f, 5.0f, 4.0f, 3.5f)

        val result = useCase.xepLoaiHocLuc(dsDTBMon, dsNhanXet)
        assertEquals(XepLoaiEnum.DAT, result)
    }

    @Test
    fun `xepLoaiHocLuc returns CHUA_DAT when more than 1 comment is false`() {
        val dsNhanXet = listOf(false, false) // 2 false comments (> 1)
        val dsDTBMon = listOf(8.5f, 8.0f, 8.2f, 9.0f, 8.0f, 8.5f, 7.0f, 6.5f)

        val result = useCase.xepLoaiHocLuc(dsDTBMon, dsNhanXet)
        assertEquals(XepLoaiEnum.CHUA_DAT, result)
    }

    @Test
    fun `xepLoaiHocLuc returns CHUA_DAT when any subject is below 3_5`() {
        val dsNhanXet = listOf(true)
        // 8 subjects, 7 subjects >= 8.0f, but 1 subject is 3.0f (< 3.5f)
        val dsDTBMon = listOf(8.5f, 8.0f, 8.2f, 9.0f, 8.0f, 8.5f, 8.0f, 3.0f)

        val result = useCase.xepLoaiHocLuc(dsDTBMon, dsNhanXet)
        assertEquals(XepLoaiEnum.CHUA_DAT, result)
    }

    @Test
    fun `xepLoaiHocLuc returns CHUA_DAT when count of subjects at least 5_0 is less than 6`() {
        val dsNhanXet = listOf(true)
        // Only 5 subjects >= 5.0f, remaining 3 subjects are 4.0f (>= 3.5f)
        val dsDTBMon = listOf(6.0f, 5.5f, 5.0f, 5.2f, 5.8f, 4.0f, 4.0f, 4.0f)

        val result = useCase.xepLoaiHocLuc(dsDTBMon, dsNhanXet)
        assertEquals(XepLoaiEnum.CHUA_DAT, result)
    }
}
