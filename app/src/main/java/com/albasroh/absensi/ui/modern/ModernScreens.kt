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
import com.albasroh.absensi.util.ReportExporter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Blue = Color(0xFF1769E0)
private val BlueDark = Color(0xFF0D47B8)

private fun logout(vm: AppVM, nav: NavHostController) {
    vm.logout()
    nav.navigate(R.LOGIN) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(AppR.drawable.logo_mts_al_basroh),
                            contentDescription = "Logo MTs-Al Basroh",
                            modifier = Modifier.size(44.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                Spacer(Modifier.size(12.dp))
                Column {
                    Text("MTs-Al Basroh", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(role, color = Color.White.copy(alpha = .78f), style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Halo, $name 👋", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(alpha = .86f), style = MaterialTheme.typography.bodyMedium)
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(icon, null, Modifier.padding(7.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(13.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(icon, null, Modifier.padding(8.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.size(10.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun BottomNav(
    current: String,
    nav: NavHostController,
    items: List<Triple<String, String, androidx.compose.ui.graphics.vector.ImageVector>>
) {
    NavigationBar {
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = current == route,
                onClick = {
                    nav.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(nav.graph.startDestinationId) { saveState = true }
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun ModernAdminHome(app: AbsensiApplication, vm: AppVM, nav: NavHostController) {
    val students by app.db.students().observeActive().collectAsState(emptyList())
    val teachers by app.db.teachers().observeAll().collectAsState(emptyList())
    val attendance by app.db.attendance().observeAll().collectAsState(emptyList())
    val requests by app.db.leaveRequests().observeAll().collectAsState(emptyList())
    val today = Clock.date()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beranda", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton({ nav.navigate(R.SETTINGS) }) { Icon(Icons.Default.Settings, "Pengaturan") }
                    IconButton({ logout(vm, nav) }) { Icon(Icons.Default.Logout, "Logout") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            BottomNav(
                R.ADMIN,
                nav,
                listOf(
                    Triple(R.ADMIN, "Beranda", Icons.Default.Home),
                    Triple(R.ATTENDANCE, "Absensi", Icons.Default.Assignment),
                    Triple(R.CHAT, "Chat", Icons.Default.Chat),
                    Triple(R.SETTINGS, "Lainnya", Icons.Default.Settings)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item { HeaderCard(vm.user.value?.nama ?: "Administrator", "ADMINISTRATOR", "Kelola operasional absensi sekolah dengan cepat") }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    StatCard("Murid Aktif", students.size.toString(), Icons.Default.People, Modifier.weight(1f))
                    StatCard("Guru", teachers.size.toString(), Icons.Default.Person, Modifier.weight(1f))
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    StatCard("Hadir Hari Ini", attendance.count { it.tanggal == today && it.status == "HADIR" }.toString(), Icons.Default.CheckCircle, Modifier.weight(1f))
                    StatCard("Pengajuan", requests.count { it.status == "MENUNGGU" }.toString(), Icons.Default.EventNote, Modifier.weight(1f))
                }
            }
            item { Text("Akses Cepat", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        ActionCard("Data Murid", "Kelola siswa", Icons.Default.People) { nav.navigate(R.STUDENTS) }
                        ActionCard("Data Kelas", "Kelola kelas", Icons.Default.Groups) { nav.navigate(R.CLASSES) }
                        ActionCard("Chatroom", "Komunikasi sekolah", Icons.Default.Chat) { nav.navigate(R.CHAT) }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        ActionCard("Data Guru", "Kelola guru", Icons.Default.Person) { nav.navigate(R.TEACHERS) }
                        ActionCard("Laporan", "Export PDF / Excel", Icons.Default.Description) { nav.navigate(R.REPORTS) }
                        ActionCard("Pengajuan", "Review izin/sakit", Icons.Default.EventNote) { nav.navigate(R.LEAVE) }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernTeacherHome(app: AbsensiApplication, vm: AppVM, nav: NavHostController) {
    val attendance by app.db.attendance().observeAll().collectAsState(emptyList())
    val students by app.db.students().observeActive().collectAsState(emptyList())
    val today = Clock.date()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beranda", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton({ nav.navigate(R.PROFILE) }) { Icon(Icons.Default.Person, "Profil") }
                    IconButton({ logout(vm, nav) }) { Icon(Icons.Default.Logout, "Logout") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            BottomNav(
                R.TEACHER,
                nav,
                listOf(
                    Triple(R.TEACHER, "Beranda", Icons.Default.Home),
                    Triple(R.SCANNER, "Absensi", Icons.Default.QrCodeScanner),
                    Triple(R.CHAT, "Chat", Icons.Default.Chat),
                    Triple(R.STUDENTS_T, "Lainnya", Icons.Default.People)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item { HeaderCard(vm.user.value?.nama ?: "Guru", "GURU", "Pantau kehadiran dan kelas hari ini") }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Murid", students.size.toString(), Icons.Default.People, Modifier.weight(1f))
                    StatCard("Hadir", attendance.count { it.tanggal == today && it.status == "HADIR" }.toString(), Icons.Default.CheckCircle, Modifier.weight(1f))
                    StatCard("Izin/Sakit", attendance.count { it.tanggal == today && (it.status == "IZIN" || it.status == "SAKIT") }.toString(), Icons.Default.EventNote, Modifier.weight(1f))
                }
            }
            item { Text("Menu Utama", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        ActionCard("Scan QR", "Absensi cepat", Icons.Default.QrCodeScanner) { nav.navigate(R.SCANNER) }
                        ActionCard("Sesi Absensi", "Mulai sesi kelas", Icons.Default.Schedule) { nav.navigate(R.SESSION) }
                        ActionCard("Manajemen Murid", "Tambah murid + akun", Icons.Default.People) { nav.navigate(R.STUDENTS_T) }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        ActionCard("Absensi Hari Ini", "Cek status siswa", Icons.Default.Assignment) { nav.navigate(R.TODAY) }
                        ActionCard("Pengajuan", "Review izin/sakit", Icons.Default.EventNote) { nav.navigate(R.LEAVE) }
                        ActionCard("Laporan", "Rekap & export", Icons.Default.BarChart) { nav.navigate(R.REPORTS) }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernStudentHome(app: AbsensiApplication, vm: AppVM, nav: NavHostController) {
    val student by produceState<Student?>(null, vm.user.value?.id) {
        value = vm.user.value?.id?.let { app.db.students().byUser(it) }
    }
    val attendance by app.db.attendance().byStudent(student?.id ?: -1L).collectAsState(emptyList())
    val today = Clock.date()
    val todayStatus = attendance.firstOrNull { it.tanggal == today }?.status ?: "BELUM ABSEN"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beranda", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton({ nav.navigate(R.PROFILE) }) { Icon(Icons.Default.Person, "Profil") }
                    IconButton({ logout(vm, nav) }) { Icon(Icons.Default.Logout, "Logout") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            BottomNav(
                R.STUDENT,
                nav,
                listOf(
                    Triple(R.STUDENT, "Beranda", Icons.Default.Home),
                    Triple(R.QR, "Absensi", Icons.Default.QrCode),
                    Triple(R.CHAT, "Chat", Icons.Default.Chat),
                    Triple(R.PROFILE, "Profil", Icons.Default.Person)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item { HeaderCard(student?.nama ?: vm.user.value?.nama.orEmpty(), "MURID • ${student?.kelas ?: "-"}", "Semangat belajar dan jaga kehadiran hari ini") }
            item {
                Card(shape = RoundedCornerShape(23.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (todayStatus == "HADIR") Icons.Default.CheckCircle else Icons.Default.EventNote, null, Modifier.size(42.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.size(14.dp))
                        Column {
                            Text("Status Hari Ini", style = MaterialTheme.typography.bodySmall)
                            Text(todayStatus, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Hadir", attendance.count { it.status == "HADIR" }.toString(), Icons.Default.CheckCircle, Modifier.weight(1f))
                    StatCard("Izin", attendance.count { it.status == "IZIN" }.toString(), Icons.Default.EventNote, Modifier.weight(1f))
                    StatCard("Sakit", attendance.count { it.status == "SAKIT" }.toString(), Icons.Default.Sick, Modifier.weight(1f))
                }
            }
            item { Text("Menu Utama", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        ActionCard("QR Saya", "Tunjukkan ke guru", Icons.Default.QrCode) { nav.navigate(R.QR) }
                        ActionCard("Pengajuan Izin", "Sakit, izin, keterangan", Icons.Default.EventNote) { nav.navigate(R.LEAVE) }
                        ActionCard("Chatroom", "Grup kelas", Icons.Default.Chat) { nav.navigate(R.CHAT) }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        ActionCard("Riwayat", "Lihat absensi", Icons.Default.Assignment) { nav.navigate(R.HISTORY) }
                        ActionCard("Statistik", "Ringkasan kehadiran", Icons.Default.BarChart) { nav.navigate(R.STATS) }
                        ActionCard("Profil", "Data akun", Icons.Default.Person) { nav.navigate(R.PROFILE) }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaveRequests(app: AbsensiApplication, vm: AppVM, nav: NavHostController) {
    val isStudent = vm.user.value?.role == "MURID"
    val student by produceState<Student?>(null, vm.user.value?.id) {
        value = vm.user.value?.id?.let { app.db.students().byUser(it) }
    }
    val requests = if (isStudent) {
        app.db.leaveRequests().byStudent(student?.id ?: -1L).collectAsState(emptyList()).value
    } else {
        app.db.leaveRequests().observeAll().collectAsState(emptyList()).value
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
                item { Text("Ajukan keterangan ketidakhadiran", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("SAKIT", "IZIN", "KEPERLUAN").forEach { value -> FilterChip(type == value, { type = value }, label = { Text(value) }) } } }
                item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { Field(start, { start = it }, "Mulai", modifier = Modifier.weight(1f)); Field(end, { end = it }, "Sampai", modifier = Modifier.weight(1f)) } }
                item { Field(reason, { reason = it }, "Keterangan / alasan", minLines = 3) }
                item { Field(attachment, { attachment = it }, "Lampiran (nama file, opsional)") }
                item { Button(onClick = { if (student != null && reason.isNotBlank()) scope.launch { app.db.leaveRequests().insert(LeaveRequest(studentId = student!!.id, type = type, startDate = start, endDate = end, reason = reason, attachmentName = attachment.ifBlank { null })); reason = ""; attachment = "" } }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), enabled = student != null && reason.isNotBlank()) { Text("Kirim Pengajuan") } }
                item { Text("Riwayat Pengajuan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                items(requests) { RequestCard(it) }
            } else {
                item { Text("Pengajuan terbaru", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                items(requests) { request -> ReviewCard(app, request) }
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
                Text(request.status, color = when (request.status) { "DISETUJUI" -> Color(0xFF159957); "DITOLAK" -> MaterialTheme.colorScheme.error; else -> MaterialTheme.colorScheme.primary })
            }
            Text("${request.startDate} → ${request.endDate}")
            Text(request.reason, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (request.reviewerNote.isNotBlank()) Text("Catatan: ${request.reviewerNote}")
        }
    }
}

@Composable
private fun ReviewCard(app: AbsensiApplication, request: LeaveRequest) {
    val student = produceState<Student?>(null, request.studentId) {
        value = app.db.students().byId(request.studentId)
    }.value
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
            } else {
                Text("Status: ${request.status}", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun Chatroom(app: AbsensiApplication, vm: AppVM, nav: NavHostController) {
    val user = vm.user.value ?: return
    val room = when (user.role) {
        "MURID" -> "KELAS-${user.kelas ?: "VII-A"}"
        "GURU" -> "KELAS-VII-A"
        else -> "RUANG-SEKOLAH"
    }
    val messages by app.db.chat().observeRoom(room).collectAsState(emptyList())
    var text by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("Chatroom", fontWeight = FontWeight.Bold); Text(room, style = MaterialTheme.typography.labelSmall) } },
                navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.Default.Chat, "Kembali") } }
            )
        },
        bottomBar = {
            Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Field(text, { text = it }, "Tulis pesan…", modifier = Modifier.weight(1f))
                Spacer(Modifier.size(8.dp))
                Button(onClick = { if (text.isNotBlank()) scope.launch { app.db.chat().insert(ChatMessage(roomName = room, senderId = user.id, senderName = user.nama, senderRole = user.role, message = text.trim())); text = "" } }, enabled = text.isNotBlank(), shape = RoundedCornerShape(18.dp)) { Text("Kirim") }
            }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
            items(messages) { msg -> MessageBubble(msg, msg.senderId == user.id) }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage, mine: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start) {
        Column(Modifier.fillMaxWidth(.82f).clip(RoundedCornerShape(18.dp)).background(if (mine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant).padding(12.dp)) {
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

    ScaffoldBack("Laporan & Export", { nav.popBackStack() }) { padding ->
        LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text("Laporan Absensi", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("${attendance.size} catatan kehadiran tersedia")
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = { message = runCatching { ReportExporter.exportExcel(context, attendance, students) }.getOrElse { "Gagal: ${it.message}" } }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(18.dp)) { Text("Export Excel") }
                    Button(onClick = { message = runCatching { ReportExporter.exportPdf(context, attendance, students) }.getOrElse { "Gagal: ${it.message}" } }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(18.dp)) { Text("Export PDF") }
                }
            }
            if (message.isNotBlank()) item { Card(shape = RoundedCornerShape(18.dp)) { Text(message + "\nFile tersimpan di folder Download/Absensi-MTs-Al-Basroh", Modifier.padding(16.dp)) } }
            item { Text("Ringkasan Hari Ini", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            item {
                val date = Clock.date()
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Hadir", attendance.count { it.tanggal == date && it.status == "HADIR" }.toString(), Icons.Default.CheckCircle, Modifier.weight(1f))
                    StatCard("Izin", attendance.count { it.tanggal == date && it.status == "IZIN" }.toString(), Icons.Default.EventNote, Modifier.weight(1f))
                    StatCard("Sakit", attendance.count { it.tanggal == date && it.status == "SAKIT" }.toString(), Icons.Default.Sick, Modifier.weight(1f))
                }
            }
        }
    }
}
