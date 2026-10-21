package com.example.thcsedu.ui.sodiem

import android.widget.Toast
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.thcsedu.ui.util.PdfExporter
import com.example.thcsedu.ui.util.PdfReportHelper
import com.example.thcsedu.ui.util.PdfSubjectScore
import com.example.thcsedu.ui.util.StudentPdfReportData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoDiemScreen(
    viewModel: SoDiemViewModel,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedLop by remember { mutableStateOf("9A1") }
    var selectedMonHoc by remember { mutableStateOf("TOAN") }
    var selectedHocKy by remember { mutableStateOf(1) }

    LaunchedEffect(selectedLop, selectedMonHoc, selectedHocKy) {
        viewModel.loadSoDiem(
            maLop = selectedLop,
            maMonHoc = selectedMonHoc,
            hocKy = selectedHocKy
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Nhập Sổ Điểm Học Sinh", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trở về")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val pdfSubjects = uiState.dsHocSinhDiem.take(1).map { student ->
                                PdfSubjectScore(
                                    subjectName = "Toán",
                                    diemTX = student.dsDiemTX.joinToString(", "),
                                    diemGK = student.diemGiuaKy?.toString() ?: "N/A",
                                    diemCK = student.diemCuoiKy?.toString() ?: "N/A",
                                    diemTB = student.diemTBMon?.let { "%.1f".format(it) } ?: "N/A",
                                    nhanXet = student.nhanXet ?: "Học tập tốt"
                                )
                            } + listOf(
                                PdfSubjectScore("Ngữ Văn", "8.0, 7.5", "8.0", "8.0", "7.9", "Cần cố gắng diễn đạt"),
                                PdfSubjectScore("Tiếng Anh", "9.0, 9.5", "9.0", "9.5", "9.3", "Giao tiếp lưu loát"),
                                PdfSubjectScore("KHTN", "8.0, 8.5", "8.0", "8.5", "8.3", "Tiếp thu tốt"),
                                PdfSubjectScore("LS & ĐL", "8.0, 8.0", "8.0", "8.0", "8.0", "Hăng hái phát biểu"),
                                PdfSubjectScore("GDCD", "8.5, 8.5", "8.5", "8.5", "8.5", "Chấp hành nội quy tốt")
                            )

                            val pdfData = StudentPdfReportData(
                                studentName = uiState.dsHocSinhDiem.firstOrNull()?.hoTen ?: "Nguyễn Văn An",
                                studentId = uiState.dsHocSinhDiem.firstOrNull()?.maHocSinh ?: "HS01",
                                className = selectedLop,
                                teacherName = "Nguyễn Văn Trọng",
                                subjects = pdfSubjects,
                                overallGpa = 8.5f,
                                academicRank = "Giỏi",
                                conduct = "Tốt"
                            )
                            val pdfDoc = PdfReportHelper.generateGradeReportPdf(context, pdfData)
                            val pdfFile = PdfExporter.exportAndSavePdf(context, pdfDoc, "PhieuLienLac_${selectedLop}_HS01.pdf")
                            val result = snackbarHostState.showSnackbar(
                                message = "Đã xuất phiếu liên lạc PDF thành công!",
                                actionLabel = "Mở file",
                                duration = SnackbarDuration.Long
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                PdfExporter.openPdfFile(context, pdfFile)
                            }
                            PdfExporter.openPdfFile(context, pdfFile)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Xuất PDF",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(onClick = { viewModel.toggleGradeLock() }) {
                        Icon(
                            imageVector = if (uiState.isGradeLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = if (uiState.isGradeLocked) "Mở khóa sổ điểm" else "Khóa sổ điểm",
                            tint = if (uiState.isGradeLocked) Color.Red else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            if (!uiState.isGradeLocked) {
                FloatingActionButton(
                    onClick = {
                        viewModel.saveBangDiem()
                        Toast.makeText(context, "Đã lưu bảng điểm thành công!", Toast.LENGTH_SHORT).show()
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Lưu bảng điểm")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Filters Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bộ lọc thông tin",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState.isGradeLocked) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Red.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Đang khóa sổ điểm",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = selectedLop,
                            onValueChange = { selectedLop = it },
                            label = { Text("Mã Lớp") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = selectedMonHoc,
                            onValueChange = { selectedMonHoc = it },
                            label = { Text("Mã Môn") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = selectedHocKy.toString(),
                            onValueChange = { selectedHocKy = it.toIntOrNull() ?: 1 },
                            label = { Text("Học Kỳ") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(0.8f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Content: Student Grade List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Lỗi: ${uiState.errorMessage}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else if (uiState.dsHocSinhDiem.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không có học sinh nào trong lớp này.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.dsHocSinhDiem, key = { it.maHocSinh }) { item ->
                        Box(modifier = Modifier.animateItem()) {
                            StudentGradeCard(
                                item = item,
                                isLocked = uiState.isGradeLocked,
                                onUpdateTX = { index, value ->
                                    viewModel.updateDiemTX(item.maHocSinh, index, value)
                                },
                                onAddTX = { value ->
                                    viewModel.updateDiemTX(item.maHocSinh, item.dsDiemTX.size, value)
                                },
                                onUpdateGK = { value ->
                                    viewModel.updateDiemGK(item.maHocSinh, value)
                                },
                                onUpdateCK = { value ->
                                    viewModel.updateDiemCK(item.maHocSinh, value)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Reason for grade change audit dialog
    if (uiState.pendingGradeChange != null) {
        val change = uiState.pendingGradeChange!!
        var reasonText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { viewModel.dismissPendingGradeChange() },
            title = { Text("Lý do sửa điểm") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Thay đổi điểm ${change.loaiDiem} của học sinh (${change.maHocSinh}) từ [${change.diemCu}] thành [${change.diemMoi}]:")
                    OutlinedTextField(
                        value = reasonText,
                        onValueChange = { reasonText = it },
                        label = { Text("Nhập lý do sửa điểm (bắt buộc)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reasonText.isNotBlank()) {
                            change.onConfirm(reasonText)
                        } else {
                            Toast.makeText(context, "Vui lòng nhập lý do sửa điểm", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissPendingGradeChange() }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun StudentGradeCard(
    item: HocSinhDiemItemUiState,
    isLocked: Boolean,
    onUpdateTX: (Int, Float) -> Unit,
    onAddTX: (Float) -> Unit,
    onUpdateGK: (Float) -> Unit,
    onUpdateCK: (Float) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val (badgeBg, badgeText) = when {
        item.diemTBMon == null -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        item.diemTBMon >= 8.0f -> Color(0xFFD1FAE5) to Color(0xFF065F46)
        item.diemTBMon >= 6.5f -> Color(0xFFDBEAFE) to Color(0xFF1E40AF)
        item.diemTBMon >= 5.0f -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        else -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Student Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = item.hoTen,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mã HS: ${item.maHocSinh}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = "ĐTB: ${item.diemTBMon?.let { "%.1f".format(it) } ?: "N/A"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = badgeText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Điểm Thường Xuyên (ĐĐGtx):",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item.dsDiemTX.forEachIndexed { index, diem ->
                    var textVal by remember(diem) { mutableStateOf(diem.toString()) }
                    OutlinedTextField(
                        value = textVal,
                        onValueChange = { newVal ->
                            textVal = newVal
                            newVal.toFloatOrNull()?.let { onUpdateTX(index, it) }
                        },
                        label = { Text("TX${index + 1}") },
                        modifier = Modifier.width(72.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLocked,
                        readOnly = isLocked,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
                if (!isLocked) {
                    var newTxVal by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = newTxVal,
                        onValueChange = { newVal ->
                            newTxVal = newVal
                            newVal.toFloatOrNull()?.let {
                                onAddTX(it)
                                newTxVal = ""
                            }
                        },
                        label = { Text("+ Thêm") },
                        modifier = Modifier.width(85.dp),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var gkVal by remember(item.diemGiuaKy) {
                    mutableStateOf(item.diemGiuaKy?.toString() ?: "")
                }
                OutlinedTextField(
                    value = gkVal,
                    onValueChange = { newVal ->
                        gkVal = newVal
                        newVal.toFloatOrNull()?.let { onUpdateGK(it) }
                    },
                    label = { Text("ĐGK (Giữa kỳ)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLocked,
                    readOnly = isLocked,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                var ckVal by remember(item.diemCuoiKy) {
                    mutableStateOf(item.diemCuoiKy?.toString() ?: "")
                }
                OutlinedTextField(
                    value = ckVal,
                    onValueChange = { newVal ->
                        ckVal = newVal
                        newVal.toFloatOrNull()?.let { onUpdateCK(it) }
                    },
                    label = { Text("ĐCK (Cuối kỳ)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLocked,
                    readOnly = isLocked,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nhận xét của giáo viên:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.nhanXet?.ifBlank { "Chưa có nhận xét" } ?: "Chưa có nhận xét",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
