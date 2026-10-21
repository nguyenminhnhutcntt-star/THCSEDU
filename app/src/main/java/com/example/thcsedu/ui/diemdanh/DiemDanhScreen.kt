package com.example.thcsedu.ui.diemdanh

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun DiemDanhScreen(
    viewModel: DiemDanhViewModel,
    initialLop: String = "9A1",
    initialDate: String = "2026-10-13",
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedLop by remember { mutableStateOf(initialLop) }
    var classMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(selectedLop, uiState.selectedDate) {
        viewModel.loadAttendance(selectedLop, uiState.selectedDate)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Điểm Danh Học Sinh", fontWeight = FontWeight.Bold) },
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
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { viewModel.submitBatchAttendance() },
                        enabled = !uiState.isSaving && !uiState.isLoading && uiState.students.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Lưu Điểm Danh", style = MaterialTheme.typography.titleMedium)
                        }
                    }
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
            // Filters Row: Class Selector & Date Picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Class Dropdown
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

                // Date Button Card
                OutlinedCard(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(56.dp),
                    onClick = {
                        val nextDate = if (uiState.selectedDate == "2026-10-13") "2026-10-14" else "2026-10-13"
                        viewModel.updateDate(nextDate)
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
                        Text(text = uiState.selectedDate, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (uiState.successMessage != null) {
                Text(
                    text = uiState.successMessage ?: "",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.students.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không có học sinh trong lớp $selectedLop.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.students, key = { it.maHocSinh }) { student ->
                        StudentAttendanceRow(
                            student = student,
                            onStatusChanged = { newStatus ->
                                viewModel.updateStudentStatus(student.maHocSinh, newStatus)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentAttendanceRow(
    student: StudentAttendanceItem,
    onStatusChanged: (TrangThaiDiemDanh) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "${student.maHocSinh} - ${student.hoTen}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Tri-State Toggle / Segmented Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TrangThaiDiemDanh.entries.forEach { status ->
                    val isSelected = student.trangThai == status
                    val containerColor = when (status) {
                        TrangThaiDiemDanh.CO_MAT -> if (isSelected) Color(0xFF4CAF50) else Color(0xFFE8F5E9)
                        TrangThaiDiemDanh.VANG_PHEP -> if (isSelected) Color(0xFFFF9800) else Color(0xFFFFF3E0)
                        TrangThaiDiemDanh.VANG_KHONG_PHEP -> if (isSelected) Color(0xFFF44336) else Color(0xFFFFEBEE)
                    }
                    val contentColor = if (isSelected) Color.White else Color.DarkGray

                    Button(
                        onClick = { onStatusChanged(status) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = containerColor,
                            contentColor = contentColor
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            text = status.displayName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
