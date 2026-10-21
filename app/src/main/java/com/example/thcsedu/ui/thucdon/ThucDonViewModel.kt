package com.example.thcsedu.ui.thucdon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.dao.SuatAnDao
import com.example.thcsedu.data.local.dao.ThucDonDao
import com.example.thcsedu.data.local.entity.SuatAnEntity
import com.example.thcsedu.data.local.entity.ThucDonEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ThucDonUiState(
    val thucDonList: List<ThucDonEntity> = emptyList(),
    val suatAnList: List<SuatAnEntity> = emptyList(),
    val selectedStudentId: String = "HS01",
    val soBuaDaCat: Int = 0,
    val tongTienHoan: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ThucDonViewModel @Inject constructor(
    private val thucDonDao: ThucDonDao,
    private val suatAnDao: SuatAnDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ThucDonUiState())
    val uiState: StateFlow<ThucDonUiState> = _uiState.asStateFlow()

    init {
        loadData("HS01")
    }

    fun loadData(maHocSinh: String) {
        _uiState.update { it.copy(isLoading = true, selectedStudentId = maHocSinh) }
        viewModelScope.launch {
            try {
                // Ensure default data is seeded if empty
                val existingMenu = thucDonDao.getAllThucDonList()
                if (existingMenu.isEmpty()) {
                    val defaultMenu = listOf(
                        ThucDonEntity(ngay = "2026-10-12", thuInWeek = "Thứ 2", buaTrua = "Cơm trắng, Thịt kho tàu, Canh bí đao", buaPhu = "Sữa chua hạt dẻ", calo = 680),
                        ThucDonEntity(ngay = "2026-10-13", thuInWeek = "Thứ 3", buaTrua = "Cơm trắng, Gà rán mật ong, Canh chua cá", buaPhu = "Bánh flan", calo = 710),
                        ThucDonEntity(ngay = "2026-10-14", thuInWeek = "Thứ 4", buaTrua = "Cơm trắng, Thịt bò xào hành tây, Canh rau ngót", buaPhu = "Sữa đậu nành", calo = 650),
                        ThucDonEntity(ngay = "2026-10-15", thuInWeek = "Thứ 5", buaTrua = "Cơm trắng, Cá chiên xù sốt cà, Canh khoai mỡ", buaPhu = "Chè đậu đen", calo = 690),
                        ThucDonEntity(ngay = "2026-10-16", thuInWeek = "Thứ 6", buaTrua = "Cơm trắng, Sườn xào chua ngọt, Canh bầu nấu tôm", buaPhu = "Sữa tươi cacao", calo = 720)
                    )
                    thucDonDao.insertThucDonList(defaultMenu)
                }

                val existingSuatAn = suatAnDao.getSuatAnListByStudent(maHocSinh)
                if (existingSuatAn.isEmpty()) {
                    val defaultSuatAn = listOf(
                        SuatAnEntity(maHocSinh = maHocSinh, ngay = "2026-10-12", trangThai = "DA_DANG_KY"),
                        SuatAnEntity(maHocSinh = maHocSinh, ngay = "2026-10-13", trangThai = "DA_DANG_KY"),
                        SuatAnEntity(maHocSinh = maHocSinh, ngay = "2026-10-14", trangThai = "DA_HUY"),
                        SuatAnEntity(maHocSinh = maHocSinh, ngay = "2026-10-15", trangThai = "DA_DANG_KY"),
                        SuatAnEntity(maHocSinh = maHocSinh, ngay = "2026-10-16", trangThai = "DA_DANG_KY")
                    )
                    suatAnDao.insertSuatAnList(defaultSuatAn)
                }

                thucDonDao.getAllThucDon().collect { menuList ->
                    suatAnDao.getSuatAnByStudent(maHocSinh).collect { suatAnList ->
                        val thangPattern = "%2026-10-%"
                        val soBua = suatAnDao.getSoBuaDaCatThang(maHocSinh, thangPattern) ?: 0
                        val tongTien = suatAnDao.getTongTienHoanThang(maHocSinh, thangPattern) ?: 0.0

                        _uiState.update {
                            it.copy(
                                thucDonList = menuList,
                                suatAnList = suatAnList,
                                soBuaDaCat = soBua,
                                tongTienHoan = tongTien,
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    fun toggleMealStatus(ngay: String) {
        viewModelScope.launch {
            val studentId = _uiState.value.selectedStudentId
            val current = suatAnDao.getSuatAnByDate(studentId, ngay)
            val newStatus = if (current?.trangThai == "DA_HUY") "DA_DANG_KY" else "DA_HUY"
            if (current != null) {
                suatAnDao.updateTrangThaiByDate(studentId, ngay, newStatus)
            } else {
                suatAnDao.insertSuatAn(
                    SuatAnEntity(maHocSinh = studentId, ngay = ngay, trangThai = newStatus)
                )
            }
            val thangPattern = "%2026-10-%"
            val soBua = suatAnDao.getSoBuaDaCatThang(studentId, thangPattern) ?: 0
            val tongTien = suatAnDao.getTongTienHoanThang(studentId, thangPattern) ?: 0.0
            _uiState.update { it.copy(soBuaDaCat = soBua, tongTienHoan = tongTien) }
        }
    }
}
