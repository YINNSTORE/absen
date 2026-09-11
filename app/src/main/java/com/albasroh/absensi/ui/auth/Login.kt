@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.albasroh.absensi.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.albasroh.absensi.R as AppR
import com.albasroh.absensi.ui.components.Field
import com.albasroh.absensi.ui.navigation.AppVM
import com.albasroh.absensi.ui.navigation.R

@Composable
fun Login(vm: AppVM, nav: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D47B8), Color(0xFF1769E0), Color(0xFFF4F8FF))
                )
            )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                Modifier
                    .size(108.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = .96f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(AppR.drawable.logo_mts_al_basroh),
                    contentDescription = "Logo MTs-Al Basroh",
                    modifier = Modifier.size(92.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(Modifier.size(14.dp))
            Text("MTs-Al Basroh", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Absensi Digital", color = Color.White.copy(alpha = .9f))
            Spacer(Modifier.size(22.dp))

            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .98f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Selamat Datang", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Masuk ke akun sekolah Anda", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Field(username, { username = it; error = null }, "NIS / Username")
                    Field(password, { password = it; error = null }, "Password", secret = true)
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Button(
                        onClick = {
                            busy = true
                            vm.login(username.trim(), password) { result ->
                                busy = false
                                result.onFailure { error = it.message ?: "Username atau password salah" }
                                result.onSuccess { user ->
                                    nav.navigate(when (user.role) {
                                        "ADMIN" -> R.ADMIN
                                        "GURU" -> R.TEACHER
                                        else -> R.STUDENT
                                    }) { popUpTo(R.LOGIN) { inclusive = true } }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !busy && username.isNotBlank() && password.isNotBlank(),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        if (busy) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                        else {
                            androidx.compose.material3.Icon(Icons.Default.Lock, null, Modifier.size(18.dp))
                            Spacer(Modifier.size(8.dp))
                            Text("Masuk")
                        }
                    }
                }
            }
            Spacer(Modifier.size(14.dp))
            Text("Sistem Informasi Absensi MTs-Al Basroh", color = Color.White.copy(alpha = .78f), style = MaterialTheme.typography.bodySmall)
        }
    }
}
