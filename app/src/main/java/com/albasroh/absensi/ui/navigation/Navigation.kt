package com.albasroh.absensi.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.albasroh.absensi.AbsensiApplication
import com.albasroh.absensi.R as AppR
import com.albasroh.absensi.ui.admin.AdminAttendance
import com.albasroh.absensi.ui.admin.Classes
import com.albasroh.absensi.ui.admin.Reports
import com.albasroh.absensi.ui.admin.Settings
import com.albasroh.absensi.ui.admin.Students
import com.albasroh.absensi.ui.admin.Teachers
import com.albasroh.absensi.ui.auth.Login
import com.albasroh.absensi.ui.modern.Chatroom
import com.albasroh.absensi.ui.modern.LeaveRequests
import com.albasroh.absensi.ui.modern.ModernAdminHome
import com.albasroh.absensi.ui.modern.ModernReports
import com.albasroh.absensi.ui.modern.ModernStudentHome
import com.albasroh.absensi.ui.modern.ModernTeacherHome
import com.albasroh.absensi.ui.student.StudentQR
import com.albasroh.absensi.ui.student.StudentStats
import com.albasroh.absensi.ui.teacher.History
import com.albasroh.absensi.ui.teacher.Profile
import com.albasroh.absensi.ui.teacher.Scanner
import com.albasroh.absensi.ui.teacher.Session
import com.albasroh.absensi.ui.teacher.TeacherStudents
import com.albasroh.absensi.ui.teacher.Today

object R {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val ADMIN = "admin"
    const val STUDENTS = "students"
    const val TEACHERS = "teachers"
    const val CLASSES = "classes"
    const val ATTENDANCE = "attendance"
    const val REPORTS = "reports"
    const val SETTINGS = "settings"
    const val TEACHER = "teacher"
    const val SESSION = "session"
    const val SCANNER = "scanner"
    const val TODAY = "today"
    const val STUDENTS_T = "students_t"
    const val HISTORY = "history"
    const val STUDENT = "student"
    const val QR = "qr"
    const val STATS = "stats"
    const val PROFILE = "profile"
    const val LEAVE = "leave"
    const val CHAT = "chat"
}

@Composable
fun Nav(
    app: AbsensiApplication,
    vm: AppVM,
    nav: NavHostController
) {
    val sessionReady by vm.sessionReady.collectAsState()
    val user by vm.user.collectAsState()

    NavHost(
        navController = nav,
        startDestination = R.SPLASH
    ) {
        composable(R.SPLASH) {
            androidx.compose.runtime.LaunchedEffect(sessionReady, user?.role) {
                if (!sessionReady) return@LaunchedEffect

                val destination = when (user?.role) {
                    "ADMIN" -> R.ADMIN
                    "GURU" -> R.TEACHER
                    "MURID" -> R.STUDENT
                    else -> R.LOGIN
                }

                nav.navigate(destination) {
                    popUpTo(R.SPLASH) { inclusive = true }
                    launchSingleTop = true
                }
            }

            SplashContent()
        }

        composable(R.LOGIN) { Login(vm, nav) }
        composable(R.ADMIN) { ModernAdminHome(app, vm, nav) }
        composable(R.STUDENTS) { Students(app, vm, nav) }
        composable(R.TEACHERS) { Teachers(app, vm, nav) }
        composable(R.CLASSES) { Classes(app, vm, nav) }
        composable(R.ATTENDANCE) { AdminAttendance(app, vm, nav) }
        composable(R.REPORTS) { ModernReports(app, vm, nav) }
        composable(R.SETTINGS) { Settings(vm, nav) }
        composable(R.TEACHER) { ModernTeacherHome(app, vm, nav) }
        composable(R.SESSION) { Session(app, vm, nav) }
        composable(R.SCANNER) { Scanner(app, vm, nav) }
        composable(R.TODAY) { Today(app, vm, nav) }
        composable(R.STUDENTS_T) { TeacherStudents(app, vm, nav) }
        composable(R.HISTORY) { History(app, vm, nav) }
        composable(R.STUDENT) { ModernStudentHome(app, vm, nav) }
        composable(R.QR) { StudentQR(app, vm, nav) }
        composable(R.STATS) { StudentStats(app, vm, nav) }
        composable(R.PROFILE) { Profile(vm, nav) }
        composable(R.LEAVE) { LeaveRequests(app, vm, nav) }
        composable(R.CHAT) { Chatroom(app, vm, nav) }
    }
}

@Composable
private fun SplashContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D47B8), Color(0xFF1769E0), Color(0xFFF5F9FF))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(132.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(AppR.drawable.logo_mts_al_basroh),
                    contentDescription = "Logo MTs-Al Basroh",
                    modifier = Modifier.size(112.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(Modifier.size(18.dp))
            Text(
                "MTs-Al Basroh",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Text(
                "Absensi Digital",
                color = Color.White.copy(alpha = .86f)
            )
            Spacer(Modifier.size(26.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = Color.White,
                strokeWidth = 3.dp
            )
        }
    }
}
