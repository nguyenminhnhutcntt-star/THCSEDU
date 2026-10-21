# 📚 HƯỚNG DẪN BÁO CÁO & ĐÁNH GIÁ ỨNG DỤNG THCSEDU (EDUMANAGE)

Ứng dụng **THCSEDU (EduManage)** là hệ thống Quản lý Giáo dục THCS / Sổ liên lạc điện tử thông minh dành cho **Giáo viên**, **Phụ huynh** và **Học sinh**. Dự án được thiết kế chuẩn chỉnh theo kiến trúc **Offline-First**, **Clean Architecture + MVVM**, áp dụng công nghệ lập trình Android hiện đại nhất.

---

## 🛠️ 1. Công Nghệ & Kiến Trúc Sử Dụng

* **Ngôn ngữ lập trình:** Kotlin (Coroutines, Flow, StateFlow).
* **Giao diện (UI):** Jetpack Compose (Material 3) kết hợp Custom Canvas Drawing (vẽ biểu đồ).
* **Cơ sở dữ liệu (Database):** Room Database (SQLite) + Nạp tự động dữ liệu mẫu bằng `AppDatabaseCallback`.
* **Tiêm phụ thuộc (Dependency Injection):** Dagger Hilt (`@HiltViewModel`, `@Inject`).
* **Lưu trữ cấu hình:** Jetpack Preferences DataStore (Lưu phiên đăng nhập & vai trò người dùng).
* **Mạng & API giả lập:** Retrofit 2 + OkHttp 4 + Moshi / Gson.
* **Kiểm thử (Unit Test):** JUnit 4, Kotlin Coroutines Test (14/14 test cases PASSED).

---

## 🚀 2. Hướng Dẫn Cài Đặt & Chạy Ứng Dụng

1. **Mở dự án:** Mở Android Studio (`File > Open` -> thư mục `E:/Nam3HK1/LTM`).
2. **Sync Gradle:** Bấm biểu tượng con voi 🐘 (**Sync Project with Gradle Files**).
3. **Chạy ứng dụng:** Chọn máy ảo Android (API 24 trở lên) và bấm nút **Run (Shift + F10)**.
4. **Lưu ý dữ liệu mẫu (Database Seeding):** 
   - Ứng dụng có cơ chế tự động nạp dữ liệu mẫu vào Room Database khi khởi tạo lần đầu (`AppDatabaseCallback`).
   - *Mẹo:* Nếu đã từng cài ứng dụng trước đây, vui lòng **Gỡ cài đặt (Uninstall) app cũ trên máy ảo** rồi chạy lại để Room khởi tạo mới đầy đủ dữ liệu mẫu (Lớp học, Học sinh, Bảng điểm, Sổ đầu bài, Vi phạm, Học phí, Thông báo, Chat).

---

## 🔑 3. Đăng Nhập & Phân Quyền (Authentication)

Khi mở ứng dụng, màn hình **LoginScreen** cho phép kiểm thử 2 vai trò chính:
* **Vai trò Giáo viên:** Chọn nút **Teacher** -> Bấm **Login** (Chủ nhiệm lớp 9A1).
* **Vai trò Phụ huynh:** Chọn nút **Parent** -> Bấm **Login** (Phụ huynh học sinh Nguyễn Văn An - `HS01`).
* **Quên Mật Khẩu:** Nhấn vào link "Quên mật khẩu?" để thử nghiệm luồng khôi phục mật khẩu qua `QuenMatKhauScreen`.
* *Lưu ý:* Ứng dụng tự động lưu trạng thái đăng nhập qua **DataStore**. Khi đăng xuất (Logout), app sẽ xóa token và đưa về màn hình Đăng nhập.

---

## 📱 4. TỔNG HỢP TOÀN BỘ CHỨC NĂNG ĐÃ HOÀN THÀNH

### A. PHÂN HỆ GIÁO VIÊN (`TEACHER`)

1. **Dashboard Quản Trị Giáo Viên (`TeacherDashboard`):**
   - **Thống kê Điểm Thi Đua Tuần:** Hiển thị tổng điểm nề nếp lớp 9A1 (bắt đầu từ 100đ, tự động trừ điểm khi có vi phạm sao đỏ).
   - **Menu Chức Năng Nhanh:** Thẻ lối tắt dạng lưới dẫn đến tất cả các nghiệp vụ của giáo viên.
   - **Hộp thoại Thông Báo & Đăng Xuất:** Đếm số thông báo mới và nút đăng xuất an toàn.

2. **Sổ Điểm Điện Tử (`SoDiemScreen` & `SoDiemViewModel`):**
   - Quản lý điểm số lớp 9A1, môn Toán (`TOAN`), Học kỳ 1.
   - Hiển thị danh sách điểm đánh giá thường xuyên (`ĐĐGtx`), Giữa kỳ (`ĐGK`), Cuối kỳ (`ĐCK`).
   - Tự động tính toán **Điểm Trung Bình môn (`ĐTB`)** theo đúng quy chế bằng `TinhDiemVaXepLoaiUseCase`.
   - **Khóa Sổ Điểm & Kiểm Toán (`LichSuSuaDiemDao`):** Bấm nút 🔓/🔒 để mở/khóa sổ điểm. Khi chỉnh sửa điểm cũ, hệ thống bắt buộc nhập **Lý do sửa điểm** và lưu lại nhật ký kiểm toán.

