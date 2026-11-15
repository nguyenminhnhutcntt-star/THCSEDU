package com.example.thcsedu.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.thcsedu.data.local.entity.*
import com.example.thcsedu.ui.util.PasswordUtil
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

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        CoroutineScope(Dispatchers.IO).launch {
            val database = databaseProvider.get()
            // Đảm bảo tài khoản mẫu luôn có sẵn
            seedDefaultAccounts(database)
        }
    }

    private suspend fun seedDefaultAccounts(database: AppDatabase) {
        val taiKhoanDao = database.taiKhoanDao()
        val defaultAccounts = listOf(
            TaiKhoanEntity(
                tenDangNhap = "teacher",
                matKhauHash = PasswordUtil.hashPassword("123456"),
                vaiTro = "TEACHER",
                hoTen = "Thầy Nguyễn Văn Trọng",
                maDinhDanh = "GV01"
            ),
            TaiKhoanEntity(
                tenDangNhap = "gv_an",
                matKhauHash = PasswordUtil.hashPassword("123456"),
                vaiTro = "TEACHER",
                hoTen = "Cô Trần Thị Mai",
                maDinhDanh = "GV02"
            ),
            TaiKhoanEntity(
                tenDangNhap = "parent",
                matKhauHash = PasswordUtil.hashPassword("123456"),
                vaiTro = "PARENT",
                hoTen = "Phụ huynh em Nguyễn Văn An",
                maDinhDanh = "HS01"
            ),
            TaiKhoanEntity(
                tenDangNhap = "ph_binh",
                matKhauHash = PasswordUtil.hashPassword("123456"),
                vaiTro = "PARENT",
                hoTen = "Phụ huynh em Trần Thị Bình",
                maDinhDanh = "HS02"
            ),
            TaiKhoanEntity(
                tenDangNhap = "admin",
                matKhauHash = PasswordUtil.hashPassword("admin123"),
                vaiTro = "ADMIN",
                hoTen = "Quản trị viên nhà trường",
                maDinhDanh = "AD01"
            )
        )
        taiKhoanDao.insertTaiKhoanList(defaultAccounts)
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
        val thoiKhoaBieuDao = database.thoiKhoaBieuDao()
        val phanCongDao = database.phanCongGiaoVienDao()

        // 0. Seed Accounts
        seedDefaultAccounts(database)

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

        // 3. Students
        val students = listOf(
            HocSinhEntity("HS01", "Nguyễn Văn An", "2011-05-12", "Nam", "9A1", "Đang học"),
            HocSinhEntity("HS02", "Trần Thị Bình", "2011-08-20", "Nữ", "9A1", "Đang học"),
            HocSinhEntity("HS03", "Lê Hoàng Cường", "2011-01-15", "Nam", "9A1", "Đang học"),
            HocSinhEntity("HS04", "Phạm Thị Dung", "2011-11-03", "Nữ", "9A1", "Đang học"),
            HocSinhEntity("HS05", "Hoàng Văn Em", "2011-07-28", "Nam", "9A1", "Đang học"),
            HocSinhEntity("HS06", "Đỗ Thị Hoa", "2011-03-10", "Nữ", "9A1", "Đang học"),
            HocSinhEntity("HS07", "Vũ Văn Hùng", "2011-09-19", "Nam", "9A1", "Đang học"),
            HocSinhEntity("HS08", "Ngô Thị Lan", "2011-12-05", "Nữ", "9A1", "Đang học"),
            HocSinhEntity("HS09", "Bùi Văn Nam", "2011-02-14", "Nam", "9A2", "Đang học"),
            HocSinhEntity("HS10", "Dương Thị Mai", "2011-06-22", "Nữ", "9A2", "Đang học")
        )
        hocSinhDao.insertOrUpdateHocSinhList(students)

        // 4. Full Grades for HS01 across all 8 subjects
        val initialGrades = listOf(
            BangDiemEntity(
                maHocSinh = "HS01",
                maMonHoc = "TOAN",
                hocKy = 1,
                namHoc = "2024-2025",
                dsDiemTX = listOf(8.0f, 8.5f, 9.0f),
                diemGiuaKy = 8.5f,
                diemCuoiKy = 8.8f,
                nhanXet = "Tiếp thu nhanh, tư duy toán học xuất sắc"
            ),
            BangDiemEntity(
                maHocSinh = "HS01",
                maMonHoc = "VAN",
                hocKy = 1,
                namHoc = "2024-2025",
                dsDiemTX = listOf(7.5f, 8.0f),
                diemGiuaKy = 7.8f,
                diemCuoiKy = 8.0f,
                nhanXet = "Kỹ năng cảm thụ và hành văn tiến bộ"
            ),
            BangDiemEntity(
                maHocSinh = "HS01",
                maMonHoc = "ANH",
                hocKy = 1,
                namHoc = "2024-2025",
                dsDiemTX = listOf(9.0f, 9.5f, 9.0f),
                diemGiuaKy = 9.2f,
                diemCuoiKy = 9.5f,
                nhanXet = "Phát âm chuẩn, giao tiếp lưu loát tiếng Anh"
            ),
            BangDiemEntity(
                maHocSinh = "HS01",
                maMonHoc = "KHTN",
                hocKy = 1,
                namHoc = "2024-2025",
                dsDiemTX = listOf(8.0f, 8.5f),
                diemGiuaKy = 8.0f,
                diemCuoiKy = 8.5f,
                nhanXet = "Nắm vững kiến thức khoa học, chăm thực hành"
            ),
            BangDiemEntity(
                maHocSinh = "HS01",
                maMonHoc = "LS_DL",
                hocKy = 1,
                namHoc = "2024-2025",
                dsDiemTX = listOf(8.0f, 8.0f),
                diemGiuaKy = 8.0f,
                diemCuoiKy = 8.0f,
                nhanXet = "Hăng hái phát biểu, hiểu bài sâu"
            ),
            BangDiemEntity(
                maHocSinh = "HS01",
                maMonHoc = "GDCD",
                hocKy = 1,
                namHoc = "2024-2025",
                dsDiemTX = listOf(8.5f, 9.0f),
                diemGiuaKy = 8.5f,
                diemCuoiKy = 9.0f,
                nhanXet = "Ý thức rèn luyện và đạo đức rất tốt"
            ),
            BangDiemEntity(
                maHocSinh = "HS01",
                maMonHoc = "TIN",
                hocKy = 1,
                namHoc = "2024-2025",
                dsDiemTX = listOf(9.0f, 10.0f),
                diemGiuaKy = 9.5f,
                diemCuoiKy = 9.5f,
                nhanXet = "Thao tác máy tính thành thạo, viết mã tốt"
            ),
            BangDiemEntity(
                maHocSinh = "HS01",
                maMonHoc = "CN",
                hocKy = 1,
                namHoc = "2024-2025",
                dsDiemTX = listOf(8.5f, 9.0f),
                diemGiuaKy = 8.8f,
                diemCuoiKy = 8.8f,
                nhanXet = "Nhiệt tình tham gia hoạt động kỹ thuật"
            ),
            // Điểm cho HS02
            BangDiemEntity(
                maHocSinh = "HS02",
                maMonHoc = "TOAN",
                hocKy = 1,
                namHoc = "2024-2025",
                dsDiemTX = listOf(7.0f, 7.5f),
                diemGiuaKy = 7.0f,
                diemCuoiKy = 7.5f,
                nhanXet = "Chăm chỉ làm bài tập về nhà"
            ),
            BangDiemEntity(
                maHocSinh = "HS02",
                maMonHoc = "VAN",
                hocKy = 1,
                namHoc = "2024-2025",
                dsDiemTX = listOf(8.0f, 8.5f),
                diemGiuaKy = 8.0f,
                diemCuoiKy = 8.2f,
                nhanXet = "Văn phong mượt mà, diễn đạt tốt"
            )
        )
        bangDiemDao.insertOrUpdateBangDiemList(initialGrades)

        // 5. Timetable for Class 9A1
        val timetable9A1 = listOf(
            // Thứ 2 (2)
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 2, tiet = 1, maMonHoc = "TOAN", tenMonHoc = "Toán", giaoVien = "Thầy Trọng", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 2, tiet = 2, maMonHoc = "TOAN", tenMonHoc = "Toán", giaoVien = "Thầy Trọng", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 2, tiet = 3, maMonHoc = "VAN", tenMonHoc = "Ngữ Văn", giaoVien = "Cô Mai", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 2, tiet = 4, maMonHoc = "ANH", tenMonHoc = "Tiếng Anh", giaoVien = "Cô Chi", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 2, tiet = 5, maMonHoc = "GDCD", tenMonHoc = "Giáo dục công dân", giaoVien = "Cô Hoa", phongHoc = "P.201"),

            // Thứ 3 (3)
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 3, tiet = 1, maMonHoc = "KHTN", tenMonHoc = "Khoa học Tự nhiên", giaoVien = "Thầy Dũng", phongHoc = "P.Lab"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 3, tiet = 2, maMonHoc = "KHTN", tenMonHoc = "Khoa học Tự nhiên", giaoVien = "Thầy Dũng", phongHoc = "P.Lab"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 3, tiet = 3, maMonHoc = "LS_DL", tenMonHoc = "Lịch sử & Địa lý", giaoVien = "Thầy Hùng", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 3, tiet = 4, maMonHoc = "TOAN", tenMonHoc = "Toán", giaoVien = "Thầy Trọng", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 3, tiet = 5, maMonHoc = "TIN", tenMonHoc = "Tin học", giaoVien = "Thầy Em", phongHoc = "P.Tin"),

            // Thứ 4 (4)
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 4, tiet = 1, maMonHoc = "VAN", tenMonHoc = "Ngữ Văn", giaoVien = "Cô Mai", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 4, tiet = 2, maMonHoc = "VAN", tenMonHoc = "Ngữ Văn", giaoVien = "Cô Mai", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 4, tiet = 3, maMonHoc = "ANH", tenMonHoc = "Tiếng Anh", giaoVien = "Cô Chi", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 4, tiet = 4, maMonHoc = "LS_DL", tenMonHoc = "Lịch sử & Địa lý", giaoVien = "Thầy Hùng", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 4, tiet = 5, maMonHoc = "CN", tenMonHoc = "Công nghệ", giaoVien = "Cô Lan", phongHoc = "P.201"),

            // Thứ 5 (5)
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 5, tiet = 1, maMonHoc = "TOAN", tenMonHoc = "Toán", giaoVien = "Thầy Trọng", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 5, tiet = 2, maMonHoc = "KHTN", tenMonHoc = "Khoa học Tự nhiên", giaoVien = "Thầy Dũng", phongHoc = "P.Lab"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 5, tiet = 3, maMonHoc = "KHTN", tenMonHoc = "Khoa học Tự nhiên", giaoVien = "Thầy Dũng", phongHoc = "P.Lab"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 5, tiet = 4, maMonHoc = "GDCD", tenMonHoc = "Giáo dục công dân", giaoVien = "Cô Hoa", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 5, tiet = 5, maMonHoc = "TIN", tenMonHoc = "Tin học", giaoVien = "Thầy Em", phongHoc = "P.Tin"),

            // Thứ 6 (6)
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 6, tiet = 1, maMonHoc = "ANH", tenMonHoc = "Tiếng Anh", giaoVien = "Cô Chi", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 6, tiet = 2, maMonHoc = "ANH", tenMonHoc = "Tiếng Anh", giaoVien = "Cô Chi", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 6, tiet = 3, maMonHoc = "VAN", tenMonHoc = "Ngữ Văn", giaoVien = "Cô Mai", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 6, tiet = 4, maMonHoc = "CN", tenMonHoc = "Công nghệ", giaoVien = "Cô Lan", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 6, tiet = 5, maMonHoc = "LS_DL", tenMonHoc = "Lịch sử & Địa lý", giaoVien = "Thầy Hùng", phongHoc = "P.201"),

            // Thứ 7 (7)
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 7, tiet = 1, maMonHoc = "TOAN", tenMonHoc = "Toán", giaoVien = "Thầy Trọng", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 7, tiet = 2, maMonHoc = "VAN", tenMonHoc = "Ngữ Văn", giaoVien = "Cô Mai", phongHoc = "P.201"),
            ThoiKhoaBieuEntity(maLop = "9A1", thu = 7, tiet = 3, maMonHoc = "KHTN", tenMonHoc = "Khoa học Tự nhiên", giaoVien = "Thầy Dũng", phongHoc = "P.Lab")
        )
        thoiKhoaBieuDao.insertThoiKhoaBieuList(timetable9A1)

        // 6. Teacher Assignments (PhanCongGiaoVienEntity)
        val assignments = listOf(
            PhanCongGiaoVienEntity(maGiaoVien = "GV01", tenGiaoVien = "Thầy Nguyễn Văn Trọng", maLop = "9A1", maMonHoc = "TOAN", tenMonHoc = "Toán", vaiTro = "Chủ nhiệm"),
            PhanCongGiaoVienEntity(maGiaoVien = "GV02", tenGiaoVien = "Cô Trần Thị Mai", maLop = "9A1", maMonHoc = "VAN", tenMonHoc = "Ngữ Văn", vaiTro = "Bộ môn"),
            PhanCongGiaoVienEntity(maGiaoVien = "GV03", tenGiaoVien = "Cô Lê Cẩm Chi", maLop = "9A1", maMonHoc = "ANH", tenMonHoc = "Tiếng Anh", vaiTro = "Bộ môn"),
            PhanCongGiaoVienEntity(maGiaoVien = "GV04", tenGiaoVien = "Thầy Phạm Tiến Dũng", maLop = "9A1", maMonHoc = "KHTN", tenMonHoc = "Khoa học Tự nhiên", vaiTro = "Bộ môn"),
            PhanCongGiaoVienEntity(maGiaoVien = "GV05", tenGiaoVien = "Thầy Vũ Đình Hùng", maLop = "9A1", maMonHoc = "LS_DL", tenMonHoc = "Lịch sử & Địa lý", vaiTro = "Bộ môn"),
            PhanCongGiaoVienEntity(maGiaoVien = "GV06", tenGiaoVien = "Cô Đỗ Thị Hoa", maLop = "9A1", maMonHoc = "GDCD", tenMonHoc = "Giáo dục công dân", vaiTro = "Bộ môn"),
            PhanCongGiaoVienEntity(maGiaoVien = "GV07", tenGiaoVien = "Thầy Hoàng Văn Em", maLop = "9A1", maMonHoc = "TIN", tenMonHoc = "Tin học", vaiTro = "Bộ môn"),
            PhanCongGiaoVienEntity(maGiaoVien = "GV08", tenGiaoVien = "Cô Bùi Lan Anh", maLop = "9A1", maMonHoc = "CN", tenMonHoc = "Công nghệ", vaiTro = "Bộ môn"),
            // Phân công lớp 9A2
            PhanCongGiaoVienEntity(maGiaoVien = "GV02", tenGiaoVien = "Cô Trần Thị Mai", maLop = "9A2", maMonHoc = "VAN", tenMonHoc = "Ngữ Văn", vaiTro = "Chủ nhiệm"),
            PhanCongGiaoVienEntity(maGiaoVien = "GV01", tenGiaoVien = "Thầy Nguyễn Văn Trọng", maLop = "9A2", maMonHoc = "TOAN", tenMonHoc = "Toán", vaiTro = "Bộ môn")
        )
        phanCongDao.insertPhanCongList(assignments)

        // 7. Audit Log
        val auditLogs = listOf(
            LichSuSuaDiemEntity(
                maHocSinh = "HS01",
                maMonHoc = "TOAN",
                loaiDiem = "Giữa kỳ (ĐGK)",
                diemCu = 7.5f,
                diemMoi = 8.0f,
                lyDoSua = "Phúc khảo bài kiểm tra giữa kỳ",
                nguoiSua = "Thầy Nguyễn Văn Trọng"
            )
        )
        for (log in auditLogs) {
            auditDao.insertAuditLog(log)
        }

        // 8. Leave Requests
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

        // 9. Class Journal
        val journals = listOf(
            SoDauBaiEntity(maLop = "9A1", ngay = "2026-10-13", tietHoc = 1, tenMon = "Toán", tenBaiDay = "Phương trình bậc hai một ẩn", nhanXet = "Lớp chú ý nghe giảng", xepLoaiTiet = "A", giaoVienId = "GV.Trong"),
            SoDauBaiEntity(maLop = "9A1", ngay = "2026-10-13", tietHoc = 2, tenMon = "Ngữ Văn", tenBaiDay = "Kiều ở lầu Ngưng Bích", nhanXet = "Sôi nổi phát biểu", xepLoaiTiet = "A", giaoVienId = "GV.Mai"),
            SoDauBaiEntity(maLop = "9A1", ngay = "2026-10-13", tietHoc = 3, tenMon = "Tiếng Anh", tenBaiDay = "Unit 3: Protecting the Environment", nhanXet = "Thực hành tốt", xepLoaiTiet = "A", giaoVienId = "GV.Chi"),
            SoDauBaiEntity(maLop = "9A1", ngay = "2026-10-13", tietHoc = 4, tenMon = "KHTN", tenBaiDay = "Định luật Ôm", nhanXet = "Một số em chưa chuẩn bị bài", xepLoaiTiet = "B", giaoVienId = "GV.Dung"),
            SoDauBaiEntity(maLop = "9A1", ngay = "2026-10-13", tietHoc = 5, tenMon = "Lịch sử & Địa lý", tenBaiDay = "Chiến tranh thế giới thứ hai", nhanXet = "Trật tự, nghiêm túc", xepLoaiTiet = "A", giaoVienId = "GV.Hung")
        )
        soDauBaiDao.insertOrUpdateSoDauBaiList(journals)

        // 10. Violations
        val violations = listOf(
            ViPhamEntity(maHocSinh = "HS03", maLop = "9A1", ngayViPham = "2026-10-12", tenLoi = "Đi trễ", diemTru = 2),
            ViPhamEntity(maHocSinh = "HS05", maLop = "9A1", ngayViPham = "2026-10-12", tenLoi = "Không đồng phục", diemTru = 5)
        )
        for (v in violations) {
            viPhamDao.insertViPham(v)
        }

        // 11. Tuition Bills
        val sampleBills = listOf(
            HoaDonHocPhiEntity(maHocSinh = "HS01", thangNam = "10/2026", tenKhoanThu = "Học phí chính khóa", soTien = 1200000.0, daThanhToan = false, hanNop = "2026-10-25"),
            HoaDonHocPhiEntity(maHocSinh = "HS01", thangNam = "10/2026", tenKhoanThu = "Tiền bán trú & Ăn trưa", soTien = 850000.0, daThanhToan = false, hanNop = "2026-10-25"),
            HoaDonHocPhiEntity(maHocSinh = "HS01", thangNam = "2026-2027", tenKhoanThu = "Bảo hiểm Y tế học sinh", soTien = 680000.0, daThanhToan = true, hanNop = "2026-09-30")
        )
        hoaDonDao.insertHoaDonList(sampleBills)

        // 12. Announcements
        val sampleAnnouncements = listOf(
            ThongBaoEntity(tieuDe = "Thông báo lịch nghỉ học nhân dịp 20/11", noiDung = "Nhà trường thông báo toàn thể học sinh nghỉ học ngày 20/11 nhân dịp Ngày Nhà giáo Việt Nam.", ngayGui = "2026-10-10", nguoiGui = "Ban Giám Hiệu"),
            ThongBaoEntity(tieuDe = "Kế hoạch kiểm tra giữa kì I", noiDung = "Học sinh ôn tập kỹ các nội dung đã học từ tuần 1 đến tuần 8.", ngayGui = "2026-10-12", nguoiGui = "Phòng Đào Tạo")
        )
        thongBaoDao.insertThongBaoList(sampleAnnouncements)

        // 13. Chat Messages
        val sampleMessages = listOf(
            TinNhanEntity(senderId = "TEACHER", receiverId = "PARENT", messageText = "Chào phụ huynh em Nguyễn Văn An, tuần này em An phát biểu rất tích cực trong giờ Toán.", timestamp = System.currentTimeMillis() - 86400000),
            TinNhanEntity(senderId = "PARENT", receiverId = "TEACHER", messageText = "Cảm ơn cô giáo đã quan tâm động viên cháu ạ.", timestamp = System.currentTimeMillis() - 43200000)
        )
        tinNhanDao.insertMessageList(sampleMessages)
    }
}
