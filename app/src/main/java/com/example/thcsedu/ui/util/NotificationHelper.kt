package com.example.thcsedu.ui.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.thcsedu.R
import com.example.thcsedu.data.local.dao.ThongBaoDao
import com.example.thcsedu.data.local.entity.ThongBaoEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val thongBaoDao: ThongBaoDao
) {

    companion object {
        const val CHANNEL_ID = "school_alerts_channel"
        const val CHANNEL_NAME = "Thông Báo Học Đường THCSEDU"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Kênh nhận thông báo điểm số, nghỉ học và nề nếp"
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun sendPushNotification(title: String, message: String, notificationId: Int = (System.currentTimeMillis() % 10000).toInt()) {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (hasPermission) {
            try {
                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_app_logo)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)

                NotificationManagerCompat.from(context).notify(notificationId, builder.build())
            } catch (e: SecurityException) {
                // Quyền chưa được cấp ở cấp hệ thống
            } catch (e: Exception) {
                // Bỏ qua lỗi hiển thị push notification hệ thống
            }
        }
    }

    /**
     * Thông báo đẩy khi có điểm mới
     */
    fun notifyDiemMoi(studentName: String, subjectName: String, score: Float) {
        val title = "Điểm Mới Cập Nhật - $studentName"
        val message = "Em $studentName vừa có điểm môn $subjectName: %.1f điểm. Hãy kiểm tra sổ điểm điện tử.".format(score)

        sendPushNotification(title, message, 1001)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                thongBaoDao.insertThongBao(
                    ThongBaoEntity(
                        tieuDe = title,
                        noiDung = message,
                        ngayGui = getTodayDateString(),
                        nguoiGui = "Giáo viên bộ môn $subjectName"
                    )
                )
            } catch (e: Exception) {
                // Bỏ qua lỗi lưu DB nếu có
            }
        }
    }

    /**
     * Thông báo đẩy khi có đơn xin nghỉ học
     */
    fun notifyDonXinNghi(studentName: String, reason: String, dates: String) {
        val title = "Đơn Xin Nghỉ Mới - $studentName"
        val message = "Phụ huynh em $studentName đã gửi đơn xin nghỉ ($dates). Lý do: $reason."

        sendPushNotification(title, message, 1002)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                thongBaoDao.insertThongBao(
                    ThongBaoEntity(
                        tieuDe = title,
                        noiDung = message,
                        ngayGui = getTodayDateString(),
                        nguoiGui = "Phụ huynh học sinh $studentName"
                    )
                )
            } catch (e: Exception) {
                // Bỏ qua
            }
        }
    }

    /**
     * Thông báo đẩy khi có vi phạm nề nếp
     */
    fun notifyViPham(studentName: String, violation: String, deducted: Int) {
        val title = "Cảnh Báo Nề Nếp - $studentName"
        val message = "Học sinh $studentName vi phạm lỗi: $violation (-$deducted điểm thi đua). Nhắc nhở học sinh nghiêm túc chấp hành."

        sendPushNotification(title, message, 1003)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                thongBaoDao.insertThongBao(
                    ThongBaoEntity(
                        tieuDe = title,
                        noiDung = message,
                        ngayGui = getTodayDateString(),
                        nguoiGui = "Ban Thi Đua / Đội Sao Đỏ"
                    )
                )
            } catch (e: Exception) {
                // Bỏ qua
            }
        }
    }
}
