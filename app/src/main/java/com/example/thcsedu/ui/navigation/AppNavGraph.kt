package com.example.thcsedu.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thcsedu.data.preferences.UserPreferencesRepository
import com.example.thcsedu.ui.auth.AuthViewModel
import com.example.thcsedu.ui.auth.LoginScreen
import com.example.thcsedu.ui.auth.QuenMatKhauScreen
import com.example.thcsedu.ui.auth.QuenMatKhauViewModel
import com.example.thcsedu.ui.auth.UserRole
import com.example.thcsedu.ui.chat.ChatScreen
import com.example.thcsedu.ui.chat.ChatViewModel
import com.example.thcsedu.ui.dashboard.TeacherDashboard
import com.example.thcsedu.ui.dashboard.TeacherDashboardViewModel
import com.example.thcsedu.ui.diemdanh.DiemDanhScreen
import com.example.thcsedu.ui.diemdanh.DiemDanhViewModel
import com.example.thcsedu.ui.donnghi.DonNghiViewModel
import com.example.thcsedu.ui.donnghi.DuyetDonNghiScreen
import com.example.thcsedu.ui.donnghi.TaoDonXinNghiScreen
import com.example.thcsedu.ui.hocphi.QuanLyHocPhiScreen
import com.example.thcsedu.ui.hocphi.QuanLyHocPhiViewModel
import com.example.thcsedu.ui.nepnep.ChamNeNepScreen
import com.example.thcsedu.ui.nepnep.ChamNeNepViewModel
import com.example.thcsedu.ui.parent.ParentDashboardScreen
import com.example.thcsedu.ui.parent.ParentDashboardViewModel
import com.example.thcsedu.ui.qrcode.QuetMaQrScreen
import com.example.thcsedu.ui.qrcode.QuetMaQrViewModel
import com.example.thcsedu.ui.sodaubai.SoDauBaiScreen
import com.example.thcsedu.ui.sodaubai.SoDauBaiViewModel
import com.example.thcsedu.ui.sodiem.SoDiemScreen
import com.example.thcsedu.ui.sodiem.SoDiemViewModel
import com.example.thcsedu.ui.thehocsinh.TheHocSinhScreen
import com.example.thcsedu.ui.thehocsinh.TheHocSinhViewModel
import com.example.thcsedu.ui.thongbao.ThongBaoScreen
import com.example.thcsedu.ui.thongbao.ThongBaoViewModel
import com.example.thcsedu.ui.thucdon.ThucDonScreen
import com.example.thcsedu.ui.thucdon.ThucDonViewModel
import com.example.thcsedu.ui.timetable.TimetableScreen
import com.example.thcsedu.ui.timetable.TimetableViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object TeacherDashboard : Screen("teacher_dashboard")
    object ParentDashboard : Screen("parent_dashboard")
    object SoDiem : Screen("so_diem")
    object DiemDanh : Screen("diem_danh")
    object Timetable : Screen("timetable")
    object SoDauBai : Screen("so_dau_bai")
    object TaoDonNghi : Screen("tao_don_nghi")
    object DuyetDonNghi : Screen("duyet_don_nghi")
    object ChamNeNep : Screen("cham_ne_nep")
    object QuanLyHocPhi : Screen("quan_ly_hoc_phi")
    object ThongBao : Screen("thong_bao")
    object Chat : Screen("chat")
    object ThucDon : Screen("thuc_don")
    object TheHocSinh : Screen("the_hoc_sinh")
    object QuetMaQr : Screen("quet_ma_qr")
    object QuenMatKhau : Screen("quen_mat_khau")
}

