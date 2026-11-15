package com.example.thcsedu.ui.thongke

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaoCaoThongKeScreen(
    viewModel: BaoCaoThongKeViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Báo Cáo Thống Kê Theo Lớp", fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Class and Semester Selectors
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Chọn Lớp & Học Kỳ Báo Cáo",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Lớp:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            uiState.danhSachLop.forEach { lop ->
                                FilterChip(
                                    selected = uiState.maLopDangChon == lop.maLop,
                                    onClick = { viewModel.chonLop(lop.maLop) },
                                    label = { Text(lop.maLop) }
                                )
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Học kỳ:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            FilterChip(
                                selected = uiState.hocKyDangChon == 1,
                                onClick = { viewModel.chonHocKy(1) },
                                label = { Text("Học kỳ I") }
                            )
                            FilterChip(
                                selected = uiState.hocKyDangChon == 2,
                                onClick = { viewModel.chonHocKy(2) },
                                label = { Text("Học kỳ II") }
                            )
                        }
                    }
                }
            }

            // 1. Chuyên Cần
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF0D9488))
                                Text(
                                    text = "1. Báo Cáo Chuyên Cần",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFE0F2F1)
                            ) {
                                Text(
                                    text = "%.1f%% chuyên cần".format(uiState.chuyenCan.tyLeChuyenCan),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00796B),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { (uiState.chuyenCan.tyLeChuyenCan / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = Color(0xFF0D9488),
                            trackColor = Color(0xFFE0E0E0)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ThongKeBox(title = "Sĩ số", value = "${uiState.chuyenCan.tongSoHocSinh} HS", color = MaterialTheme.colorScheme.primary)
                            ThongKeBox(title = "Có mặt", value = "${uiState.chuyenCan.soLuotCoMat}", color = Color(0xFF2E7D32))
                            ThongKeBox(title = "Vắng có phép", value = "${uiState.chuyenCan.soLuotVangCoPhep}", color = Color(0xFFF57F17))
                            ThongKeBox(title = "Không phép", value = "${uiState.chuyenCan.soLuotVangKhongPhep}", color = Color(0xFFC62828))
                        }
                    }
                }
            }

            // 2. Học Lực 4 Mức theo Thông tư 22
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Grade, contentDescription = null, tint = Color(0xFF4F46E5))
                                Column {
                                    Text(
                                        text = "2. Báo Cáo Học Lực (Thông Tư 22)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Xếp loại 4 mức: Tốt / Khá / Đạt / Chưa đạt",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "ĐTB: %.1f".format(uiState.hocLuc.diemTrungBinhLop),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // 4 Mức Thông Tư 22
                        HocLucRow(
                            level = "Mức Tốt",
                            count = uiState.hocLuc.soLuongTot,
                            percent = uiState.hocLuc.tyLeTot,
                            badgeBg = Color(0xFFE8F5E9),
                            badgeColor = Color(0xFF2E7D32)
                        )
                        HocLucRow(
                            level = "Mức Khá",
                            count = uiState.hocLuc.soLuongKha,
                            percent = uiState.hocLuc.tyLeKha,
                            badgeBg = Color(0xFFE3F2FD),
                            badgeColor = Color(0xFF1565C0)
                        )
                        HocLucRow(
                            level = "Mức Đạt",
                            count = uiState.hocLuc.soLuongDat,
                            percent = uiState.hocLuc.tyLeDat,
                            badgeBg = Color(0xFFFFF8E1),
                            badgeColor = Color(0xFFF57F17)
                        )
                        HocLucRow(
                            level = "Mức Chưa đạt",
                            count = uiState.hocLuc.soLuongChuaDat,
                            percent = uiState.hocLuc.tyLeChuaDat,
                            badgeBg = Color(0xFFFFEBEE),
                            badgeColor = Color(0xFFC62828)
                        )
                    }
                }
            }

            // 3. Thi Đua Nề Nếp
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B))
                                Text(
                                    text = "3. Thi Đua & Nề Nếp Lớp",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFF3E0)
                            ) {
                                Text(
                                    text = "Xếp loại: ${uiState.neNep.danhGiaThiDua}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${uiState.neNep.diemThiDuaConLai} / 100",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.neNep.diemThiDuaConLai >= 80) Color(0xFF2E7D32) else Color(0xFFE65100)
                                )
                                Text(
                                    text = "Điểm thi đua tuần này (-${uiState.neNep.tongDiemBiTru} điểm vi phạm)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (uiState.neNep.danhSachViPham.isNotEmpty()) {
                            Text(
                                text = "Chi tiết các lỗi vi phạm sao đỏ:",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            uiState.neNep.danhSachViPham.forEach { vp ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "• Mã HS: ${vp.maHocSinh} - ${vp.tenLoi} (${vp.ngayViPham})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "-${vp.diemTru}đ",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Lớp không có vi phạm nào trong tuần. Rất tốt!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThongKeBox(title: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
        Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun HocLucRow(level: String, count: Int, percent: Float, badgeBg: Color, badgeColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = RoundedCornerShape(6.dp), color = badgeBg) {
            Text(
                text = level,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = badgeColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
        Text(
            text = "$count học sinh (%.1f%%)".format(percent),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
