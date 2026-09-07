package com.sounddose.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.sounddose.MainActivity
import com.sounddose.data.AppDatabase
import com.sounddose.data.DoseEntry
import com.sounddose.data.UserManager
import com.sounddose.dose.DoseEngine
import com.sounddose.dose.NoiseCategory
import com.sounddose.dose.SafetyStatus
import com.sounddose.util.HapticManager
import com.sounddose.util.LocationHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

data class LiveState(
    val currentDb: Double = 36.0,
    val peakDb: Double = 36.0,
    val sessionAvgDb: Double = 36.0,
    val totalDosePercent: Double = 0.0,
    val safeTimeRemainingHours: Double = 8.0,
    val budgetMultiplier: Float = 1.0f,
    val earRecoveryHours: Int = 0,
    val currentCategory: NoiseCategory = NoiseCategory.AMBIENT,
    val safetyStatus: SafetyStatus = SafetyStatus.SAFE,
    val isListening: Boolean = false,
    val rawLabel: String = "Quiet Ambient",
    val confidence: Float = 0.92f,
    val locationTag: String = "Monitoring Active",
    val activePreset: AudioPreset = AudioPreset.REAL_MIC,
    val calibrationOffset: Double = 0.0,
    val waveformPoints: FloatArray = FloatArray(24) { 0.04f }
)

class AudioCaptureService : Service() {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val dbCalculator = DbCalculator()
    private lateinit var audioClassifier: AudioClassifier
    private lateinit var hapticManager: HapticManager
    private lateinit var locationHelper: LocationHelper
    private lateinit var database: AppDatabase
    private lateinit var userManager: UserManager

    private val _liveState = MutableStateFlow(LiveState())
    val liveState: StateFlow<LiveState> = _liveState.asStateFlow()

    private var cumulativeDose = 0.0
    private var lastHapticMilestone = 0
    private var captureJob: Job? = null
    private var currentLocationTag = "Urban Transit Corridor"

    private var totalDbSum = 0.0
    private var dbSampleCount = 0

    inner class LocalBinder : Binder() {
        fun getService(): AudioCaptureService = this@AudioCaptureService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        userManager = UserManager.getInstance(applicationContext)
        audioClassifier = AudioClassifier(applicationContext)
        audioClassifier.initialize()
        hapticManager = HapticManager(applicationContext)
        locationHelper = LocationHelper(applicationContext)
        database = AppDatabase.getInstance(applicationContext)
        createNotificationChannel()

        val currentMult = userManager.getBudgetMultiplier()
        _liveState.value = _liveState.value.copy(budgetMultiplier = currentMult)

        serviceScope.launch {
            val today = LocalDate.now().toString()
            val existing = database.getTotalDoseForDate(today) ?: 0.0
            cumulativeDose = existing
            val status = DoseEngine.getSafetyStatus(cumulativeDose)
            val recoveryHours = DoseEngine.computeEarRestRecoveryHours(cumulativeDose)
            val safeRemaining = DoseEngine.computeSafeTimeRemainingHours(36.0, cumulativeDose, currentMult)
            _liveState.value = _liveState.value.copy(
                totalDosePercent = cumulativeDose,
                safetyStatus = status,
                earRecoveryHours = recoveryHours,
                safeTimeRemainingHours = safeRemaining,
                budgetMultiplier = currentMult
            )
        }
    }

    fun updateBudgetMultiplier(multiplier: Float) {
        val safeRemaining = DoseEngine.computeSafeTimeRemainingHours(
            currentDb = _liveState.value.currentDb,
            currentDosePercent = cumulativeDose,
            budgetMultiplier = multiplier
        )
        _liveState.value = _liveState.value.copy(
            budgetMultiplier = multiplier,
            safeTimeRemainingHours = safeRemaining
        )
    }

    fun setAudioPreset(preset: AudioPreset) {
        dbCalculator.activePreset = preset
        _liveState.value = _liveState.value.copy(activePreset = preset)
    }

    fun adjustCalibration(delta: Double) {
        val newOffset = (dbCalculator.calibrationOffset + delta).coerceIn(-12.0, 12.0)
        dbCalculator.calibrationOffset = newOffset
        _liveState.value = _liveState.value.copy(calibrationOffset = newOffset)
    }

