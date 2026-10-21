package com.example.thcsedu.ui.sodiem

import com.example.thcsedu.data.local.entity.BangDiemEntity
import com.example.thcsedu.data.local.entity.HocSinhEntity
import com.example.thcsedu.data.local.entity.LichSuSuaDiemEntity
import com.example.thcsedu.data.util.Resource
import com.example.thcsedu.domain.repository.BangDiemRepository
import com.example.thcsedu.domain.repository.LichSuSuaDiemRepository
import com.example.thcsedu.domain.usecase.TinhDiemVaXepLoaiUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SoDiemViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeBangDiemRepository
    private lateinit var fakeAuditRepository: FakeLichSuSuaDiemRepository
    private lateinit var tinhDiemUseCase: TinhDiemVaXepLoaiUseCase
    private lateinit var viewModel: SoDiemViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeBangDiemRepository()
        fakeAuditRepository = FakeLichSuSuaDiemRepository()
        tinhDiemUseCase = TinhDiemVaXepLoaiUseCase()
        viewModel = SoDiemViewModel(fakeRepository, fakeAuditRepository, tinhDiemUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadSoDiem updates uiState with combined student and grade data`() = runTest {
        // Given
        val hocSinhList = listOf(
            HocSinhEntity("HS01", "Nguyen Van A", "2010-01-01", "Nam", "6A1", "DangHoc"),
            HocSinhEntity("HS02", "Tran Thi B", "2010-02-02", "Nu", "6A1", "DangHoc")
        )
        val bangDiemList = listOf(
            BangDiemEntity(
                maHocSinh = "HS01",
                maMonHoc = "TOAN",
                hocKy = 1,
                namHoc = "2024-2025",
                dsDiemTX = listOf(8.0f),
                diemGiuaKy = 7.0f,
                diemCuoiKy = 9.0f,
                nhanXet = "Good"
            )
        )

        fakeRepository.setHocSinhList(hocSinhList)
        fakeRepository.setBangDiemList(bangDiemList)

        // When
        viewModel.loadSoDiem("6A1", "TOAN", 1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.dsHocSinhDiem.size)
        assertEquals("Nguyen Van A", state.dsHocSinhDiem[0].hoTen)
        assertEquals(7.0f, state.dsHocSinhDiem[0].diemGiuaKy ?: 0f, 0.01f)
    }

    @Test
    fun `updateDiem updates local state and saves correctly`() = runTest {
        // Given
        val hocSinhList = listOf(
            HocSinhEntity("HS01", "Nguyen Van A", "2010-01-01", "Nam", "6A1", "DangHoc")
        )
        fakeRepository.setHocSinhList(hocSinhList)
        viewModel.loadSoDiem("6A1", "TOAN", 1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateDiemGK("HS01", 9.0f)
        viewModel.updateDiemCK("HS01", 8.5f)

        // When
        viewModel.saveBangDiem()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val savedList = fakeRepository.savedBangDiemList
        assertEquals(1, savedList.size)
        assertEquals("HS01", savedList[0].maHocSinh)
        assertEquals("TOAN", savedList[0].maMonHoc)
        assertEquals(9.0f, savedList[0].diemGiuaKy)
        assertEquals(8.5f, savedList[0].diemCuoiKy)
    }
}

class FakeBangDiemRepository : BangDiemRepository {
    private val hocSinhFlow = MutableStateFlow<List<HocSinhEntity>>(emptyList())
    private val bangDiemFlow = MutableStateFlow<List<BangDiemEntity>>(emptyList())
    var savedBangDiemList = listOf<BangDiemEntity>()

    fun setHocSinhList(list: List<HocSinhEntity>) {
        hocSinhFlow.value = list
    }

    fun setBangDiemList(list: List<BangDiemEntity>) {
        bangDiemFlow.value = list
    }

    override fun getBangDiem(maLop: String, maMonHoc: String, hocKy: Int): Flow<List<BangDiemEntity>> {
        return bangDiemFlow
    }

    override fun getHocSinhByLop(maLop: String): Flow<List<HocSinhEntity>> {
        return hocSinhFlow
    }

    override suspend fun saveBangDiemList(dsBangDiem: List<BangDiemEntity>) {
        savedBangDiemList = dsBangDiem
    }

    override fun getBangDiemWithNetwork(
        classId: String,
        maMonHoc: String,
        hocKy: Int
    ): Flow<Resource<List<BangDiemEntity>>> {
        return flowOf(Resource.Success(bangDiemFlow.value))
    }

    override suspend fun syncLocalChanges(): Result<Unit> {
        return Result.success(Unit)
    }
}

class FakeLichSuSuaDiemRepository : LichSuSuaDiemRepository {
    val auditLogs = mutableListOf<LichSuSuaDiemEntity>()

    override fun getHistory(maHocSinh: String, maMonHoc: String): Flow<List<LichSuSuaDiemEntity>> {
        return flowOf(auditLogs.filter { it.maHocSinh == maHocSinh && it.maMonHoc == maMonHoc })
    }

    override suspend fun logGradeChange(log: LichSuSuaDiemEntity) {
        auditLogs.add(log)
    }
}
