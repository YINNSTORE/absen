@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.albasroh.absensi.ui.modern

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sick
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.albasroh.absensi.AbsensiApplication
import com.albasroh.absensi.R as AppR
import com.albasroh.absensi.data.local.entity.ChatMessage
import com.albasroh.absensi.data.local.entity.LeaveRequest
import com.albasroh.absensi.data.local.entity.Student
import com.albasroh.absensi.ui.components.Field
import com.albasroh.absensi.ui.components.ScaffoldBack
import com.albasroh.absensi.ui.navigation.AppVM
import com.albasroh.absensi.ui.navigation.R
import com.albasroh.absensi.util.Clock
import com.albasroh.absensi.util.Hashing
import com.albasroh.absensi.util.ReportExporter
import com.albasroh.absensi.util.Tokens
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Blue = Color(0xFF1769E0)
private val BlueDark = Color(0xFF0B49B7)
private val Page = Color(0xFFF5F9FF)

private fun logout(vm: AppVM, nav: NavHostController) {
    vm.logout()
    nav.navigate(R.LOGIN) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
private fun AppTopBar(
    title: String,
    subtitle: String,
    onNotification: () -> Unit,
    onLogout: () -> Unit
) {
    Surface(color = Blue) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(15.dp),
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(AppR.drawable.logo_mts_al_basroh),
                        contentDescription = "Logo",
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            Spacer(Modifier.size(11.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.White.copy(.76f), style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onNotification) {
                Icon(Icons.Default.NotificationsNone, "Notifikasi", tint = Color.White)
            }
            IconButton(onClick = onLogout) {
                Icon(Icons.Default.Logout, "Logout", tint = Color.White)
            }
        }
    }
}

