package com.example.thcsedu.ui.quanly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.dao.HocSinhDao
import com.example.thcsedu.data.local.dao.LopHocDao
import com.example.thcsedu.data.local.dao.MonHocDao
import com.example.thcsedu.data.local.dao.PhanCongGiaoVienDao
import com.example.thcsedu.data.local.entity.HocSinhEntity
import com.example.thcsedu.data.local.entity.LoaiDanhGia
import com.example.thcsedu.data.local.entity.LopEntity
import com.example.thcsedu.data.local.entity.MonHocEntity
import com.example.thcsedu.data.local.entity.PhanCongGiaoVienEntity
import com.example.thcsedu.ui.util.ValidationUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuanLyTruongHocUiState(
    val selectedTab: Int = 0, // 0: Lớp, 1: Học sinh, 2: Môn học, 3: Phân công
    val classes: List<LopEntity> = emptyList(),
    val students: List<HocSinhEntity> = emptyList(),
    val subjects: List<MonHocEntity> = emptyList(),
    val assignments: List<PhanCongGiaoVienEntity> = emptyList(),
    val selectedClassFilter: String = "ALL",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class QuanLyTruongHocViewModel @Inject constructor(
    private val lopHocDao: LopHocDao,
    private val hocSinhDao: HocSinhDao,
    private val monHocDao: MonHocDao,
    private val phanCongDao: PhanCongGiaoVienDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuanLyTruongHocUiState())
    val uiState: StateFlow<QuanLyTruongHocUiState> = _uiState.asStateFlow()

    init {
        loadAllData()
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex, errorMessage = null, successMessage = null) }
    }

    fun setClassFilter(maLop: String) {
        _uiState.update { it.copy(selectedClassFilter = maLop) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun loadAllData() {
        viewModelScope.launch {
            lopHocDao.getAllLop().catch { }.collect { list ->
                _uiState.update { it.copy(classes = list) }
            }
        }
        viewModelScope.launch {
            hocSinhDao.getAllHocSinh().catch { }.collect { list ->
                _uiState.update { it.copy(students = list) }
            }
        }
        viewModelScope.launch {
            monHocDao.getAllMonHoc().catch { }.collect { list ->
                _uiState.update { it.copy(subjects = list) }
            }
        }
        viewModelScope.launch {
            phanCongDao.getAllPhanCong().catch { }.collect { list ->
                _uiState.update { it.copy(assignments = list) }
            }
        }
    }

    // 1. Quản lý Lớp học
    fun saveClass(maLop: String, tenLop: String, nienKhoa: String) {
        val trimmedMa = maLop.trim().uppercase()
        val trimmedTen = tenLop.trim()
        val trimmedNienKhoa = nienKhoa.trim()

        if (trimmedMa.isBlank() || trimmedTen.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Mã lớp và tên lớp không được để trống!") }
            return
        }

        viewModelScope.launch {
            try {
                lopHocDao.insertOrUpdateLop(LopEntity(maLop = trimmedMa, tenLop = trimmedTen, nienKhoa = trimmedNienKhoa))
                _uiState.update { it.copy(successMessage = "Lưu thông tin lớp học thành công!", errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Lỗi: ${e.localizedMessage}") }
            }
        }
    }

    fun deleteClass(lop: LopEntity) {
        viewModelScope.launch {
            try {
                lopHocDao.deleteLop(lop)
                _uiState.update { it.copy(successMessage = "Đã xóa lớp ${lop.tenLop}!", errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Lỗi khi xóa lớp: ${e.localizedMessage}") }
            }
        }
    }

    // 2. Quản lý Học sinh
    fun saveStudent(
        maHocSinh: String,
        hoTen: String,
        ngaySinh: String,
        gioiTinh: String,
        maLop: String,
        trangThai: String = "Đang học"
    ) {
        val trimmedMa = maHocSinh.trim().uppercase()
        val trimmedTen = hoTen.trim()
        val trimmedNgaySinh = ngaySinh.trim()

        if (trimmedMa.isBlank() || trimmedTen.isBlank() || maLop.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập đầy đủ mã HS, họ tên và lớp!") }
            return
        }

        // Kiểm tra ngày sinh hợp lệ
        if (!ValidationUtil.isValidDate(trimmedNgaySinh)) {
            _uiState.update { it.copy(errorMessage = "Ngày sinh không hợp lệ ($trimmedNgaySinh). Định dạng: yyyy-MM-dd hoặc dd/MM/yyyy") }
            return
        }

        viewModelScope.launch {
            try {
                val student = HocSinhEntity(
                    maHocSinh = trimmedMa,
                    hoTen = trimmedTen,
                    ngaySinh = trimmedNgaySinh,
                    gioiTinh = gioiTinh,
                    maLop = maLop,
                    trangThai = trangThai
                )
                hocSinhDao.insertOrUpdateHocSinh(student)
                _uiState.update { it.copy(successMessage = "Lưu thông tin học sinh $trimmedTen thành công!", errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Lỗi: ${e.localizedMessage}") }
            }
        }
    }

    fun deleteStudent(student: HocSinhEntity) {
        viewModelScope.launch {
            try {
                hocSinhDao.deleteHocSinh(student)
                _uiState.update { it.copy(successMessage = "Đã xóa học sinh ${student.hoTen}!", errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Lỗi: ${e.localizedMessage}") }
            }
        }
    }

    // 3. Quản lý Môn học
    fun saveSubject(maMonHoc: String, tenMonHoc: String, loaiDanhGia: LoaiDanhGia = LoaiDanhGia.CHO_DIEM) {
        val trimmedMa = maMonHoc.trim().uppercase()
        val trimmedTen = tenMonHoc.trim()

        if (trimmedMa.isBlank() || trimmedTen.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Mã môn và tên môn không được để trống!") }
            return
        }

        viewModelScope.launch {
            try {
                monHocDao.insertOrUpdateMonHoc(MonHocEntity(maMonHoc = trimmedMa, tenMonHoc = trimmedTen, loaiDanhGia = loaiDanhGia))
                _uiState.update { it.copy(successMessage = "Lưu môn học $trimmedTen thành công!", errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Lỗi: ${e.localizedMessage}") }
            }
        }
    }

    fun deleteSubject(subject: MonHocEntity) {
        viewModelScope.launch {
            try {
                monHocDao.deleteMonHoc(subject)
                _uiState.update { it.copy(successMessage = "Đã xóa môn học ${subject.tenMonHoc}!", errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Lỗi: ${e.localizedMessage}") }
            }
        }
    }

    // 4. Phân công Giáo viên
    fun saveAssignment(
        maGiaoVien: String,
        tenGiaoVien: String,
        maLop: String,
        maMonHoc: String,
        tenMonHoc: String,
        vaiTro: String
    ) {
        if (maGiaoVien.isBlank() || tenGiaoVien.isBlank() || maLop.isBlank() || maMonHoc.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập đầy đủ thông tin phân công!") }
            return
        }

        viewModelScope.launch {
            try {
                val entity = PhanCongGiaoVienEntity(
                    maGiaoVien = maGiaoVien.trim().uppercase(),
                    tenGiaoVien = tenGiaoVien.trim(),
                    maLop = maLop.trim(),
                    maMonHoc = maMonHoc.trim(),
                    tenMonHoc = tenMonHoc.trim(),
                    vaiTro = vaiTro
                )
                phanCongDao.insertPhanCong(entity)
                _uiState.update { it.copy(successMessage = "Phân công thành công cho $tenGiaoVien dạy môn $tenMonHoc lớp $maLop!", errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Lỗi: ${e.localizedMessage}") }
            }
        }
    }

    fun deleteAssignment(assignment: PhanCongGiaoVienEntity) {
        viewModelScope.launch {
            try {
                phanCongDao.deletePhanCong(assignment)
                _uiState.update { it.copy(successMessage = "Đã xóa phân công!", errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Lỗi: ${e.localizedMessage}") }
            }
        }
    }
}
