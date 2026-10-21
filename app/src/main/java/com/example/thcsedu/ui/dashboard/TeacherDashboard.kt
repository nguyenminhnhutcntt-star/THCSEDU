package com.example.thcsedu.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TeacherMenuFeature(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboard(
    viewModel: TeacherDashboardViewModel,
    onNavigateToSoDiem: () -> Unit,
    onNavigateToDiemDanh: () -> Unit,
    onNavigateToTimetable: () -> Unit,
    onNavigateToSoDauBai: () -> Unit,
    onNavigateToDuyetDonNghi: () -> Unit,
    onNavigateToChamNeNep: () -> Unit,
    onNavigateToThongBao: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToQuetMaQr: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val currentDateStr = remember {
        val sdf = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("vi", "VN"))
        sdf.format(Date()).replaceFirstChar { it.uppercase() }
    }

    val features = listOf(
        TeacherMenuFeature(
            title = "Sổ Điểm",
            description = "Nhập & quản lý điểm",
            icon = Icons.Default.Edit,
            gradientColors = listOf(Color(0xFF4F46E5), Color(0xFF818CF8)),
            onClick = onNavigateToSoDiem
        ),
        TeacherMenuFeature(
            title = "Điểm Danh",
            description = "Quản lý chuyên cần",
            icon = Icons.Default.CheckCircle,
            gradientColors = listOf(Color(0xFF0D9488), Color(0xFF2DD4BF)),
            onClick = onNavigateToDiemDanh
        ),
        TeacherMenuFeature(
            title = "Quét Mã QR Cổng",
            description = "Check-in học sinh vào cổng",
            icon = Icons.Default.QrCodeScanner,
            gradientColors = listOf(Color(0xFF0284C7), Color(0xFF38BDF8)),
            onClick = onNavigateToQuetMaQr
        ),
        TeacherMenuFeature(
            title = "Chấm Nề Nếp",
            description = "Ghi nhận lỗi sao đỏ",
            icon = Icons.Default.Star,
            gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFFBBF24)),
            onClick = onNavigateToChamNeNep
        ),
        TeacherMenuFeature(
            title = "Duyệt Đơn Nghỉ",
            description = "Xét duyệt đơn phụ huynh",
            icon = Icons.AutoMirrored.Filled.Assignment,
            gradientColors = listOf(Color(0xFFEC4899), Color(0xFFF472B6)),
            onClick = onNavigateToDuyetDonNghi
        ),
        TeacherMenuFeature(
            title = "Sổ Đầu Bài",
            description = "Nội dung & xếp loại tiết",
            icon = Icons.Default.Book,
            gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA)),
            onClick = onNavigateToSoDauBai
        ),
        TeacherMenuFeature(
            title = "Thông Báo",
            description = "Bảng tin nhà trường",
            icon = Icons.Default.Notifications,
            gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF60A5FA)),
            onClick = onNavigateToThongBao
        ),
        TeacherMenuFeature(
            title = "Chat 1-1",
            description = "Trao đổi phụ huynh",
            icon = Icons.Default.Chat,
            gradientColors = listOf(Color(0xFF10B981), Color(0xFF34D399)),
            onClick = onNavigateToChat
        ),
        TeacherMenuFeature(
            title = "Thời Khóa Biểu",
            description = "Lịch giảng dạy tuần",
            icon = Icons.Default.DateRange,
            gradientColors = listOf(Color(0xFF6366F1), Color(0xFF818CF8)),
            onClick = onNavigateToTimetable
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Quản Trị Giáo Viên", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = currentDateStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 4.dp)) {
                        IconButton(onClick = onNavigateToThongBao) {
                            Badge(containerColor = MaterialTheme.colorScheme.error) {
                                Text("2")
                            }
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Giáo viên chủ nhiệm",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Thầy Nguyễn Văn Trọng",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Chủ nhiệm lớp ${uiState.selectedClass}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Điểm Nề Nếp Tuần (Lớp ${uiState.selectedClass})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tổng số lỗi vi phạm: ${uiState.violations.size} lỗi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "${uiState.weeklyMeritScore}/100 đ",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Text(
                text = "Chức năng quản lý",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(features) { feature ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clickable { feature.onClick() },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(brush = Brush.linearGradient(feature.gradientColors)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = feature.icon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = feature.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = feature.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
