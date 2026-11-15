package com.example.thcsedu.ui.quanly

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thcsedu.data.local.entity.LoaiDanhGia

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuanLyTruongHocScreen(
    viewModel: QuanLyTruongHocViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Lớp Học", "Học Sinh", "Môn Học", "Phân Công GV")

    var showAddClassDialog by remember { mutableStateOf(false) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAddAssignmentDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản Trị Nhà Trường", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trở về")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    when (uiState.selectedTab) {
                        0 -> showAddClassDialog = true
                        1 -> showAddStudentDialog = true
                        2 -> showAddSubjectDialog = true
                        3 -> showAddAssignmentDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm mới")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs
            PrimaryTabRow(selectedTabIndex = uiState.selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Default.Class, contentDescription = null, modifier = Modifier.size(18.dp))
                                1 -> Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(18.dp))
                                2 -> Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(18.dp))
                                3 -> Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }
                    )
                }
            }

            // Notification message banners
            if (uiState.errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (uiState.successMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = uiState.successMessage ?: "",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Tab contents
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (uiState.selectedTab) {
                    0 -> TabClasses(uiState, viewModel)
                    1 -> TabStudents(uiState, viewModel)
                    2 -> TabSubjects(uiState, viewModel)
                    3 -> TabAssignments(uiState, viewModel)
                }
            }
        }
    }

    // Dialogs
    if (showAddClassDialog) {
        AddClassDialog(onDismiss = { showAddClassDialog = false }, onSave = { ma, ten, nam ->
            viewModel.saveClass(ma, ten, nam)
            showAddClassDialog = false
        })
    }

    if (showAddStudentDialog) {
        AddStudentDialog(classes = uiState.classes, onDismiss = { showAddStudentDialog = false }, onSave = { ma, ten, dob, sex, lop ->
            viewModel.saveStudent(ma, ten, dob, sex, lop)
            showAddStudentDialog = false
        })
    }

    if (showAddSubjectDialog) {
        AddSubjectDialog(onDismiss = { showAddSubjectDialog = false }, onSave = { ma, ten, loai ->
            viewModel.saveSubject(ma, ten, loai)
            showAddSubjectDialog = false
        })
    }

    if (showAddAssignmentDialog) {
        AddAssignmentDialog(
            classes = uiState.classes,
            subjects = uiState.subjects,
            onDismiss = { showAddAssignmentDialog = false },
            onSave = { gvId, gvName, lop, monId, monName, role ->
                viewModel.saveAssignment(gvId, gvName, lop, monId, monName, role)
                showAddAssignmentDialog = false
            }
        )
    }
}

