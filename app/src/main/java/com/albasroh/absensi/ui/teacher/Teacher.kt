@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.albasroh.absensi.ui.teacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.albasroh.absensi.AbsensiApplication
import com.albasroh.absensi.data.local.entity.AttendanceSession
import com.albasroh.absensi.data.local.entity.ClassRoom
import com.albasroh.absensi.data.local.entity.Student
import com.albasroh.absensi.data.local.entity.Teacher
import com.albasroh.absensi.data.local.entity.User
import com.albasroh.absensi.scanner.QrScannerView
import com.albasroh.absensi.ui.components.Badge
import com.albasroh.absensi.ui.components.Field
import com.albasroh.absensi.ui.components.MenuBlock
import com.albasroh.absensi.ui.components.ScaffoldBack
import com.albasroh.absensi.ui.navigation.AppVM
import com.albasroh.absensi.ui.navigation.R
import com.albasroh.absensi.util.Clock
import com.albasroh.absensi.util.Hashing
import com.albasroh.absensi.util.Qr
import com.albasroh.absensi.util.Tokens
import kotlinx.coroutines.launch


@Composable
private fun rememberTeacher(
    app: AbsensiApplication,
    userId: Long?
): Teacher? {
    return produceState<Teacher?>(
        initialValue = null,
        userId
    ) {
        value = userId?.let {
            app.db.teachers().byUser(it)
        }
    }.value
}


/* =========================================================
   DASHBOARD GURU
   ========================================================= */

@Composable
fun TeacherDash(
    app: AbsensiApplication,
    vm: AppVM,
    nav: NavHostController
) {
    if (vm.user.value?.role != "GURU") return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Dashboard Guru")
                },
                actions = {
                    IconButton(
                        onClick = {
                            nav.navigate(R.PROFILE)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil"
                        )
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
                    text = "Selamat datang, ${vm.user.value?.nama.orEmpty()}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item {
                MenuBlock(
                    "SCAN QR",
                    "Scan QR murid untuk absensi",
                    {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = null
                        )
                    }
                ) {
                    nav.navigate(R.SCANNER)
                }
            }

            item {
                MenuBlock(
                    "SESI ABSENSI",
                    "Pilih kelas dan mulai sesi",
                    {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null
                        )
                    }
                ) {
                    nav.navigate(R.SESSION)
                }
            }

            item {
                MenuBlock(
                    "ABSENSI HARI INI",
                    "Lihat absensi hari ini",
                    {
                        Icon(
                            Icons.Default.Assignment,
                            contentDescription = null
                        )
                    }
                ) {
                    nav.navigate(R.TODAY)
                }
            }

            item {
                MenuBlock(
                    "DAFTAR MURID",
                    "Cari murid",
                    {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null
                        )
                    }
                ) {
                    nav.navigate(R.STUDENTS_T)
                }
            }

            item {
                MenuBlock(
                    "REKAP ABSENSI",
                    "Riwayat kehadiran",
                    {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = null
                        )
                    }
                ) {
                    nav.navigate(R.HISTORY)
                }
            }

            item {
                MenuBlock(
                    "PROFIL",
                    "Profil guru",
                    {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null
                        )
                    }
                ) {
                    nav.navigate(R.PROFILE)
                }
            }
        }
    }
}


/* =========================================================
   BUAT SESI ABSENSI
   ========================================================= */

