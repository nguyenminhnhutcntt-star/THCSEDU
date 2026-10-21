package com.example.thcsedu.ui.parent

import android.widget.Toast
import kotlinx.coroutines.launch
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.thcsedu.ui.util.PdfExporter
import com.example.thcsedu.ui.util.PdfReportHelper
import com.example.thcsedu.ui.util.PdfSubjectScore
import com.example.thcsedu.ui.util.StudentPdfReportData

data class ParentFeatureItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val colors: List<Color>,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    viewModel: ParentDashboardViewModel,
    onNavigateToTaoDonNghi: () -> Unit,
    onNavigateToQuanLyHocPhi: () -> Unit,
    onNavigateToThongBao: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToThucDon: () -> Unit,
    onNavigateToTheHocSinh: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var isGeneratingPdf by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val features = listOf(
        ParentFeatureItem(
            title = "Thẻ Học Sinh",
            description = "Thẻ điện tử & Mã QR",
            icon = Icons.Default.CreditCard,
            colors = listOf(Color(0xFF3B82F6), Color(0xFF60A5FA)),
            onClick = onNavigateToTheHocSinh
        ),
        ParentFeatureItem(
            title = "Xin Nghỉ Học",
            description = "Gửi đơn cho GVCN",
            icon = Icons.AutoMirrored.Filled.Assignment,
            colors = listOf(Color(0xFFEC4899), Color(0xFFF472B6)),
            onClick = onNavigateToTaoDonNghi
        ),
        ParentFeatureItem(
            title = "Chat 1-1 GV",
            description = "Trao đổi với giáo viên",
            icon = Icons.Default.Chat,
            colors = listOf(Color(0xFF10B981), Color(0xFF34D399)),
            onClick = onNavigateToChat
        ),
        ParentFeatureItem(
            title = "Học Phí",
            description = "Thanh toán & Hóa đơn",
            icon = Icons.Default.CreditCard,
            colors = listOf(Color(0xFF0D9488), Color(0xFF2DD4BF)),
            onClick = onNavigateToQuanLyHocPhi
        ),
        ParentFeatureItem(
            title = "Thực Đơn",
            description = "Thực đơn bán trú tuần",
            icon = Icons.Default.Restaurant,
            colors = listOf(Color(0xFFF59E0B), Color(0xFFFBBF24)),
            onClick = onNavigateToThucDon
        ),
        ParentFeatureItem(
            title = "Thông Báo",
            description = "Tin tức từ nhà trường",
            icon = Icons.Default.Notifications,
            colors = listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA)),
            onClick = onNavigateToThongBao
        )
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Sổ Liên Lạc Điện Tử", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Trường THCS EDU", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToChat) {
                        Badge(containerColor = MaterialTheme.colorScheme.primary) { Text("1") }
                        Icon(Icons.Default.Chat, contentDescription = "Trò chuyện")
                    }
                    IconButton(onClick = onNavigateToThongBao) {
                        Badge(containerColor = MaterialTheme.colorScheme.error) { Text("2") }
                        Icon(Icons.Default.Notifications, contentDescription = "Thông báo")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Đăng xuất")
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
            // Student Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                    text = uiState.studentName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${uiState.className} • Hạnh kiểm: ${uiState.conduct}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = onNavigateToTheHocSinh) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Xem thẻ học sinh",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                isGeneratingPdf = true
                                val pdfData = StudentPdfReportData(
                                    studentName = uiState.studentName,
                                    studentId = "HS01",
                                    className = uiState.className,
                                    teacherName = "Nguyễn Văn Trọng",
                                    subjects = uiState.subjectScores.map { item ->
                                        PdfSubjectScore(
                                            subjectName = item.tenMonHoc,
                                            diemTX = "8.0, 8.5",
                                            diemGK = "8.0",
                                            diemCK = "${item.diemTB}",
                                            diemTB = "${item.diemTB}",
                                            nhanXet = item.danhGia
                                        )
                                    },
                                    overallGpa = 8.5f,
                                    academicRank = "Giỏi",
                                    conduct = uiState.conduct
                                )
                                val pdfDoc = PdfReportHelper.generateGradeReportPdf(context, pdfData)
                                val pdfFile = PdfExporter.exportAndSavePdf(context, pdfDoc, "PhieuLienLac_HS01.pdf")
                                isGeneratingPdf = false
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Đã xuất phiếu liên lạc PDF thành công!",
                                        actionLabel = "Mở file",
                                        duration = SnackbarDuration.Long
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        PdfExporter.openPdfFile(context, pdfFile)
                                    }
                                }
                                PdfExporter.openPdfFile(context, pdfFile)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isGeneratingPdf,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (isGeneratingPdf) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Đang tạo PDF...")
                            } else {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Xuất Phiếu Liên Lạc PDF", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Violations Warning Card if present
            if (uiState.violations.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFE65100)
                            )
                            Column {
                                Text(
                                    text = "Ghi Nhận Vi Phạm Nề Nếp",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                                Text(
                                    text = "Có ${uiState.violations.size} ghi nhận vi phạm nề nếp tuần này.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }
                }
            }

            // Line Chart Component for GPA history
            item {
                SemesterScoreLineChart(semesterHistory = uiState.semesterHistory)
            }

            // Features Grid Section
            item {
                Text(
                    text = "Dịch Vụ & Tiện Ích",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    items(features) { feature ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clickable { feature.onClick() },
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
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
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(brush = Brush.linearGradient(feature.colors)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = feature.icon,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = feature.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
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

            // Full Subject Scores Section
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
                            Text(
                                text = "Bảng Điểm Các Môn Học",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${uiState.subjectScores.size} môn",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        uiState.subjectScores.forEach { subject ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = subject.tenMonHoc,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (subject.danhGia) {
                                        "Xuất sắc" -> Color(0xFFE8F5E9)
                                        "Giỏi" -> Color(0xFFE3F2FD)
                                        else -> Color(0xFFFFF8E1)
                                    }
                                ) {
                                    Text(
                                        text = "${subject.diemTB} • ${subject.danhGia}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = when (subject.danhGia) {
                                            "Xuất sắc" -> Color(0xFF2E7D32)
                                            "Giỏi" -> Color(0xFF1565C0)
                                            else -> Color(0xFFF57F17)
                                        },
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
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

@Composable
fun SemesterScoreLineChart(
    semesterHistory: List<SemesterScore>,
    modifier: Modifier = Modifier
) {
    if (semesterHistory.isEmpty()) return

    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Biểu Đồ Tiến Trình Điểm Trung Bình",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(top = 20.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
            ) {
                val width = size.width
                val height = size.height

                val minScore = 6.0f
                val maxScore = 10.0f
                val scoreRange = maxScore - minScore

                val points = semesterHistory.mapIndexed { index, item ->
                    val x = if (semesterHistory.size > 1) {
                        index * (width / (semesterHistory.size - 1))
                    } else {
                        width / 2
                    }
                    val normalizedY = (item.diemTrungBinhChung - minScore) / scoreRange
                    val y = height - (normalizedY * height)
                    Offset(x, y)
                }

                for (i in 0..3) {
                    val gridY = height * (i / 3f)
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        start = Offset(0f, gridY),
                        end = Offset(width, gridY),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                val fillPath = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points.first().x, height)
                        lineTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                        lineTo(points.last().x, height)
                        close()
                    }
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.3f),
                            primaryColor.copy(alpha = 0.0f)
                        )
                    )
                )

                val linePath = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                    }
                }
                drawPath(
                    path = linePath,
                    color = primaryColor,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                points.forEach { point ->
                    drawCircle(
                        color = Color.White,
                        radius = 6.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = primaryColor,
                        radius = 4.dp.toPx(),
                        center = point
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                semesterHistory.forEach { semester ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${semester.diemTrungBinhChung}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                        Text(
                            text = semester.hocKy,
                            style = MaterialTheme.typography.labelSmall,
                            color = onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
