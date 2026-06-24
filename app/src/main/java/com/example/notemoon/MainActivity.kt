package com.example.notemoon

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.example.notemoon.navigation.MainScreen
import com.example.notemoon.settings.domain.ThemeMode
import com.example.notemoon.tasks.reminder.NotificationHelper
import com.example.notemoon.ui.theme.NoteMoonTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single activity host. [AndroidEntryPoint] enables Hilt injection. It also
 * reads the task id delivered by a reminder notification (both on cold start and
 * via [onNewIntent]) so the app can open that task's details, and requests the
 * notification permission on Android 13+.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val deepLinkTaskId = mutableStateOf<Long?>(null)
    private val mainViewModel: MainViewModel by viewModels()

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        deepLinkTaskId.value = extractTaskId(intent)
        requestNotificationPermissionIfNeeded()
        setContent {
            val themeMode by mainViewModel.themeMode.collectAsState()
            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            NoteMoonTheme(darkTheme = darkTheme) {
                MainScreen(
                    deepLinkTaskId = deepLinkTaskId.value,
                    onDeepLinkHandled = { deepLinkTaskId.value = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkTaskId.value = extractTaskId(intent)
    }

    private fun extractTaskId(intent: Intent?): Long? {
        val id = intent?.getLongExtra(NotificationHelper.EXTRA_TASK_ID, -1L) ?: -1L
        return id.takeIf { it > 0L }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