@Composable
fun AppNavGraph(
    userPreferencesRepository: UserPreferencesRepository
) {
    val navController = rememberNavController()
    val token by userPreferencesRepository.tokenFlow.collectAsState(initial = null)
    val role by userPreferencesRepository.roleFlow.collectAsState(initial = null)

    val startDestination = when {
        token.isNullOrBlank() -> Screen.Login.route
        role == UserRole.TEACHER.value -> Screen.TeacherDashboard.route
        role == UserRole.PARENT.value -> Screen.ParentDashboard.route
        else -> Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    val targetRoute = if (authViewModel.uiState.value.selectedRole == UserRole.TEACHER) {
                        Screen.TeacherDashboard.route
                    } else {
                        Screen.ParentDashboard.route
                    }
                    navController.navigate(targetRoute) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate(Screen.QuenMatKhau.route)
                }
            )
        }
        composable(Screen.TeacherDashboard.route) {
            val teacherDashboardViewModel: TeacherDashboardViewModel = hiltViewModel()
            val authViewModel: AuthViewModel = hiltViewModel()
            TeacherDashboard(
                viewModel = teacherDashboardViewModel,
                onNavigateToSoDiem = { navController.navigate(Screen.SoDiem.route) },
                onNavigateToDiemDanh = { navController.navigate(Screen.DiemDanh.route) },
                onNavigateToTimetable = { navController.navigate(Screen.Timetable.route) },
                onNavigateToSoDauBai = { navController.navigate(Screen.SoDauBai.route) },
                onNavigateToDuyetDonNghi = { navController.navigate(Screen.DuyetDonNghi.route) },
                onNavigateToChamNeNep = { navController.navigate(Screen.ChamNeNep.route) },
                onNavigateToThongBao = { navController.navigate(Screen.ThongBao.route) },
                onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                onNavigateToQuetMaQr = { navController.navigate(Screen.QuetMaQr.route) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.ParentDashboard.route) {
            val parentViewModel: ParentDashboardViewModel = hiltViewModel()
            val authViewModel: AuthViewModel = hiltViewModel()
            ParentDashboardScreen(
                viewModel = parentViewModel,
                onNavigateToTaoDonNghi = { navController.navigate(Screen.TaoDonNghi.route) },
                onNavigateToQuanLyHocPhi = { navController.navigate(Screen.QuanLyHocPhi.route) },
                onNavigateToThongBao = { navController.navigate(Screen.ThongBao.route) },
                onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                onNavigateToThucDon = { navController.navigate(Screen.ThucDon.route) },
                onNavigateToTheHocSinh = { navController.navigate(Screen.TheHocSinh.route) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.SoDiem.route) {
            val soDiemViewModel: SoDiemViewModel = hiltViewModel()
            SoDiemScreen(
                viewModel = soDiemViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.DiemDanh.route) {
            val diemDanhViewModel: DiemDanhViewModel = hiltViewModel()
            DiemDanhScreen(
                viewModel = diemDanhViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Timetable.route) {
            val timetableViewModel: TimetableViewModel = hiltViewModel()
            TimetableScreen(
                viewModel = timetableViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.SoDauBai.route) {
            val soDauBaiViewModel: SoDauBaiViewModel = hiltViewModel()
            SoDauBaiScreen(
                viewModel = soDauBaiViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.TaoDonNghi.route) {
            val donNghiViewModel: DonNghiViewModel = hiltViewModel()
            TaoDonXinNghiScreen(
                viewModel = donNghiViewModel,
                onSubmitted = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.DuyetDonNghi.route) {
            val donNghiViewModel: DonNghiViewModel = hiltViewModel()
            DuyetDonNghiScreen(
                viewModel = donNghiViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.ChamNeNep.route) {
            val chamNeNepViewModel: ChamNeNepViewModel = hiltViewModel()
            ChamNeNepScreen(
                viewModel = chamNeNepViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.QuanLyHocPhi.route) {
            val hocPhiViewModel: QuanLyHocPhiViewModel = hiltViewModel()
            QuanLyHocPhiScreen(
                viewModel = hocPhiViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.ThongBao.route) {
            val thongBaoViewModel: ThongBaoViewModel = hiltViewModel()
            val isTeacher = role == UserRole.TEACHER.value
            ThongBaoScreen(
                viewModel = thongBaoViewModel,
                isTeacher = isTeacher,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Chat.route) {
            val chatViewModel: ChatViewModel = hiltViewModel()
            val currentUserId = if (role == UserRole.TEACHER.value) "TEACHER" else "PARENT"
            val peerId = if (role == UserRole.TEACHER.value) "PARENT" else "TEACHER"
            ChatScreen(
                viewModel = chatViewModel,
                currentUserId = currentUserId,
                peerId = peerId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.ThucDon.route) {
            val thucDonViewModel: ThucDonViewModel = hiltViewModel()
            ThucDonScreen(
                viewModel = thucDonViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.TheHocSinh.route) {
            val theHocSinhViewModel: TheHocSinhViewModel = hiltViewModel()
            TheHocSinhScreen(
                viewModel = theHocSinhViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.QuetMaQr.route) {
            val quetMaQrViewModel: QuetMaQrViewModel = hiltViewModel()
            QuetMaQrScreen(
                viewModel = quetMaQrViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.QuenMatKhau.route) {
            val quenMatKhauViewModel: QuenMatKhauViewModel = hiltViewModel()
            QuenMatKhauScreen(
                viewModel = quenMatKhauViewModel,
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}
