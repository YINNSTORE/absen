package com.albasroh.absensi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.albasroh.absensi.ui.navigation.AppVM
import com.albasroh.absensi.ui.navigation.Nav
import com.albasroh.absensi.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as AbsensiApplication

        setContent {
            val vm: AppVM = viewModel(
                factory = AppVM.Factory(app)
            )
            val nav = rememberNavController()
            val isDark = when (vm.theme.value) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            AppTheme(isDark = isDark) {
                Nav(app = app, vm = vm, nav = nav)
            }
        }
    }
}
