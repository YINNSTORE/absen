package com.albasroh.absensi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.navigation.compose.rememberNavController
import com.albasroh.absensi.ui.navigation.AppVM
import com.albasroh.absensi.ui.navigation.Nav
import com.albasroh.absensi.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        val app = application as AbsensiApplication
        val vm = ViewModelProvider(this, AppVM.Factory(app))[AppVM::class.java]

        // Splash native tetap tampil sampai session + database awal benar-benar siap.
        splash.setKeepOnScreenCondition { !vm.sessionReady.value }

        setContent {
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
