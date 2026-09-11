package com.albasroh.absensi.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.CircularProgressIndicator
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.albasroh.absensi.AbsensiApplication
import com.albasroh.absensi.ui.admin.AdminAttendance
import com.albasroh.absensi.ui.admin.Classes
import com.albasroh.absensi.ui.admin.Reports
import com.albasroh.absensi.ui.admin.Settings
import com.albasroh.absensi.ui.admin.Students
import com.albasroh.absensi.ui.admin.Teachers
import com.albasroh.absensi.ui.auth.Login
import com.albasroh.absensi.ui.teacher.History
import com.albasroh.absensi.ui.teacher.Profile
import com.albasroh.absensi.ui.teacher.Scanner
import com.albasroh.absensi.ui.teacher.Session
import com.albasroh.absensi.ui.teacher.TeacherStudents
import com.albasroh.absensi.ui.teacher.Today
import com.albasroh.absensi.ui.modern.ModernAdminHome
import com.albasroh.absensi.ui.modern.ModernTeacherHome
import com.albasroh.absensi.ui.modern.ModernStudentHome
import com.albasroh.absensi.ui.modern.LeaveRequests
import com.albasroh.absensi.ui.modern.Chatroom
import com.albasroh.absensi.ui.modern.ModernReports

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
    NavHost(
        navController = nav,
        startDestination = R.SPLASH
    ) {
        composable(R.SPLASH) {
            LaunchedEffect(vm.sessionReady.value) {
                if (!vm.sessionReady.value) return@LaunchedEffect

                val destination = when (vm.user.value?.role) {
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

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
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
        composable(R.QR) { com.albasroh.absensi.ui.student.StudentQR(app, vm, nav) }
        composable(R.STATS) { com.albasroh.absensi.ui.student.StudentStats(app, vm, nav) }
        composable(R.PROFILE) { Profile(vm, nav) }
        composable(R.LEAVE) { LeaveRequests(app, vm, nav) }
        composable(R.CHAT) { Chatroom(app, vm, nav) }
    }
}
