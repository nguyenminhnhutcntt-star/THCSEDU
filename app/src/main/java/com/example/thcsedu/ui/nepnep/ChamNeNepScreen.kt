package com.example.thcsedu.ui.nepnep

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class CommonViolation(val name: String, val points: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChamNeNepScreen(
    viewModel: ChamNeNepViewModel,
    initialLop: String = "9A1",
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedLop by remember { mutableStateOf(initialLop) }
    var maHocSinh by remember { mutableStateOf("HS01") }
    var ngayViPham by remember { mutableStateOf("2026-10-13") }
    var selectedViolation by remember { mutableStateOf(CommonViolation("Đi trễ", 2)) }
    var classMenuExpanded by remember { mutableStateOf(false) }
    var violationMenuExpanded by remember { mutableStateOf(false) }

    val commonViolations = listOf(
        CommonViolation("Đi trễ", 2),
        CommonViolation("Không đồng phục", 5),
        CommonViolation("Nói chuyện riêng trong lớp", 3),
        CommonViolation("Không thuộc bài cũ", 4),
        CommonViolation("Quên đem vở bài tập", 3),
        CommonViolation("Vô kỷ luật / Gây gổ", 10)
    )

    LaunchedEffect(selectedLop) {
        viewModel.loadViPham(selectedLop)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chấm Nề Nếp & Sao Đỏ", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Log Violation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Ghi nhận lỗi vi phạm mới",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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

                        // Student ID Input
                        OutlinedTextField(
                            value = maHocSinh,
                            onValueChange = { maHocSinh = it },
                            label = { Text("Mã HS (VD: HS01)") },
                            singleLine = true,
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    // Violation Type Dropdown
                    ExposedDropdownMenuBox(
                        expanded = violationMenuExpanded,
                        onExpandedChange = { violationMenuExpanded = !violationMenuExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = "${selectedViolation.name} (-${selectedViolation.points}đ)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Lỗi vi phạm") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = violationMenuExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = violationMenuExpanded,
                            onDismissRequest = { violationMenuExpanded = false }
                        ) {
                            commonViolations.forEach { violation ->
                                DropdownMenuItem(
                                    text = { Text("${violation.name} (-${violation.points} điểm)") },
                                    onClick = {
                                        selectedViolation = violation
                                        violationMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (uiState.successMessage != null) {
                        Text(
                            text = uiState.successMessage ?: "",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.logViPham(
                                maHocSinh = maHocSinh,
                                maLop = selectedLop,
                                ngayViPham = ngayViPham,
                                tenLoi = selectedViolation.name,
                                diemTru = selectedViolation.points
                            ) {
                                maHocSinh = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Trừ Điểm Nề Nếp")
                    }
                }
            }

            Text(
                text = "Lịch sử vi phạm lớp $selectedLop",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Violations List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.viPhamList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Lớp $selectedLop chưa có lỗi vi phạm nào.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.viPhamList, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.maHocSinh} - ${item.tenLoi}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Ngày: ${item.ngayViPham}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = Color(0xFFF44336).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "-${item.diemTru} đ",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD32F2F),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