    fun startListening() {
        if (_liveState.value.isListening) return

        val notification = buildNotification(36.0, cumulativeDose, SafetyStatus.SAFE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                ServiceCompat.startForeground(
                    this,
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                )
            } catch (e: Exception) {
                startForeground(NOTIFICATION_ID, notification)
            }
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        _liveState.value = _liveState.value.copy(isListening = true)

        serviceScope.launch {
            while (isActive && _liveState.value.isListening) {
                currentLocationTag = locationHelper.getApproximateLocationTag()
                delay(30000)
            }
        }

        captureJob = serviceScope.launch {
            var lastDbSaveTime = System.currentTimeMillis()
            var currentCategory = _liveState.value.currentCategory
            var rawLabel = _liveState.value.rawLabel
            var confidence = _liveState.value.confidence

            dbCalculator.startCapture().collect { reading ->
                val db = reading.dbSpl

                totalDbSum += db
                dbSampleCount++
                val avgDb = if (dbSampleCount > 0) totalDbSum / dbSampleCount else db

                // 1-second YAMNet audio classification
                if (reading.classifierFrame != null) {
                    val preset = dbCalculator.activePreset
                    if (preset == AudioPreset.REAL_MIC) {
                        val classification = audioClassifier.classify(reading.classifierFrame)
                        currentCategory = classification.category
                        rawLabel = classification.rawLabel
                        confidence = classification.confidence
                    } else {
                        currentCategory = when (preset) {
                            AudioPreset.URBAN_TRAFFIC -> NoiseCategory.TRAFFIC
                            AudioPreset.HORN_BLAST -> NoiseCategory.HORN
                            AudioPreset.CONSTRUCTION -> NoiseCategory.CONSTRUCTION
                            AudioPreset.QUIET_ROOM -> NoiseCategory.AMBIENT
                            else -> NoiseCategory.AMBIENT
                        }
                        rawLabel = preset.simulatedLabel
                        confidence = 0.94f
                    }
                }

                // NIOSH dose accumulation for 100ms interval
                val increment = DoseEngine.doseIncrement(db, 0.1)
                cumulativeDose += increment

                val status = DoseEngine.getSafetyStatus(cumulativeDose)
                val currentMult = _liveState.value.budgetMultiplier
                // Use session-smoothed exposure to ensure budget ticks down smoothly without bouncing
                val exposureDb = if (avgDb > 35.0) (db * 0.20 + avgDb * 0.80) else db
                val safeRemaining = DoseEngine.computeSafeTimeRemainingHours(exposureDb, cumulativeDose, currentMult)
                val recoveryHours = DoseEngine.computeEarRestRecoveryHours(cumulativeDose)
                checkThresholdHaptics(cumulativeDose)

                _liveState.value = _liveState.value.copy(
                    currentDb = db,
                    peakDb = reading.peakDb,
                    sessionAvgDb = avgDb,
                    totalDosePercent = cumulativeDose,
                    safeTimeRemainingHours = safeRemaining,
                    budgetMultiplier = currentMult,
                    earRecoveryHours = recoveryHours,
                    currentCategory = currentCategory,
                    safetyStatus = status,
                    isListening = true,
                    rawLabel = rawLabel,
                    confidence = confidence,
                    locationTag = currentLocationTag,
                    waveformPoints = reading.waveformSamples
                )

                val now = System.currentTimeMillis()
                if (increment > 0.005 || (now - lastDbSaveTime) >= 2000) {
                    lastDbSaveTime = now
                    database.insertEntry(
                        DoseEntry(
                            timestamp = now,
                            date = LocalDate.now().toString(),
                            dbLevel = db,
                            doseIncrement = increment,
                            category = currentCategory.name,
                            rawLabel = rawLabel,
                            confidence = confidence,
                            locationTag = currentLocationTag
                        )
                    )
                    updateNotification(db, cumulativeDose, status, currentCategory)
                }
            }
        }
    }

    fun stopListening() {
        captureJob?.cancel()
        captureJob = null
        dbCalculator.stop()
        _liveState.value = _liveState.value.copy(
            isListening = false,
            waveformPoints = FloatArray(24) { 0.02f }
        )
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun checkThresholdHaptics(dose: Double) {
        val currentMilestone = when {
            dose >= 100.0 -> 100
            dose >= 80.0 -> 80
            dose >= 50.0 -> 50
            else -> 0
        }
        if (currentMilestone > lastHapticMilestone && currentMilestone > 0) {
            lastHapticMilestone = currentMilestone
            when (currentMilestone) {
                50 -> hapticManager.mediumBuzz()
                80 -> hapticManager.strongBuzz()
                100 -> hapticManager.alarmPattern()
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "SoundDose Hearing Monitor",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Active cumulative noise dose tracking"
        }
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    private fun buildNotification(
        db: Double,
        dose: Double,
        status: SafetyStatus,
        category: NoiseCategory = NoiseCategory.AMBIENT
    ): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SoundDose • ${status.title}")
            .setContentText("${db.toInt()} dB SPL • Dose: ${"%.1f".format(dose)}% • ${category.label}")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(
        db: Double,
        dose: Double,
        status: SafetyStatus,
        category: NoiseCategory
    ) {
        val nm = getSystemService(NotificationManager::class.java)
        nm?.notify(NOTIFICATION_ID, buildNotification(db, dose, status, category))
    }

    override fun onDestroy() {
        stopListening()
        serviceScope.cancel()
        audioClassifier.close()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "sounddose_active_channel"
        const val NOTIFICATION_ID = 2026
    }
}