@Composable
fun Session(
    app: AbsensiApplication,
    vm: AppVM,
    nav: NavHostController
) {
    val teacher = rememberTeacher(
        app,
        vm.user.value?.id
    )

    val classes by app.db
        .classes()
        .observeAll()
        .collectAsState(emptyList())

    var selected by remember {
        mutableStateOf<ClassRoom?>(null)
    }

    var subject by remember {
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(teacher?.id) {
        subject = teacher?.mataPelajaran.orEmpty()
    }

    ScaffoldBack(
        "Buat Sesi Absensi",
        {
            nav.popBackStack()
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Tanggal: ${Clock.date()}"
            )

            Text(
                text = "Pilih kelas",
                style = MaterialTheme.typography.titleMedium
            )

            classes.forEach { classroom ->

                FilterChip(
                    selected = selected?.id == classroom.id,
                    onClick = {
                        selected = classroom
                    },
                    label = {
                        Text(classroom.namaKelas)
                    }
                )
            }

            Field(
                value = subject,
                onChange = {
                    subject = it
                },
                label = "Mata pelajaran"
            )

            Button(
                onClick = {

                    val currentTeacher = teacher
                    val currentClass = selected

                    if (
                        currentTeacher != null &&
                        currentClass != null
                    ) {
                        scope.launch {

                            app.db.sessions().insert(
                                AttendanceSession(
                                    teacherId = currentTeacher.id,
                                    classId = currentClass.id,
                                    tanggal = Clock.date(),
                                    startTime = Clock.time()
                                )
                            )

                            nav.navigate(R.SCANNER)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = teacher != null && selected != null
            ) {
                Text("Mulai Absensi")
            }
        }
    }
}


/* =========================================================
   SCANNER QR
   ========================================================= */

@Composable
fun Scanner(
    app: AbsensiApplication,
    vm: AppVM,
    nav: NavHostController
) {
    val teacher = rememberTeacher(
        app,
        vm.user.value?.id
    )

    var rawValue by remember {
        mutableStateOf("")
    }

    var message by remember {
        mutableStateOf(
            "Arahkan kamera ke QR murid"
        )
    }

    var lastValue by remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        QrScannerView(
            onToken = { value ->

                if (value != lastValue) {
                    lastValue = value
                    rawValue = value
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {

            Card {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Scanner QR",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(message)
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Button(
                onClick = {
                    nav.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Selesai")
            }
        }
    }

    if (
        teacher != null &&
        rawValue.isNotBlank()
    ) {

        ScanEffect(
            app = app,
            teacher = teacher,
            rawValue = rawValue,
            setMessage = {
                message = it
            }
        )
    }
}


/* =========================================================
   PROSES HASIL SCAN
   ========================================================= */

@Composable
private fun ScanEffect(
    app: AbsensiApplication,
    teacher: Teacher,
    rawValue: String,
    setMessage: (String) -> Unit
) {

    LaunchedEffect(rawValue) {

        val token = Qr.token(rawValue)

        if (token == null) {

            setMessage(
                "QR Code tidak valid."
            )

            return@LaunchedEffect
        }

        val student =
            app.db.students().byToken(token)

        if (student == null) {

            setMessage(
                "Murid tidak ditemukan atau tidak aktif."
            )

            return@LaunchedEffect
        }

        val session =
            app.db.sessions()
                .active(
                    teacher.id,
                    Clock.date()
                )

        if (session == null) {

            setMessage(
                "Sesi absensi belum dibuat."
            )

            return@LaunchedEffect
        }

        val classroom =
            app.db.classes()
                .byId(session.classId)

        if (
            classroom?.namaKelas != student.kelas
        ) {

            setMessage(
                "Murid tidak sesuai dengan kelas sesi."
            )

            return@LaunchedEffect
        }

        val result =
            app.attendanceRepo.mark(
                student,
                teacher,
                session
            )

        setMessage(
            result.fold(
                onSuccess = {

                    "✓ Absensi Berhasil: " +
                            "${student.nama} • ${Clock.time()}"
                },
                onFailure = { throwable ->

                    throwable.message
                        ?: "Terjadi kesalahan"
                }
            )
        )
    }
}


/* =========================================================
   ABSENSI HARI INI
   ========================================================= */

@Composable
fun Today(
    app: AbsensiApplication,
    vm: AppVM,
    nav: NavHostController
) {

    val list by app.db
        .attendance()
        .observeAll()
        .collectAsState(emptyList())

    ScaffoldBack(
        "Absensi Hari Ini",
        {
            nav.popBackStack()
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(
                list.filter {
                    it.tanggal == Clock.date()
                }
            ) { attendance ->

                Card {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                "Murid #${attendance.studentId}"
                            )

                            Text(
                                attendance.waktu
                            )
                        }

                        Badge(
                            attendance.status
                        )
                    }
                }
            }
        }
    }
}


/* =========================================================
   MANAJEMEN MURID
   ========================================================= */

@Composable
fun TeacherStudents(
    app: AbsensiApplication,
    vm: AppVM,
    nav: NavHostController
) {

    val list by app.db
        .students()
        .observeActive()
        .collectAsState(emptyList())

    var query by remember {
        mutableStateOf("")
    }

    var showAdd by remember {
        mutableStateOf(false)
    }

    var name by remember {
        mutableStateOf("")
    }

    var nis by remember {
        mutableStateOf("")
    }

    var kelas by remember {
        mutableStateOf(
            vm.user.value?.kelas ?: "VII-A"
        )
    }

    var username by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var createdCredentials by remember {
        mutableStateOf<Pair<String, String>?>(null)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    val scope = rememberCoroutineScope()

    ScaffoldBack(
        "Manajemen Murid",
        {
            nav.popBackStack()
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = "Data murid",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text =
                    "Kelola murid dan buat akun login " +
                            "tanpa meninggalkan halaman ini",
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Field(
                    value = query,
                    onChange = {
                        query = it
                    },
                    label = "Cari nama / NIS",
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        showAdd = true
                    },
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text("+ Murid")
                }
            }

            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(
                    list.filter {
                        it.nama.contains(
                            query,
                            true
                        ) ||
                                it.nis.contains(
                                    query,
                                    true
                                ) ||
                                it.kelas.contains(
                                    query,
                                    true
                                )
                    }
                ) { student ->

                    Card(
                        shape = RoundedCornerShape(20.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = RoundedCornerShape(14.dp),
                                color =
                                    MaterialTheme.colorScheme
                                        .primaryContainer
                            ) {

                                Box(
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.People,
                                        contentDescription = null,
                                        tint =
                                            MaterialTheme
                                                .colorScheme
                                                .primary
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier.size(10.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = student.nama,
                                    fontWeight =
                                        FontWeight.SemiBold
                                )

                                Text(
                                    text =
                                        "${student.nis} • ${student.kelas}",
                                    style =
                                        MaterialTheme.typography.bodySmall,
                                    color =
                                        MaterialTheme.colorScheme
                                            .onSurfaceVariant
                                )
                            }

                            Badge("AKTIF")
                        }
                    }
                }
            }
        }
    }


    /* =====================================================
       DIALOG TAMBAH MURID
       ===================================================== */

    if (showAdd) {

        AlertDialog(

            onDismissRequest = {
                showAdd = false
            },

            title = {
                Text("Tambah Murid & Akun")
            },

            text = {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Field(
                        value = name,
                        onChange = {
                            name = it
                        },
                        label = "Nama lengkap"
                    )

                    Field(
                        value = nis,
                        onChange = {
                            nis = it
                        },
                        label = "NIS"
                    )

                    Field(
                        value = kelas,
                        onChange = {
                            kelas = it
                        },
                        label = "Kelas"
                    )

                    Field(
                        value = username,
                        onChange = {
                            username = it
                        },
                        label = "Username login (opsional)"
                    )

                    Field(
                        value = password,
                        onChange = {
                            password = it
                        },
                        label = "Password login (opsional)",
                        secret = true
                    )

                    Text(
                        text =
                            "Jika username/password kosong, " +
                                    "sistem membuat kredensial otomatis.",
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )

                    error?.let {
                        Text(
                            text = it,
                            color =
                                MaterialTheme.colorScheme.error
                        )
                    }
                }
            },

            confirmButton = {

                Button(
                    onClick = {

                        error = null

                        if (
                            name.isBlank() ||
                            nis.isBlank() ||
                            kelas.isBlank()
                        ) {

                            error =
                                "Nama, NIS, dan kelas wajib diisi"

                            return@Button
                        }

                        scope.launch {

                            val finalUsername =
                                username.ifBlank {
                                    "murid${nis.trim()}"
                                }

                            val finalPassword =
                                password.ifBlank {
                                    "murid123"
                                }

                            if (
                                app.db.users()
                                    .byUsername(
                                        finalUsername
                                    ) != null
                            ) {

                                error =
                                    "Username sudah digunakan"

                                return@launch
                            }

                            val userId =
                                app.db.users().insert(
                                    User(
                                        username =
                                            finalUsername,
                                        passwordHash =
                                            Hashing.sha256(
                                                finalPassword
                                            ),
                                        nama =
                                            name.trim(),
                                        role = "MURID",
                                        nis =
                                            nis.trim(),
                                        kelas =
                                            kelas.trim()
                                    )
                                )

                            app.db.students().insert(
                                Student(
                                    userId = userId,
                                    nis = nis.trim(),
                                    nama = name.trim(),
                                    kelas = kelas.trim(),
                                    qrToken = Tokens.new()
                                )
                            )

                            createdCredentials =
                                finalUsername to
                                        finalPassword

                            name = ""
                            nis = ""
                            username = ""
                            password = ""

                            showAdd = false
                        }
                    },
                    shape =
                        RoundedCornerShape(22.dp)
                ) {
                    Text("Simpan")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showAdd = false
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }


    /* =====================================================
       DIALOG KREDENSIAL
       ===================================================== */

    createdCredentials?.let { credentials ->

        AlertDialog(

            onDismissRequest = {
                createdCredentials = null
            },

            title = {
                Text(
                    "Akun Murid Berhasil Dibuat"
                )
            },

            text = {

                Text(
                    "Username: ${credentials.first}\n" +
                            "Password: ${credentials.second}\n\n" +
                            "Simpan kredensial ini dan " +
                            "berikan kepada murid."
                )
            },

            confirmButton = {

                Button(
                    onClick = {
                        createdCredentials = null
                    },
                    shape =
                        RoundedCornerShape(22.dp)
                ) {
                    Text("Selesai")
                }
            }
        )
    }
}


/* =========================================================
   RIWAYAT ABSENSI
   ========================================================= */

@Composable
fun History(
    app: AbsensiApplication,
    vm: AppVM,
    nav: NavHostController
) {

    val studentId =
        vm.user.value?.id

    val student =
        produceState<Student?>(
            initialValue = null,
            studentId
        ) {

            value =
                studentId?.let {
                    app.db.students()
                        .byUser(it)
                }
        }.value

    val list by app.db
        .attendance()
        .byStudent(
            student?.id ?: -1L
        )
        .collectAsState(emptyList())

    ScaffoldBack(
        "Riwayat Absensi",
        {
            nav.popBackStack()
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            items(list) { attendance ->

                Card {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Column {

                            Text(
                                attendance.tanggal
                            )

                            Text(
                                attendance.waktu
                            )
                        }

                        Badge(
                            attendance.status
                        )
                    }
                }
            }
        }
    }
}


/* =========================================================
   PROFIL
   ========================================================= */

@Composable
fun Profile(
    vm: AppVM,
    nav: NavHostController
) {

    ScaffoldBack(
        "Profil",
        {
            nav.popBackStack()
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text =
                    vm.user.value?.nama.orEmpty(),
                style =
                    MaterialTheme.typography.headlineSmall
            )

            Text(
                "Username: ${
                    vm.user.value?.username.orEmpty()
                }"
            )

            Text(
                "Role: ${
                    vm.user.value?.role.orEmpty()
                }"
            )

            Text(
                "NIS: ${
                    vm.user.value?.nis ?: "-"
                }"
            )

            Text(
                "NIP: ${
                    vm.user.value?.nip ?: "-"
                }"
            )

            Text(
                "Kelas: ${
                    vm.user.value?.kelas ?: "-"
                }"
            )
        }
    }
}