@Composable
private fun HeaderCard(name: String, role: String, subtitle: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Blue, BlueDark)))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text("MTs-Al Basroh", color = Color.White.copy(.8f), style = MaterialTheme.typography.labelLarge)
            Text("Halo, $name 👋", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text(role, color = Color.White, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(.84f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(icon, null, Modifier.padding(7.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MenuTile(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(15.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                }
            }
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SectionTitle(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
        if (action != null && onAction != null) {
            androidx.compose.material3.TextButton(onClick = onAction) { Text(action) }
        }
    }
}

@Composable
private fun BottomNav(
    current: String,
    nav: NavHostController,
    items: List<Triple<String, String, androidx.compose.ui.graphics.vector.ImageVector>>
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = current == route,
                onClick = {
                    nav.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(icon, label) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun ModernAdminHome(app: AbsensiApplication, vm: AppVM, nav: NavHostController) {
    val user by vm.user.collectAsState()
    val students by app.db.students().observeActive().collectAsState(emptyList())
    val teachers by app.db.teachers().observeAll().collectAsState(emptyList())
    val attendance by app.db.attendance().observeAll().collectAsState(emptyList())
    val requests by app.db.leaveRequests().observeAll().collectAsState(emptyList())
    val today = Clock.date()

    Scaffold(
        containerColor = Page,
        topBar = {
            AppTopBar(
                "Halo, ${user?.nama ?: "Admin"}",
                "MTs-Al Basroh • Administrator",
                onNotification = { nav.navigate(R.LEAVE) },
                onLogout = { logout(vm, nav) }
            )
        },
        bottomBar = {
            BottomNav(R.ADMIN, nav, listOf(
                Triple(R.ADMIN, "Beranda", Icons.Default.Home),
                Triple(R.ATTENDANCE, "Absensi", Icons.Default.Assignment),
                Triple(R.CHAT, "Chat", Icons.Default.Chat),
                Triple(R.SETTINGS, "Lainnya", Icons.Default.Settings)
            ))
        }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { HeaderCard(user?.nama ?: "Administrator", "ADMINISTRATOR", "Kelola absensi sekolah dengan cepat dan rapi") }
            item {
                SectionTitle("Ringkasan Hari Ini")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Hadir", attendance.count { it.tanggal == today && it.status == "HADIR" }.toString(), Icons.Default.CheckCircle, Modifier.weight(1f))
                    StatCard("Sakit", attendance.count { it.tanggal == today && it.status == "SAKIT" }.toString(), Icons.Default.Sick, Modifier.weight(1f))
                    StatCard("Izin", attendance.count { it.tanggal == today && it.status == "IZIN" }.toString(), Icons.Default.EventNote, Modifier.weight(1f))
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Murid", students.size.toString(), Icons.Default.People, Modifier.weight(1f))
                    StatCard("Guru", teachers.size.toString(), Icons.Default.Person, Modifier.weight(1f))
                    StatCard("Pengajuan", requests.count { it.status == "MENUNGGU" }.toString(), Icons.Default.EventNote, Modifier.weight(1f))
                }
            }
            item { SectionTitle("Menu Utama") }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    MenuTile("Data Murid", Icons.Default.People, { nav.navigate(R.STUDENTS) }, Modifier.weight(1f))
                    MenuTile("Data Guru", Icons.Default.Person, { nav.navigate(R.TEACHERS) }, Modifier.weight(1f))
                    MenuTile("Data Kelas", Icons.Default.Groups, { nav.navigate(R.CLASSES) }, Modifier.weight(1f))
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    MenuTile("Absensi", Icons.Default.Assignment, { nav.navigate(R.ATTENDANCE) }, Modifier.weight(1f))
                    MenuTile("Laporan", Icons.Default.BarChart, { nav.navigate(R.REPORTS) }, Modifier.weight(1f))
                    MenuTile("Pengajuan", Icons.Default.EventNote, { nav.navigate(R.LEAVE) }, Modifier.weight(1f))
                }
            }
            item {
                Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Chat, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.size(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Chatroom Sekolah", fontWeight = FontWeight.Bold)
                            Text("Komunikasi dengan guru dan murid", style = MaterialTheme.typography.bodySmall)
                        }
                        androidx.compose.material3.TextButton(onClick = { nav.navigate(R.CHAT) }) { Text("Buka") }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernTeacherHome(app: AbsensiApplication, vm: AppVM, nav: NavHostController) {
    val user by vm.user.collectAsState()
    val attendance by app.db.attendance().observeAll().collectAsState(emptyList())
    val students by app.db.students().observeActive().collectAsState(emptyList())
    val today = Clock.date()

    Scaffold(
        containerColor = Page,
        topBar = {
            AppTopBar("Halo, ${user?.nama ?: "Guru"}", "MTs-Al Basroh • Guru", { nav.navigate(R.LEAVE) }, { logout(vm, nav) })
        },
        bottomBar = {
            BottomNav(R.TEACHER, nav, listOf(
                Triple(R.TEACHER, "Beranda", Icons.Default.Home),
                Triple(R.SCANNER, "Absensi", Icons.Default.QrCodeScanner),
                Triple(R.CHAT, "Chat", Icons.Default.Chat),
                Triple(R.STUDENTS_T, "Lainnya", Icons.Default.People)
            ))
        }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { HeaderCard(user?.nama ?: "Guru", "GURU", "Pantau kehadiran dan kelas hari ini") }
            item { SectionTitle("Ringkasan Hari Ini") }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Hadir", attendance.count { it.tanggal == today && it.status == "HADIR" }.toString(), Icons.Default.CheckCircle, Modifier.weight(1f))
                    StatCard("Izin", attendance.count { it.tanggal == today && it.status == "IZIN" }.toString(), Icons.Default.EventNote, Modifier.weight(1f))
                    StatCard("Sakit", attendance.count { it.tanggal == today && it.status == "SAKIT" }.toString(), Icons.Default.Sick, Modifier.weight(1f))
                }
            }
            item { SectionTitle("Menu Utama") }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    MenuTile("Scan QR", Icons.Default.QrCodeScanner, { nav.navigate(R.SCANNER) }, Modifier.weight(1f))
                    MenuTile("Sesi", Icons.Default.Schedule, { nav.navigate(R.SESSION) }, Modifier.weight(1f))
                    MenuTile("Murid", Icons.Default.People, { nav.navigate(R.STUDENTS_T) }, Modifier.weight(1f))
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    MenuTile("Hari Ini", Icons.Default.Assignment, { nav.navigate(R.TODAY) }, Modifier.weight(1f))
                    MenuTile("Pengajuan", Icons.Default.EventNote, { nav.navigate(R.LEAVE) }, Modifier.weight(1f))
                    MenuTile("Laporan", Icons.Default.BarChart, { nav.navigate(R.REPORTS) }, Modifier.weight(1f))
                }
            }
            item {
                Card(shape = RoundedCornerShape(22.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("Total murid aktif", style = MaterialTheme.typography.bodySmall)
                        Text("${students.size} murid", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Gunakan Scan QR untuk mencatat kehadiran dengan cepat.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun ModernStudentHome(app: AbsensiApplication, vm: AppVM, nav: NavHostController) {
    val user by vm.user.collectAsState()
    val student by produceState<Student?>(null, user?.id) { value = user?.id?.let { app.db.students().byUser(it) } }
    val attendance by app.db.attendance().byStudent(student?.id ?: -1L).collectAsState(emptyList())
    val today = Clock.date()
    val todayStatus = attendance.firstOrNull { it.tanggal == today }?.status ?: "BELUM ABSEN"

    Scaffold(
        containerColor = Page,
        topBar = {
            AppTopBar("Halo, ${student?.nama ?: user?.nama.orEmpty()} 👋", "Kelas ${student?.kelas ?: "-"}", { nav.navigate(R.LEAVE) }, { logout(vm, nav) })
        },
        bottomBar = {
            BottomNav(R.STUDENT, nav, listOf(
                Triple(R.STUDENT, "Beranda", Icons.Default.Home),
                Triple(R.QR, "Absensi", Icons.Default.QrCode),
                Triple(R.CHAT, "Chat", Icons.Default.Chat),
                Triple(R.PROFILE, "Profil", Icons.Default.Person)
            ))
        }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { HeaderCard(student?.nama ?: user?.nama.orEmpty(), "MURID • ${student?.kelas ?: "-"}", "Semangat belajar dan jaga kehadiran hari ini") }
            item {
                Card(shape = RoundedCornerShape(23.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (todayStatus == "HADIR") Icons.Default.CheckCircle else Icons.Default.EventNote, null, Modifier.size(42.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.size(13.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Status Hari Ini", style = MaterialTheme.typography.bodySmall)
                            Text(todayStatus, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                            Text(if (todayStatus == "HADIR") "Terima kasih sudah disiplin!" else "Jangan lupa melakukan absensi.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item { SectionTitle("Ringkasan Kehadiran") }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Hadir", attendance.count { it.status == "HADIR" }.toString(), Icons.Default.CheckCircle, Modifier.weight(1f))
                    StatCard("Izin", attendance.count { it.status == "IZIN" }.toString(), Icons.Default.EventNote, Modifier.weight(1f))
                    StatCard("Sakit", attendance.count { it.status == "SAKIT" }.toString(), Icons.Default.Sick, Modifier.weight(1f))
                }
            }
            item { SectionTitle("Menu Utama") }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    MenuTile("QR Saya", Icons.Default.QrCode, { nav.navigate(R.QR) }, Modifier.weight(1f))
                    MenuTile("Riwayat", Icons.Default.Assignment, { nav.navigate(R.HISTORY) }, Modifier.weight(1f))
                    MenuTile("Izin & Sakit", Icons.Default.EventNote, { nav.navigate(R.LEAVE) }, Modifier.weight(1f))
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    MenuTile("Statistik", Icons.Default.BarChart, { nav.navigate(R.STATS) }, Modifier.weight(1f))
                    MenuTile("Chatroom", Icons.Default.Chat, { nav.navigate(R.CHAT) }, Modifier.weight(1f))
                    MenuTile("Profil", Icons.Default.Person, { nav.navigate(R.PROFILE) }, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun LeaveRequests(app: AbsensiApplication, vm: AppVM, nav: NavHostController) {
    val user by vm.user.collectAsState()
    val isStudent = user?.role == "MURID"
    val student by produceState<Student?>(null, user?.id) { value = user?.id?.let { app.db.students().byUser(it) } }
    val requests by if (isStudent) {
        app.db.leaveRequests().byStudent(student?.id ?: -1L).collectAsState(emptyList())
    } else {
        app.db.leaveRequests().observeAll().collectAsState(emptyList())
    }
    var type by remember { mutableStateOf("SAKIT") }
    var start by remember { mutableStateOf(Clock.date()) }
    var end by remember { mutableStateOf(Clock.date()) }
    var reason by remember { mutableStateOf("") }
    var attachment by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    ScaffoldBack(if (isStudent) "Pengajuan Izin & Sakit" else "Review Pengajuan", { nav.popBackStack() }) { padding ->
        LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
            if (isStudent) {
                item { SectionTitle("Buat Pengajuan") }
                item { Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) { listOf("SAKIT", "IZIN", "KEPERLUAN").forEach { value -> FilterChip(type == value, { type = value }, label = { Text(value) }) } } }
                item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { Field(start, { start = it }, "Mulai", modifier = Modifier.weight(1f)); Field(end, { end = it }, "Sampai", modifier = Modifier.weight(1f)) } }
                item { Field(reason, { reason = it }, "Alasan", minLines = 3) }
                item { Field(attachment, { attachment = it }, "Lampiran (opsional)") }
                item { Button(onClick = { if (student != null && reason.isNotBlank()) scope.launch { app.db.leaveRequests().insert(LeaveRequest(studentId = student!!.id, type = type, startDate = start, endDate = end, reason = reason, attachmentName = attachment.ifBlank { null })); reason = ""; attachment = "" } }, modifier = Modifier.fillMaxWidth(), enabled = student != null && reason.isNotBlank(), shape = RoundedCornerShape(20.dp)) { Text("Kirim Pengajuan") } }
                item { SectionTitle("Riwayat Pengajuan") }
                items(requests) { RequestCard(it) }
            } else {
                item { SectionTitle("Pengajuan Terbaru") }
                items(requests) { ReviewCard(app, it) }
            }
        }
    }
}

@Composable
private fun RequestCard(request: LeaveRequest) {
    Card(shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(request.type, fontWeight = FontWeight.Bold)
                Text(request.status, color = if (request.status == "DITOLAK") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Text("${request.startDate} → ${request.endDate}")
            Text(request.reason, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (request.reviewerNote.isNotBlank()) Text("Catatan: ${request.reviewerNote}")
        }
    }
}

@Composable
private fun ReviewCard(app: AbsensiApplication, request: LeaveRequest) {
    val student = produceState<Student?>(null, request.studentId) { value = app.db.students().byId(request.studentId) }.value
    var note by remember(request.id) { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Card(shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(student?.nama ?: "Murid", fontWeight = FontWeight.Bold)
            Text("${request.type} • ${request.startDate} → ${request.endDate}")
            Text(request.reason)
            if (request.status == "MENUNGGU") {
                Field(note, { note = it }, "Catatan reviewer (opsional)")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { scope.launch { app.db.leaveRequests().review(request.id, "DISETUJUI", note) } }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(18.dp)) { Text("Setujui") }
                    OutlinedButton(onClick = { scope.launch { app.db.leaveRequests().review(request.id, "DITOLAK", note) } }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(18.dp)) { Text("Tolak") }
                }
            } else Text("Status: ${request.status}", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun Chatroom(app: AbsensiApplication, vm: AppVM, nav: NavHostController) {
    val user by vm.user.collectAsState()
    val current = user ?: return
    val room = when (current.role) { "MURID" -> "KELAS-${current.kelas ?: "VII-A"}"; "GURU" -> "KELAS-VII-A"; else -> "RUANG-SEKOLAH" }
    val messages by app.db.chat().observeRoom(room).collectAsState(emptyList())
    var text by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Page,
        topBar = { TopAppBar(title = { Column { Text("Chatroom", fontWeight = FontWeight.Bold); Text(room, style = MaterialTheme.typography.labelSmall) } }, navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.Default.Chat, "Kembali") } }) },
        bottomBar = {
            Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Field(text, { text = it }, "Tulis pesan…", modifier = Modifier.weight(1f))
                Spacer(Modifier.size(8.dp))
                Button(onClick = { if (text.isNotBlank()) scope.launch { app.db.chat().insert(ChatMessage(roomName = room, senderId = current.id, senderName = current.nama, senderRole = current.role, message = text.trim())); text = "" } }, enabled = text.isNotBlank(), shape = RoundedCornerShape(18.dp)) { Text("Kirim") }
            }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
            items(messages) { msg -> MessageBubble(msg, msg.senderId == current.id) }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage, mine: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start) {
        Column(Modifier.fillMaxWidth(.82f).clip(RoundedCornerShape(18.dp)).background(if (mine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface).padding(12.dp)) {
            if (!mine) Text(message.senderName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            Text(message.message)
            Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.sentAt)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.End))
        }
    }
}

@Composable
fun ModernReports(app: AbsensiApplication, vm: AppVM, nav: NavHostController) {
    val context = LocalContext.current
    val attendance by app.db.attendance().observeAll().collectAsState(emptyList())
    val students by app.db.students().observeAll().collectAsState(emptyList())
    var message by remember { mutableStateOf("") }
    val date = Clock.date()

    ScaffoldBack("Laporan & Export", { nav.popBackStack() }) { padding ->
        LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
            item { SectionTitle("Laporan Absensi") }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Hadir", attendance.count { it.tanggal == date && it.status == "HADIR" }.toString(), Icons.Default.CheckCircle, Modifier.weight(1f))
                    StatCard("Izin", attendance.count { it.tanggal == date && it.status == "IZIN" }.toString(), Icons.Default.EventNote, Modifier.weight(1f))
                    StatCard("Sakit", attendance.count { it.tanggal == date && it.status == "SAKIT" }.toString(), Icons.Default.Sick, Modifier.weight(1f))
                }
            }
            item {
                Card(shape = RoundedCornerShape(22.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Filter Periode", fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            FilterChip(true, {}, label = { Text("Harian") })
                            FilterChip(false, {}, label = { Text("Mingguan") })
                            FilterChip(false, {}, label = { Text("Bulanan") })
                            FilterChip(false, {}, label = { Text("Semester") })
                        }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { message = runCatching { ReportExporter.exportExcel(context, attendance, students) }.getOrElse { "Gagal: ${it.message}" } }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(18.dp)) { Text("Export Excel") }
                    Button(onClick = { message = runCatching { ReportExporter.exportPdf(context, attendance, students) }.getOrElse { "Gagal: ${it.message}" } }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(18.dp)) { Text("Export PDF") }
                }
            }
            if (message.isNotBlank()) item { Card(shape = RoundedCornerShape(18.dp)) { Text(message, Modifier.padding(16.dp)) } }
            item { Text("${attendance.size} catatan kehadiran • ${students.size} murid", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}
