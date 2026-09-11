package com.albasroh.absensi.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.albasroh.absensi.AbsensiApplication
import com.albasroh.absensi.data.local.entity.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppVM(
    private val app: AbsensiApplication
) : ViewModel() {
    val session: StateFlow<Pair<Long?, String?>> = app.prefs.session.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null to null
    )

    val theme: StateFlow<String> = app.prefs.themeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = "SYSTEM"
    )

    val user = MutableStateFlow<User?>(null)

    init {
        viewModelScope.launch {
            session.collect { (id, _) ->
                user.value = id?.let { app.db.users().byId(it) }
            }
        }
    }

    fun login(
        username: String,
        password: String,
        done: (Result<User>) -> Unit
    ) {
        viewModelScope.launch {
            val result = app.auth.login(username, password)
            result.onSuccess { loggedInUser ->
                app.prefs.login(loggedInUser.id, loggedInUser.role)
            }
            done(result)
        }
    }

    fun logout() {
        viewModelScope.launch {
            app.prefs.logout()
            user.value = null
        }
    }

    fun setTheme(value: String) {
        viewModelScope.launch {
            app.prefs.theme(value)
        }
    }

    class Factory(
        private val app: AbsensiApplication
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppVM::class.java)) {
                return AppVM(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
