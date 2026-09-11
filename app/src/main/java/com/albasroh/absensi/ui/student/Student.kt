@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.albasroh.absensi.ui.student

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.albasroh.absensi.AbsensiApplication
import com.albasroh.absensi.data.local.entity.Student
import com.albasroh.absensi.ui.components.MenuBlock
import com.albasroh.absensi.ui.components.ScaffoldBack
import com.albasroh.absensi.ui.components.Stat
import com.albasroh.absensi.ui.navigation.AppVM
import com.albasroh.absensi.ui.navigation.R
import com.albasroh.absensi.util.Qr
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

@Composable
private fun rememberStudent(
    app: AbsensiApplication,
    userId: Long?
): Student? {
    return produceState<Student?>(initialValue = null, userId) {
        value = userId?.let { app.db.students().byUser(it) }
    }.value
}

@Composable
fun StudentDash(
    app: AbsensiApplication,
    vm: AppVM,
    nav: NavHostController
) {
    if (vm.user.value?.role != "MURID") return

    val student = rememberStudent(app, vm.user.value?.id)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Murid") },
                actions = {
                    IconButton(onClick = { nav.navigate(R.PROFILE) }) {
                        Icon(Icons.Default.Person, contentDescription = "Profil")
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
                    "Halo, ${student?.nama ?: vm.user.value?.nama}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item {
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "PROFIL SAYA",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("Nama: ${student?.nama ?: "-"}")
                        Text("NIS: ${student?.nis ?: "-"}")
                        Text("Kelas: ${student?.kelas ?: "-"}")
                    }
                }
            }

            item {
                MenuBlock(
                    "QR ABSENSI",
                    "Tampilkan QR besar",
                    { Icon(Icons.Default.QrCode, contentDescription = null) }
                ) { nav.navigate(R.QR) }
            }

            item {
                MenuBlock(
                    "RIWAYAT ABSENSI",
                    "Daftar kehadiran",
                    { Icon(Icons.Default.History, contentDescription = null) }
                ) { nav.navigate(R.HISTORY) }
            }

            item {
                MenuBlock(
                    "STATISTIK KEHADIRAN",
                    "Hadir, izin, sakit, alpa",
                    { Icon(Icons.Default.BarChart, contentDescription = null) }
                ) { nav.navigate(R.STATS) }
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
fun StudentQR(
    app: AbsensiApplication,
    vm: AppVM,
    nav: NavHostController
) {
    val student = rememberStudent(app, vm.user.value?.id)

    ScaffoldBack("QR Absensi", { nav.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                student?.nama.orEmpty(),
                style = MaterialTheme.typography.headlineSmall
            )
            Text("${student?.nis ?: "-"} • ${student?.kelas ?: "-"}")
            Spacer(Modifier.height(18.dp))

            student?.let {
                QR(
                    content = Qr.payload(it.qrToken),
                    modifier = Modifier.size(290.dp)
                )
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Tunjukkan QR ini kepada guru.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = {}) {
                Text("Perbesar QR")
            }
        }
    }
}

@Composable
fun QR(
    content: String,
    modifier: Modifier
) {
    val bitmap = remember(content) {
        val matrix = MultiFormatWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            700,
            700
        )
        val result = Bitmap.createBitmap(
            700,
            700,
            Bitmap.Config.ARGB_8888
        )

        for (x in 0 until 700) {
            for (y in 0 until 700) {
                result.setPixel(
                    x,
                    y,
                    if (matrix[x, y]) {
                        android.graphics.Color.BLACK
                    } else {
                        android.graphics.Color.WHITE
                    }
                )
            }
        }
        result
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "QR Absensi",
        modifier = modifier
    )
}

@Composable
fun StudentStats(
    app: AbsensiApplication,
    vm: AppVM,
    nav: NavHostController
) {
    val student = rememberStudent(app, vm.user.value?.id)
    val list by app.db
        .attendance()
        .byStudent(student?.id ?: -1L)
        .collectAsState(emptyList())

    val hadir = list.count { it.status == "HADIR" }
    val izin = list.count { it.status == "IZIN" }
    val sakit = list.count { it.status == "SAKIT" }
    val alpa = list.count { it.status == "ALPA" }

    ScaffoldBack("Statistik Kehadiran", { nav.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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

            Text(
                "Persentase hadir: ${
                    if (list.isEmpty()) 0 else hadir * 100 / list.size
                }%"
            )
        }
    }
}
