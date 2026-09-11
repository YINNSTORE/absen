@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.albasroh.absensi.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.albasroh.absensi.AbsensiApplication
import com.albasroh.absensi.data.local.entity.ClassRoom
import com.albasroh.absensi.data.local.entity.Student
import com.albasroh.absensi.data.local.entity.Teacher
import com.albasroh.absensi.data.local.entity.User
import com.albasroh.absensi.ui.components.Badge
import com.albasroh.absensi.ui.components.Field
import com.albasroh.absensi.ui.components.MenuBlock
import com.albasroh.absensi.ui.components.ScaffoldBack
import com.albasroh.absensi.ui.components.Stat
import com.albasroh.absensi.ui.navigation.AppVM
import com.albasroh.absensi.ui.navigation.R
import com.albasroh.absensi.util.Clock
import com.albasroh.absensi.util.Hashing
import com.albasroh.absensi.util.Tokens
import kotlinx.coroutines.launch

@Composable
fun Admin(
    vm: AppVM,
    nav: NavHostController
) {
    if (vm.user.value?.role != "ADMIN") return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Absensi MTs-Al Basroh") },
                actions = {
                    IconButton(onClick = { nav.navigate(R.SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Pengaturan")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Selamat datang, ${vm.user.value?.nama}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            item {
                MenuBlock(
                    "DATA MURID",
                    "Kelola seluruh data murid",
                    { Icon(Icons.Default.People, contentDescription = null) }
                ) { nav.navigate(R.STUDENTS) }
            }
            item {
                MenuBlock(
                    "DATA GURU",
                    "Kelola seluruh data guru",
                    { Icon(Icons.Default.School, contentDescription = null) }
                ) { nav.navigate(R.TEACHERS) }
            }
            item {
                MenuBlock(
                    "DATA KELAS",
                    "Kelola kelas",
                    { Icon(Icons.Default.HomeWork, contentDescription = null) }
                ) { nav.navigate(R.CLASSES) }
            }
            item {
                MenuBlock(
                    "DATA ABSENSI",
                    "Lihat seluruh absensi",
                    { Icon(Icons.Default.Assignment, contentDescription = null) }
                ) { nav.navigate(R.ATTENDANCE) }
            }
            item {
                MenuBlock(
                    "LAPORAN",
                    "Statistik kehadiran",
                    { Icon(Icons.Default.BarChart, contentDescription = null) }
                ) { nav.navigate(R.REPORTS) }
            }
            item {
                MenuBlock(
                    "PENGATURAN",
                    "Tema dan logout",
                    { Icon(Icons.Default.Settings, contentDescription = null) }
                ) { nav.navigate(R.SETTINGS) }
            }
        }
    }
}

@Composable
fun Students(
    app: AbsensiApplication,
    vm: AppVM,
    nav: NavHostController
) {
    val list by app.db.students().observeAll().collectAsState(emptyList())
    var query by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var nis by remember { mutableStateOf("") }
    var kelas by remember { mutableStateOf("VII-A") }
    val scope = rememberCoroutineScope()

    ScaffoldBack("Data Murid", { nav.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Field(query, { query = it }, "Search murid")
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Tambah Murid")
            }
            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(
                    list.filter {
                        it.nama.contains(query, ignoreCase = true) ||
                            it.nis.contains(query, ignoreCase = true) ||
                            it.kelas.contains(query, ignoreCase = true)
                    }
                ) { student ->
                    Card {
                        Column(Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(student.nama)
                                    Text("${student.nis} • ${student.kelas}")
                                }
                                Text(if (student.isActive) "AKTIF" else "NONAKTIF")
                            }
                            Spacer(Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        app.db.students().setActive(
                                            student.id,
                                            !student.isActive
                                        )
                                    }
                                }
                            ) {
                                Text(if (student.isActive) "Nonaktifkan" else "Aktifkan")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Tambah Murid") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Field(name, { name = it }, "Nama")
                    Field(nis, { nis = it }, "NIS")
                    Field(kelas, { kelas = it }, "Kelas")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && nis.isNotBlank()) {
                            scope.launch {
                                val userId = app.db.users().insert(
                                    User(
                                        username = "murid$nis",
                                        passwordHash = Hashing.sha256("murid123"),
                                        nama = name,
                                        role = "MURID",
                                        nis = nis,
                                        kelas = kelas
                                    )
                                )
                                app.db.students().insert(
                                    Student(
                                        userId = userId,
                                        nis = nis,
                                        nama = name,
                                        kelas = kelas,
                                        qrToken = Tokens.new()
                                    )
                                )
                                name = ""
                                nis = ""
                                kelas = "VII-A"
                                showDialog = false
                            }
                        }
                    }
                ) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun Teachers(
    app: AbsensiApplication,
    vm: AppVM,
    nav: NavHostController
) {
    val list by app.db.teachers().observeAll().collectAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var nip by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    ScaffoldBack("Data Guru", { nav.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) { Text("+ Tambah Guru") }
            Spacer(Modifier.height(10.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(list) { teacher ->
                    Card {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                teacher.nama,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("NIP ${teacher.nip} • ${teacher.mataPelajaran}")
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Tambah Guru") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Field(name, { name = it }, "Nama")
                    Field(nip, { nip = it }, "NIP")
                    Field(subject, { subject = it }, "Mata pelajaran")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && nip.isNotBlank()) {
                            scope.launch {
                                val userId = app.db.users().insert(
                                    User(
                                        username = "guru$nip",
                                        passwordHash = Hashing.sha256("guru123"),
                                        nama = name,
                                        role = "GURU",
                                        nip = nip
                                    )
                                )
                                app.db.teachers().insert(
                                    Teacher(
                                        userId = userId,
                                        nip = nip,
                                        nama = name,
                                        mataPelajaran = subject
                                    )
                                )
                                name = ""
                                nip = ""
                                subject = ""
                                showDialog = false
                            }
                        }
                    }
                ) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun Classes(
    app: AbsensiApplication,
    vm: AppVM,
    nav: NavHostController
) {
    val list by app.db.classes().observeAll().collectAsState(emptyList())
    var name by remember { mutableStateOf("") }
    var wali by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    ScaffoldBack("Data Kelas", { nav.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Field(name, { name = it }, "Nama kelas")
            Field(wali, { wali = it }, "Wali kelas")
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        scope.launch {
                            app.db.classes().insert(
                                ClassRoom(
                                    namaKelas = name,
                                    waliKelas = wali
                                )
                            )
                            name = ""
                            wali = ""
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Tambah Kelas") }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(list) { classroom ->
                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(classroom.namaKelas)
                                Text(classroom.waliKelas)
                            }
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        app.db.classes().delete(classroom)
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAttendance(
    app: AbsensiApplication,
    vm: AppVM,
    nav: NavHostController
) {
    val list by app.db.attendance().observeAll().collectAsState(emptyList())

    ScaffoldBack("Data Absensi", { nav.popBackStack() }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(list) { attendance ->
                Card {
                    Column(Modifier.padding(14.dp)) {
                        Text("Murid #${attendance.studentId}")
                        Text("${attendance.tanggal} • ${attendance.waktu}")
                        Badge(attendance.status)
                    }
                }
            }
        }
    }
}

@Composable
fun Reports(
    app: AbsensiApplication,
    vm: AppVM,
    nav: NavHostController
) {
    val date = Clock.date()
    val hadir by app.db.attendance().countPresent(date).collectAsState(0)
    val izin by app.db.attendance().countIzin(date).collectAsState(0)
    val sakit by app.db.attendance().countSakit(date).collectAsState(0)
    val alpa by app.db.attendance().countAlpa(date).collectAsState(0)
    val activeStudents by app.db.students().observeActive().collectAsState(emptyList())
    val total = activeStudents.size

    ScaffoldBack("Laporan", { nav.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Statistik $date",
                style = MaterialTheme.typography.headlineSmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Stat("Hadir", hadir)
                Stat("Izin", izin)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Stat("Sakit", sakit)
                Stat("Alpa", alpa)
            }
            Text("Total murid aktif: $total")
            LinearProgressIndicator(
                progress = if (total == 0) 0f else (hadir.toFloat() / total).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "Persentase hadir: ${if (total == 0) 0 else hadir * 100 / total}%"
            )
        }
    }
}

@Composable
fun Settings(
    vm: AppVM,
    nav: NavHostController
) {
    ScaffoldBack("Pengaturan", { nav.popBackStack() }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
        ) {
            item {
                Text(
                    "Tampilan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }

            item {
                Text(
                    "Pilih tema aplikasi",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "SYSTEM" to "Sistem",
                        "LIGHT" to "Terang",
                        "DARK" to "Gelap"
                    ).forEach { (value, label) ->
                        Button(
                            onClick = { vm.setTheme(value) },
                            modifier = Modifier.weight(1f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
                        ) {
                            Text(label)
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Card(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Akun",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Spacer(Modifier.height(5.dp))
                        Text("${vm.user.value?.nama ?: "-"}")
                        Text(
                            "${vm.user.value?.role ?: "-"}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        vm.logout()
                        nav.navigate(R.LOGIN) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                ) {
                    Text("Logout")
                }
            }
        }
    }
}