3. **Điểm Danh Chuyên Cần (`DiemDanhScreen` & `DiemDanhViewModel`):**
   - Chọn lớp (`9A1` / `9A2`) và ngày tháng điểm danh.
   - Chấm chuyên cần dạng 3 trạng thái: 🟢 *Có mặt*, 🟡 *Vắng có phép*, 🔴 *Vắng không phép*.
   - **Tích Hợp Tự Động Với Đơn Xin Nghỉ:** Hệ thống tự động quét dữ liệu đơn xin nghỉ đã được duyệt (`DA_DUYET`) của phụ huynh và đánh dấu sẵn *"Vắng phép"* kèm ghi chú.

4. **Chấm Nề Nếp / Đội Sao Đỏ (`ChamNeNepScreen` & `ChamNeNepViewModel`):**
   - Ghi nhận lỗi vi phạm nề nếp của học sinh (Đi trễ, Đồng phục, Trật tự, v.v.) kèm số điểm bị trừ.
   - Tự động trừ điểm thi đua tuần của lớp tương ứng.

5. **Duyệt Đơn Xin Nghỉ Phép (`DuyetDonNghiScreen` & `DonNghiViewModel`):**
   - Tiếp nhận danh sách đơn xin nghỉ do phụ huynh gửi tới.
   - Cho phép Giáo viên bấm **Duyệt** hoặc **Từ chối** kèm cập nhật trạng thái thời gian thực.

6. **Quét Mã QR Cổng Trường (`QuetMaQrScreen` & `QuetMaQrViewModel`):**
   - Giao diện quét mã QR check-in học sinh khi vào cổng trường.
   - Hỗ trợ cả cơ chế nhập mã thẻ học sinh thủ công (`HS001`, `HS002`, v.v.) và báo trạng thái thành công.

7. **Sổ Đầu Bài Điện Tử (`SoDauBaiScreen` & `SoDauBaiViewModel`):**
   - Quản lý tiết học từ Tiết 1 đến Tiết 5 theo từng ngày.
   - Nhập tên bài dạy, ghi nhận xét tiết học và chọn chip xếp loại tiết học (`A`, `B`, `C`, `D`).

8. **Thời Khóa Biểu Giảng Dạy (`TimetableScreen` & `TimetableViewModel`):**
   - Xem lịch dạy dạng lưới từ Thứ 2 đến Thứ 7.
   - Tự động highlight nổi bật ngày hiện tại trong tuần.

---

### B. PHÂN HỆ PHỤ HUYNH & HỌC SINH (`PARENT`)

1. **Dashboard Phụ Huynh (`ParentDashboardScreen` & `ParentDashboardViewModel`):**
   - Xem hồ sơ con: Nguyễn Văn An, Lớp 9A1, Hạnh kiểm Tốt.
   - **Cảnh Báo Vi Phạm Nề Nếp:** Thẻ cảnh báo màu cam hiển thị ngay nếu con có ghi nhận vi phạm nề nếp trong tuần.

2. **Biểu Đồ Tiến Trình Điểm (`SemesterScoreLineChart` - Canvas):**
   - Biểu đồ đường (Line Chart) vẽ bằng **Jetpack Compose Canvas** trực quan hóa điểm trung bình qua 4 học kỳ gần nhất (`HK1 23-24`, `HK2 23-24`, `HK1 24-25`, `HK2 24-25`).
   - Tích hợp mảng màu Gradient bên dưới đường cong, lưới tọa độ, mốc điểm tròn và nhãn số điểm rõ ràng.

3. **Bảng Điểm Đầy Đủ Tất Cả Các Môn Học:**
   - Hiển thị danh sách đầy đủ **8 môn học** (Toán, Ngữ Văn, Tiếng Anh, Khoa học Tự nhiên, Lịch sử & Địa lý, Tin học, Công nghệ, Giáo dục công dân).
   - Đi kèm điểm trung bình môn và badge màu xếp loại đánh giá (*Xuất sắc, Giỏi, Khá*).

4. **Thẻ Học Sinh Điện Tử & Mã QR (`TheHocSinhScreen` & `TheHocSinhViewModel`):**
   - Thẻ học sinh điện tử thiết kế hiện đại có logo trường THCS EDU, ảnh đại diện, họ tên, mã HS, lớp, ngày sinh và niên khóa.
   - **Tạo Mã QR Động (`QrCodeUtil`):** Tự động sinh mã QR bitmap từ mã học sinh. Nhấp vào mã QR để mở Dialog phóng to phục vụ quét mã tại cổng trường.

5. **Tạo Đơn Xin Nghỉ Học (`TaoDonXinNghiScreen` & `DonNghiViewModel`):**
   - Gửi đơn xin nghỉ cho con: Chọn ngày bắt đầu, ngày kết thúc, nhập lý do nghỉ và bấm gửi tới GVCN.