@Composable
private fun TabClasses(uiState: QuanLyTruongHocUiState, viewModel: QuanLyTruongHocViewModel) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(
                text = "Danh Sách Lớp Học (${uiState.classes.size} lớp)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(uiState.classes) { lop ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = lop.tenLop, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = "Mã lớp: ${lop.maLop} • Niên khóa: ${lop.nienKhoa}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { viewModel.deleteClass(lop) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun TabStudents(uiState: QuanLyTruongHocUiState, viewModel: QuanLyTruongHocViewModel) {
    val filteredStudents = if (uiState.selectedClassFilter == "ALL") {
        uiState.students
    } else {
        uiState.students.filter { it.maLop == uiState.selectedClassFilter }
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Danh Sách Học Sinh (${filteredStudents.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Class Filter Chips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = uiState.selectedClassFilter == "ALL",
                    onClick = { viewModel.setClassFilter("ALL") },
                    label = { Text("Tất cả") }
                )
                uiState.classes.forEach { lop ->
                    FilterChip(
                        selected = uiState.selectedClassFilter == lop.maLop,
                        onClick = { viewModel.setClassFilter(lop.maLop) },
                        label = { Text(lop.maLop) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(filteredStudents) { student ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = student.hoTen, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(text = "Mã HS: ${student.maHocSinh} • Lớp: ${student.maLop} • Giới tính: ${student.gioiTinh}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "Ngày sinh: ${student.ngaySinh} • ${student.trangThai}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                    IconButton(onClick = { viewModel.deleteStudent(student) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun TabSubjects(uiState: QuanLyTruongHocUiState, viewModel: QuanLyTruongHocViewModel) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(
                text = "Danh Sách Môn Học (${uiState.subjects.size} môn)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(uiState.subjects) { mon ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = mon.tenMonHoc, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = "Mã môn: ${mon.maMonHoc} • Đánh giá: ${if (mon.loaiDanhGia == LoaiDanhGia.CHO_DIEM) "Cho điểm (0-10)" else "Nhận xét"}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { viewModel.deleteSubject(mon) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun TabAssignments(uiState: QuanLyTruongHocUiState, viewModel: QuanLyTruongHocViewModel) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(
                text = "Phân Công Giáo Viên Giảng Dạy (${uiState.assignments.size} lượt)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(uiState.assignments) { assignment ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = assignment.tenGiaoVien, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (assignment.vaiTro == "Chủ nhiệm") Color(0xFFE8F5E9) else Color(0xFFE3F2FD)
                            ) {
                                Text(
                                    text = assignment.vaiTro,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (assignment.vaiTro == "Chủ nhiệm") Color(0xFF2E7D32) else Color(0xFF1565C0),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(text = "Môn: ${assignment.tenMonHoc} • Lớp: ${assignment.maLop}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "Mã GV: ${assignment.maGiaoVien}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                    IconButton(onClick = { viewModel.deleteAssignment(assignment) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

// Dialogs
@Composable
fun AddClassDialog(onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var maLop by remember { mutableStateOf("") }
    var tenLop by remember { mutableStateOf("") }
    var namHoc by remember { mutableStateOf("2024-2025") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm Lớp Học Mới", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = maLop, onValueChange = { maLop = it }, label = { Text("Mã lớp (VD: 9A3)") }, singleLine = true)
                OutlinedTextField(value = tenLop, onValueChange = { tenLop = it }, label = { Text("Tên lớp (VD: Lớp 9A3)") }, singleLine = true)
                OutlinedTextField(value = namHoc, onValueChange = { namHoc = it }, label = { Text("Năm học (VD: 2024-2025)") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = { onSave(maLop, tenLop, namHoc) }) { Text("Lưu") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
fun AddStudentDialog(
    classes: List<com.example.thcsedu.data.local.entity.LopEntity>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var maHocSinh by remember { mutableStateOf("") }
    var hoTen by remember { mutableStateOf("") }
    var ngaySinh by remember { mutableStateOf("2011-01-01") }
    var gioiTinh by remember { mutableStateOf("Nam") }
    var maLop by remember { mutableStateOf(classes.firstOrNull()?.maLop ?: "9A1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm Học Sinh Mới", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = maHocSinh, onValueChange = { maHocSinh = it }, label = { Text("Mã học sinh (VD: HS11)") }, singleLine = true)
                OutlinedTextField(value = hoTen, onValueChange = { hoTen = it }, label = { Text("Họ và tên") }, singleLine = true)
                OutlinedTextField(value = ngaySinh, onValueChange = { ngaySinh = it }, label = { Text("Ngày sinh (YYYY-MM-DD)") }, singleLine = true)
                OutlinedTextField(value = gioiTinh, onValueChange = { gioiTinh = it }, label = { Text("Giới tính (Nam/Nữ)") }, singleLine = true)
                OutlinedTextField(value = maLop, onValueChange = { maLop = it }, label = { Text("Lớp học") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = { onSave(maHocSinh, hoTen, ngaySinh, gioiTinh, maLop) }) { Text("Lưu") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
fun AddSubjectDialog(onDismiss: () -> Unit, onSave: (String, String, LoaiDanhGia) -> Unit) {
    var maMonHoc by remember { mutableStateOf("") }
    var tenMonHoc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm Môn Học Mới", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = maMonHoc, onValueChange = { maMonHoc = it }, label = { Text("Mã môn (VD: AM_NHAC)") }, singleLine = true)
                OutlinedTextField(value = tenMonHoc, onValueChange = { tenMonHoc = it }, label = { Text("Tên môn (VD: Âm Nhạc)") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = { onSave(maMonHoc, tenMonHoc, LoaiDanhGia.CHO_DIEM) }) { Text("Lưu") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
fun AddAssignmentDialog(
    classes: List<com.example.thcsedu.data.local.entity.LopEntity>,
    subjects: List<com.example.thcsedu.data.local.entity.MonHocEntity>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String) -> Unit
) {
    var maGV by remember { mutableStateOf("GV01") }
    var tenGV by remember { mutableStateOf("Thầy Nguyễn Văn Trọng") }
    var maLop by remember { mutableStateOf(classes.firstOrNull()?.maLop ?: "9A1") }
    var maMon by remember { mutableStateOf(subjects.firstOrNull()?.maMonHoc ?: "TOAN") }
    var vaiTro by remember { mutableStateOf("Bộ môn") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Phân Công Giáo Viên", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = maGV, onValueChange = { maGV = it }, label = { Text("Mã giáo viên") }, singleLine = true)
                OutlinedTextField(value = tenGV, onValueChange = { tenGV = it }, label = { Text("Tên giáo viên") }, singleLine = true)
                OutlinedTextField(value = maLop, onValueChange = { maLop = it }, label = { Text("Lớp học") }, singleLine = true)
                OutlinedTextField(value = maMon, onValueChange = { maMon = it }, label = { Text("Mã môn học") }, singleLine = true)
                OutlinedTextField(value = vaiTro, onValueChange = { vaiTro = it }, label = { Text("Vai trò (Chủ nhiệm / Bộ môn)") }, singleLine = true)
            }
        },
        confirmButton = {
            val monName = subjects.find { it.maMonHoc == maMon }?.tenMonHoc ?: maMon
            Button(onClick = { onSave(maGV, tenGV, maLop, maMon, monName, vaiTro) }) { Text("Lưu") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}
