package com.example.thcsedu.ui.sodaubai

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoDauBaiScreen(
    viewModel: SoDauBaiViewModel,
    initialLop: String = "9A1",
    initialDate: String = "2026-10-13",
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedLop by remember { mutableStateOf(initialLop) }
    var classMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(selectedLop, uiState.ngay) {
        viewModel.loadSoDauBai(selectedLop, uiState.ngay)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sổ Đầu Bài Lớp $selectedLop", fontWeight = FontWeight.Bold) },
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Filters: Class Selector & Date Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = classMenuExpanded,
                    onExpandedChange = { classMenuExpanded = !classMenuExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = "Lớp: $selectedLop",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classMenuExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = classMenuExpanded,
                        onDismissRequest = { classMenuExpanded = false }
                    ) {
                        listOf("9A1", "9A2").forEach { lop ->
                            DropdownMenuItem(
                                text = { Text("Lớp $lop") },
                                onClick = {
                                    selectedLop = lop
                                    classMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedCard(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(56.dp),
                    onClick = {
                        val nextDate = if (uiState.ngay == "2026-10-13") "2026-10-14" else "2026-10-13"
                        viewModel.updateClassAndDate(selectedLop, nextDate)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = uiState.ngay, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Danh sách các tiết học (Tiết 1 - 5)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 5 Periods list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(5) { index ->
                    val tiet = index + 1
                    val entry = uiState.entries.find { it.tietHoc == tiet }

                    PeriodJournalCard(
                        tietHoc = tiet,
                        entry = entry,
                        onClick = {
                            viewModel.openEditDialog(tiet)
                        }
                    )
                }
            }
        }

        // Edit/Add Dialog
        if (uiState.isEditing && uiState.selectedPeriod != null) {
            val period = uiState.selectedPeriod!!
            val existing = uiState.entries.find { it.tietHoc == period }

            SoDauBaiEditDialog(
                tietHoc = period,
                initialEntry = existing,
                onDismiss = { viewModel.closeEditDialog() },
                onSave = { tenMon, tenBaiDay, nhanXet, xepLoaiTiet, giaoVienId ->
                    viewModel.saveEntry(
                        maLop = selectedLop,
                        ngay = uiState.ngay,
                        tietHoc = period,
                        tenMon = tenMon,
                        tenBaiDay = tenBaiDay,
                        nhanXet = nhanXet,
                        xepLoaiTiet = xepLoaiTiet,
                        giaoVienId = giaoVienId
                    )
                }
            )
        }
    }
}

@Composable
fun PeriodJournalCard(
    tietHoc: Int,
    entry: com.example.thcsedu.data.local.entity.SoDauBaiEntity?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "T$tietHoc", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = entry?.tenMon ?: "Chưa cập nhật môn học",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Bài dạy: ${entry?.tenBaiDay?.ifBlank { "Chưa có nội dung" } ?: "Chưa có nội dung"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!entry?.nhanXet.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Nhận xét: ${entry.nhanXet}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Grade Chip ("A", "B", "C", "D")
            val grade = entry?.xepLoaiTiet ?: "A"
            val (chipColor, chipText) = when (grade) {
                "A" -> Color(0xFF4CAF50) to "A - Tốt"
                "B" -> Color(0xFF2196F3) to "B - Khá"
                "C" -> Color(0xFFFF9800) to "C - TB"
                else -> Color(0xFFF44336) to "D - Yếu"
            }

            Surface(
                shape = MaterialTheme.shapes.small,
                color = chipColor.copy(alpha = 0.2f)
            ) {
                Text(
                    text = chipText,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = chipColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoDauBaiEditDialog(
    tietHoc: Int,
    initialEntry: com.example.thcsedu.data.local.entity.SoDauBaiEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var tenMon by remember { mutableStateOf(initialEntry?.tenMon ?: "Toán") }
    var tenBaiDay by remember { mutableStateOf(initialEntry?.tenBaiDay ?: "") }
    var nhanXet by remember { mutableStateOf(initialEntry?.nhanXet ?: "") }
    var xepLoaiTiet by remember { mutableStateOf(initialEntry?.xepLoaiTiet ?: "A") }
    var giaoVienId by remember { mutableStateOf(initialEntry?.giaoVienId ?: "GV.An") }
    var gradeMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cập nhật Tiết $tietHoc") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = tenMon,
                    onValueChange = { tenMon = it },
                    label = { Text("Tên Môn") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tenBaiDay,
                    onValueChange = { tenBaiDay = it },
                    label = { Text("Tên Bài Dạy / Nội Dung") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nhanXet,
                    onValueChange = { nhanXet = it },
                    label = { Text("Nhận xét tiết học") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = giaoVienId,
                    onValueChange = { giaoVienId = it },
                    label = { Text("Giáo viên bộ môn") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Grade Dropdown
                ExposedDropdownMenuBox(
                    expanded = gradeMenuExpanded,
                    onExpandedChange = { gradeMenuExpanded = !gradeMenuExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = "Xếp loại: $xepLoaiTiet",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Xếp loại tiết") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gradeMenuExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = gradeMenuExpanded,
                        onDismissRequest = { gradeMenuExpanded = false }
                    ) {
                        listOf("A", "B", "C", "D").forEach { grade ->
                            DropdownMenuItem(
                                text = { Text(when(grade) { "A" -> "A - Tốt"; "B" -> "B - Khá"; "C" -> "C - Trung bình"; else -> "D - Yếu" }) },
                                onClick = {
                                    xepLoaiTiet = grade
                                    gradeMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(tenMon, tenBaiDay, nhanXet, xepLoaiTiet, giaoVienId)
            }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
