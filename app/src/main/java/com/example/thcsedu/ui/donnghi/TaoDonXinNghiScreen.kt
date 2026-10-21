package com.example.thcsedu.ui.donnghi

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaoDonXinNghiScreen(
    viewModel: DonNghiViewModel,
    onSubmitted: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    var maHocSinh by remember { mutableStateOf("HS01") }
    var tuNgay by remember { mutableStateOf("2026-10-13") }
    var denNgay by remember { mutableStateOf("2026-10-14") }
    var lyDo by remember { mutableStateOf("Bị cảm sốt, xin phép nghỉ 2 ngày.") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tạo Đơn Xin Nghỉ Phép", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trở về")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Thông tin xin nghỉ học cho con",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = maHocSinh,
                onValueChange = { maHocSinh = it },
                label = { Text("Mã học sinh (VD: HS01)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tuNgay,
                onValueChange = { tuNgay = it },
                label = { Text("Từ ngày (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = denNgay,
                onValueChange = { denNgay = it },
                label = { Text("Đến ngày (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lyDo,
                onValueChange = { lyDo = it },
                label = { Text("Lý do xin nghỉ") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

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

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.submitDon(maHocSinh, tuNgay, denNgay, lyDo) {
                        onSubmitted()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Gửi Đơn Xin Nghỉ", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
