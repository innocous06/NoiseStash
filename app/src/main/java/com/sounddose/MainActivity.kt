package com.sounddose

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.sounddose.audio.AudioCaptureService
import com.sounddose.audio.AudioPreset
import com.sounddose.audio.LiveState
import com.sounddose.data.AppDatabase
import com.sounddose.data.UserManager
import com.sounddose.ui.navigation.SoundDoseNavHost
import com.sounddose.ui.onboarding.OnboardingScreen
import com.sounddose.ui.settings.SettingsSheet
import com.sounddose.ui.splash.LandingSplashScreen
import com.sounddose.ui.theme.SoundDoseTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var audioService: AudioCaptureService? = null
    private var isServiceBound = false
    private var pendingStartRequest = false
    private val liveStateFlow = MutableStateFlow(LiveState())

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as? AudioCaptureService.LocalBinder
            val service = localBinder?.getService()
            audioService = service
            isServiceBound = true

            service?.let { s ->
                lifecycleScope.launch {
                    s.liveState.collect { state ->
                        liveStateFlow.value = state
                    }
                }
                if (pendingStartRequest) {
                    pendingStartRequest = false
                    s.startListening()
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioService = null
            isServiceBound = false
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val micGranted = grants[Manifest.permission.RECORD_AUDIO] == true
        if (micGranted) {
            val service = audioService
            if (service != null) {
                service.startListening()
            } else {
                pendingStartRequest = true
                startAudioService()
            }
        } else {
            Toast.makeText(this, "Microphone permission required for sound monitoring", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val userManager = remember { UserManager.getInstance(applicationContext) }
            var isDarkTheme by remember { mutableStateOf(userManager.isDarkTheme()) }
            var showSplash by remember { mutableStateOf(true) }
            var isOnboarded by remember { mutableStateOf(userManager.getUserProfile().isOnboarded) }
            var showSettings by remember { mutableStateOf(false) }

            SoundDoseTheme(
                isDark = isDarkTheme,
                onToggleTheme = {
                    val nextTheme = !isDarkTheme
                    isDarkTheme = nextTheme
                    userManager.setDarkTheme(nextTheme)
                }
            ) {
                val liveState by liveStateFlow.collectAsStateWithLifecycle()
                val database = remember { AppDatabase.getInstance(applicationContext) }

                val categoryBreakdown by database.categoryBreakdownToday.collectAsStateWithLifecycle()
                val dailySummaries by database.last7DaysSummary.collectAsStateWithLifecycle()

                when {
                    showSplash -> {
                        LandingSplashScreen(
                            onAnimationFinish = {
                                showSplash = false
                            }
                        )
                    }
                    !isOnboarded -> {
                        OnboardingScreen(
                            onComplete = {
                                isOnboarded = true
                            }
                        )
                    }
                    else -> {
                        SoundDoseNavHost(
                            liveState = liveState,
                            categoryBreakdown = categoryBreakdown,
                            dailySummaries = dailySummaries,
                            onStartStop = { toggleMonitoring() },
                            onSelectPreset = { preset -> selectPreset(preset) },
                            onAdjustCalibration = { delta -> audioService?.adjustCalibration(delta) },
                            onOpenSettings = { showSettings = true }
                        )

                        if (showSettings) {
                            SettingsSheet(
                                currentOffset = liveState.calibrationOffset,
                                onAdjustCalibration = { delta -> audioService?.adjustCalibration(delta) },
                                onUpdateBudgetMultiplier = { multiplier -> audioService?.updateBudgetMultiplier(multiplier) },
                                onDismiss = { showSettings = false },
                                onReplaySplash = {
                                    showSettings = false
                                    showSplash = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun toggleMonitoring() {
        val currentService = audioService
        if (currentService != null && liveStateFlow.value.isListening) {
            currentService.stopListening()
        } else {
            if (checkRequiredPermissions()) {
                if (currentService != null) {
                    currentService.startListening()
                } else {
                    pendingStartRequest = true
                    startAudioService()
                }
            } else {
                pendingStartRequest = true
                requestRequiredPermissions()
            }
        }
    }

    private fun selectPreset(preset: AudioPreset) {
        val service = audioService
        if (service != null) {
            service.setAudioPreset(preset)
            if (!liveStateFlow.value.isListening) {
                service.startListening()
            }
        } else {
            pendingStartRequest = true
            startAudioService()
        }
    }

    private fun checkRequiredPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestRequiredPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    private fun startAudioService() {
        val intent = Intent(this, AudioCaptureService::class.java)
        ContextCompat.startForegroundService(this, intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, AudioCaptureService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }
}