6. **Quản Lý Học Phí & Khoản Thu (`QuanLyHocPhiScreen` & `QuanLyHocPhiViewModel`):**
   - Xem danh sách các khoản thu (Học phí chính khóa, Tiền bán trú, Bảo hiểm y tế).
   - Thống kê tổng số tiền còn nợ, trạng thái *Đã thanh toán* / *Chưa thanh toán*.
   - **Thanh Toán Mã QR Giả Lập:** Nhấp thanh toán để mở Dialog mã QR VietQR giả lập thanh toán chuyển khoản.

7. **Thực Đơn Bán Trú Tuần (`ThucDonScreen` & `ThucDonViewModel`):**
   - Theo dõi thực đơn bữa trưa và bữa phụ chiều theo từng ngày trong tuần (Thứ 2 đến Thứ 6).

---

### C. CHỨC NĂNG DÙNG CHUNG & TƯƠNG TÁC (`COMMON`)

1. **Thông Báo Nhà Trường (`ThongBaoScreen` & `ThongBaoViewModel`):**
   - Xem bảng tin thông báo chính thức từ Ban Giám Hiệu và Phòng Đào Tạo.
   - Hỗ trợ lọc danh sách thông báo và đánh dấu đã đọc.

2. **Trò Chuyện 1-1 Trực Tiếp (`ChatScreen` & `ChatViewModel`):**
   - Kênh trao đổi 1-1 trực tiếp giữa Phụ huynh và Giáo viên chủ nhiệm.
   - Giao diện bong bóng tin nhắn (Chat Bubbles) hiện đại, hiển thị thời gian gửi, lưu trữ thời gian thực vào Room Local Database (`TinNhanEntity`).

---

### D. XUẤT PHIẾU LIÊN LẠC & BÁO CÁO PDF (`PDF REPORT GENERATOR`)

1. **Trình Tạo Báo Cáo PDF Chuẩn A4 (`PdfReportHelper.kt` & `PdfExporter.kt`):**
   - Sử dụng native `android.graphics.pdf.PdfDocument` và `Canvas` để xuất Phiếu Liên Lạc chuẩn khổ A4 (`595 x 842` points / `72 DPI`).
   - **Tích Hợp Logo Trường (`ic_app_logo.png`):** Tự động giải mã tài nguyên `R.drawable.ic_app_logo` và vẽ lên góc trái phần đầu trang báo cáo.
   - **Cấu Trúc Phiếu Báo Cáo:**
     * **Header:** Tiêu đề Sở GD&ĐT, Trường THCS EDU, biểu tượng logo chính thức, và tên học kỳ / năm học.
     * **Student Info Box:** Thông tin học sinh (Nguyễn Văn An, Mã HS: `HS01`, Lớp `9A1`, Ngày sinh, Giáo viên chủ nhiệm).
     * **Grade Table:** Bảng lưới ô có đường viền (`Paint.Style.STROKE`) liệt kê tất cả các môn học kèm điểm đánh giá thường xuyên (`ĐĐGtx`), Giữa kỳ (`ĐGK`), Cuối kỳ (`ĐCK`), Điểm trung bình (`ĐTBmhk`) và Nhận xét.
     * **Summary Section:** Điểm Trung Bình Chung (GPA), Học lực (*Giỏi*), Hạnh kiểm (*Tốt*), và đánh giá tổng quan rèn luyện.
     * **Footer & Signatures:** Ngày tháng năm lập phiếu và khối chữ ký song song cho *"Chữ ký Phụ huynh"* & *"Giáo viên chủ nhiệm"*.

2. **Xuất File & Chia Sẻ / Mở Nhanh (`PdfExporter.kt`):**
   - Lưu trữ file `.pdf` an toàn trong thư mục cache ứng dụng (`context.cacheDir/reports`).
   - Sử dụng `FileProvider` (`${applicationId}.fileprovider`) với `Intent.ACTION_VIEW` và `Intent.ACTION_SEND` giúp người dùng xem trực tiếp, tải xuống hoặc in/chia sẻ file PDF qua bất kỳ ứng dụng đọc PDF nào.

3. **Tích Hợp UI Jetpack Compose & Material 3:**
   - Nút **"Xuất Phiếu PDF"** (biểu tượng `Icons.Default.PictureAsPdf`) tích hợp trên **`ParentDashboardScreen`** và TopAppBar của **`SoDiemScreen`**.
   - Hiển thị hiệu ứng tải (`CircularProgressIndicator`) trong quá trình tạo file và thông báo `Snackbar` đi kèm hành động **"Mở file"** khi hoàn thành.

---

## 🧪 5. Kết Quả Kiểm Thử (Unit Tests)

Dự án bao gồm bộ kiểm thử tự động cho phần logic nghiệp vụ quan trọng:
* **`TinhDiemVaXepLoaiUseCaseTest`**: Kiểm thử thuật toán tính điểm trung bình môn và xếp loại học sinh.
* **`SoDiemViewModelTest`**: Kiểm thử luồng tải điểm, sửa điểm và lưu lịch sử kiểm toán.
* **Kết quả:** `14 passed, 0 failed` (100% Passed).
