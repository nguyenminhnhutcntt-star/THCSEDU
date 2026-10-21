package com.example.thcsedu.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.thcsedu.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AppDatabaseCallback @Inject constructor(
    private val databaseProvider: Provider<AppDatabase>
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val database = databaseProvider.get()
            seedDatabase(database)
        }
    }

    private suspend fun seedDatabase(database: AppDatabase) {
        val lopHocDao = database.lopHocDao()
        val hocSinhDao = database.hocSinhDao()
        val monHocDao = database.monHocDao()
        val bangDiemDao = database.bangDiemDao()
        val hoaDonDao = database.hoaDonHocPhiDao()
        val thongBaoDao = database.thongBaoDao()
        val tinNhanDao = database.tinNhanDao()
        val donNghiDao = database.donXinNghiDao()
        val soDauBaiDao = database.soDauBaiDao()
        val viPhamDao = database.viPhamDao()
        val auditDao = database.lichSuSuaDiemDao()

        // 1. Classes: "9A1" and "9A2"
        val classes = listOf(
            LopEntity("9A1", "Lớp 9A1", "2024-2025"),
            LopEntity("9A2", "Lớp 9A2", "2024-2025")
        )
        lopHocDao.insertOrUpdateLopList(classes)

        // 2. High School Subjects
        val subjects = listOf(
            MonHocEntity("TOAN", "Toán", LoaiDanhGia.CHO_DIEM),
            MonHocEntity("VAN", "Ngữ Văn", LoaiDanhGia.CHO_DIEM),
            MonHocEntity("ANH", "Tiếng Anh", LoaiDanhGia.CHO_DIEM),
            MonHocEntity("KHTN", "Khoa học Tự nhiên", LoaiDanhGia.CHO_DIEM),
            MonHocEntity("LS_DL", "Lịch sử & Địa lý", LoaiDanhGia.CHO_DIEM),
            MonHocEntity("GDCD", "Giáo dục công dân", LoaiDanhGia.CHO_DIEM),
            MonHocEntity("TIN", "Tin học", LoaiDanhGia.CHO_DIEM),
            MonHocEntity("CN", "Công nghệ", LoaiDanhGia.CHO_DIEM)
        )
        monHocDao.insertOrUpdateMonHocList(subjects)

        // 3. 10 Students: 8 in Class 9A1 (including HS01 - Nguyễn Văn An, HS02 - Trần Thị Bình, etc.), 2 in Class 9A2
        val students = listOf(
            HocSinhEntity("HS01", "Nguyễn Văn An", "2011-05-12", "Nam", "9A1", "Đang học"),
            HocSinhEntity("HS02", "Trần Thị Bình", "2011-08-20", "Nữ", "9A1", "Đang học"),
            HocSinhEntity("HS03", "Lê Hoàng Cường", "2011-01-15", "Nam", "9A1", "Đang học"),
            HocSinhEntity("HS04", "Phạm Thị Dung", "2011-11-03", "Nữ", "9A1", "Đang học"),
            HocSinhEntity("HS05", "Hoàng Văn Em", "2011-07-28", "Nam", "9A1", "Đang học"),
            HocSinhEntity("HS06", "Đỗ Thị Hoa", "2011-03-10", "Nữ", "9A1", "Đang học"),
            HocSinhEntity("HS07", "Vũ Văn Hùng", "2011-09-19", "Nam", "9A1", "Đang học"),
            HocSinhEntity("HS08", "Ngô Thị Lan", "2011-12-05", "Nữ", "9A1", "Đang học"),
            // Class 9A2 (2 students)
            HocSinhEntity("HS09", "Bùi Văn Nam", "2011-02-14", "Nam", "9A2", "Đang học"),
            HocSinhEntity("HS10", "Dương Thị Mai", "2011-06-22", "Nữ", "9A2", "Đang học")
        )
        hocSinhDao.insertOrUpdateHocSinhList(students)

        // 4. Initial sample grades (ĐĐGtx, ĐGK, ĐCK) for student "Nguyễn Văn An" (HS01) across multiple subjects
        val initialGrades = listOf(
            BangDiemEntity(
                maHocSinh = "HS01",
                maMonHoc = "TOAN",
                hocKy = 1,
                namHoc = "2024-2025",
                dsDiemTX = listOf(8.0f, 7.5f, 9.0f),
                diemGiuaKy = 8.0f,
                diemCuoiKy = 8.5f,
                nhanXet = "Học tập xuất sắc, tiếp thu nhanh môn Toán."
            ),
            BangDiemEntity(
                maHocSinh = "HS01",
                maMonHoc = "VAN",
                hocKy = 1,
                namHoc = "2024-2025",
                dsDiemTX = listOf(7.5f, 8.0f),
                diemGiuaKy = 7.8f,
                diemCuoiKy = 8.0f,
                nhanXet = "Cố gắng phát huy kỹ năng diễn đạt."
            ),
            BangDiemEntity(
                maHocSinh = "HS01",
                maMonHoc = "ANH",
                hocKy = 1,
                namHoc = "2024-2025",
                dsDiemTX = listOf(9.0f, 9.5f, 9.0f),
                diemGiuaKy = 9.2f,
                diemCuoiKy = 9.5f,
                nhanXet = "Phát âm chuẩn, giao tiếp lưu loát tiếng Anh."
            )
        )
        bangDiemDao.insertOrUpdateBangDiemList(initialGrades)

        // 5. Audit Log (LichSuSuaDiemEntity)
        val auditLogs = listOf(
            LichSuSuaDiemEntity(
                maHocSinh = "HS01",
                maMonHoc = "TOAN",
                loaiDiem = "Giữa kỳ (ĐGK)",
                diemCu = 7.5f,
                diemMoi = 8.0f,
                lyDoSua = "Phúc khảo bài kiểm tra giữa kỳ",
                nguoiSua = "Giáo viên bộ môn Toán"
            )
        )
        for (log in auditLogs) {
            auditDao.insertAuditLog(log)
        }

        // 6. Leave Requests (DonXinNghiEntity)
        val leaveRequests = listOf(
            DonXinNghiEntity(
                maHocSinh = "HS01",
                tuNgay = "2026-10-14",
                denNgay = "2026-10-14",
                lyDo = "Khám bệnh định kỳ",
                trangThai = "DA_DUYET"
            ),
            DonXinNghiEntity(
                maHocSinh = "HS02",
                tuNgay = "2026-10-15",
                denNgay = "2026-10-16",
                lyDo = "Việc gia đình",
                trangThai = "CHO_DUYET"
            )
        )
        for (don in leaveRequests) {
            donNghiDao.insertOrUpdateDon(don)
        }

        // 7. Class Journal (SoDauBaiEntity) - 5 periods for Class 9A1 on 2026-10-13
        val journals = listOf(
            SoDauBaiEntity(maLop = "9A1", ngay = "2026-10-13", tietHoc = 1, tenMon = "Toán", tenBaiDay = "Phương trình bậc hai một ẩn", nhanXet = "Lớp chú ý nghe giảng", xepLoaiTiet = "A", giaoVienId = "GV.An"),
            SoDauBaiEntity(maLop = "9A1", ngay = "2026-10-13", tietHoc = 2, tenMon = "Ngữ Văn", tenBaiDay = "Kiều ở lầu Ngưng Bích", nhanXet = "Sôi nổi phát biểu", xepLoaiTiet = "A", giaoVienId = "GV.Binh"),
            SoDauBaiEntity(maLop = "9A1", ngay = "2026-10-13", tietHoc = 3, tenMon = "Tiếng Anh", tenBaiDay = "Unit 3: Protecting the Environment", nhanXet = "Thực hành tốt", xepLoaiTiet = "A", giaoVienId = "GV.Cuong"),
            SoDauBaiEntity(maLop = "9A1", ngay = "2026-10-13", tietHoc = 4, tenMon = "KHTN", tenBaiDay = "Định luật Ôm", nhanXet = "Một số em chưa chuẩn bị bài", xepLoaiTiet = "B", giaoVienId = "GV.Dung"),
            SoDauBaiEntity(maLop = "9A1", ngay = "2026-10-13", tietHoc = 5, tenMon = "Lịch sử & Địa lý", tenBaiDay = "Chiến tranh thế giới thứ hai", nhanXet = "Trật tự, nghiêm túc", xepLoaiTiet = "A", giaoVienId = "GV.Em")
        )
        soDauBaiDao.insertOrUpdateSoDauBaiList(journals)

        // 8. Discipline Violations (ViPhamEntity)
        val violations = listOf(
            ViPhamEntity(maHocSinh = "HS03", maLop = "9A1", ngayViPham = "2026-10-12", tenLoi = "Đi trễ", diemTru = 2),
            ViPhamEntity(maHocSinh = "HS05", maLop = "9A1", ngayViPham = "2026-10-12", tenLoi = "Không đồng phục", diemTru = 5)
        )
        for (v in violations) {
            viPhamDao.insertViPham(v)
        }

        // 9. Tuition Bills (HoaDonHocPhiEntity)
        val sampleBills = listOf(
            HoaDonHocPhiEntity(maHocSinh = "HS01", thangNam = "10/2026", tenKhoanThu = "Học phí chính khóa", soTien = 1200000.0, daThanhToan = false, hanNop = "2026-10-25"),
            HoaDonHocPhiEntity(maHocSinh = "HS01", thangNam = "10/2026", tenKhoanThu = "Tiền bán trú & Ăn trưa", soTien = 850000.0, daThanhToan = false, hanNop = "2026-10-25"),
            HoaDonHocPhiEntity(maHocSinh = "HS01", thangNam = "2026-2027", tenKhoanThu = "Bảo hiểm Y tế học sinh", soTien = 680000.0, daThanhToan = true, hanNop = "2026-09-30")
        )
        hoaDonDao.insertHoaDonList(sampleBills)

        // 10. Announcements (ThongBaoEntity)
        val sampleAnnouncements = listOf(
            ThongBaoEntity(tieuDe = "Thông báo lịch nghỉ học nhân dịp 20/11", noiDung = "Nhà trường thông báo toàn thể học sinh nghỉ học ngày 20/11 nhân dịp Ngày Nhà giáo Việt Nam.", ngayGui = "2026-10-10", nguoiGui = "Ban Giám Hiệu"),
            ThongBaoEntity(tieuDe = "Kế hoạch kiểm tra giữa kì I", noiDung = "Học sinh ôn tập kỹ các nội dung đã học từ tuần 1 đến tuần 8.", ngayGui = "2026-10-12", nguoiGui = "Phòng Đào Tạo")
        )
        thongBaoDao.insertThongBaoList(sampleAnnouncements)

        // 11. Chat Messages (TinNhanEntity)
        val sampleMessages = listOf(
            TinNhanEntity(senderId = "TEACHER", receiverId = "PARENT", messageText = "Chào phụ huynh em Nguyễn Văn An, tuần này em An phát biểu rất tích cực trong giờ Toán.", timestamp = System.currentTimeMillis() - 86400000),
            TinNhanEntity(senderId = "PARENT", receiverId = "TEACHER", messageText = "Cảm ơn cô giáo đã quan tâm động viên cháu ạ.", timestamp = System.currentTimeMillis() - 43200000)
        )
        tinNhanDao.insertMessageList(sampleMessages)
    }
}
