package com.sounddose.dose

import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

enum class SafetyStatus(val title: String, val subtitle: String) {
    SAFE("Safe Listening", "Within daily recommended limits"),
    CAUTION("Elevated Ambient", "Prolonged exposure accelerates dose"),
    WARNING("Approaching Limit", "Consider noise reduction or earplugs"),
    DANGER("Limit Exceeded", "Acoustic trauma risk — rest ears now")
}

object DoseEngine {

    /**
     * Standard smartphone MEMS mic calibration reference.
     * Maps raw digital PCM RMS to true ambient physical dB SPL.
     */
    const val DEFAULT_CALIBRATION_OFFSET = 100.0

    /**
     * Convert RMS amplitude (0.0 to 1.0) to approximate dB SPL.
     * Incorporates user calibration trimmer offset.
     */
    fun rmsToDbSpl(rms: Double, userOffset: Double = 0.0): Double {
        if (rms <= 0.00001) return 35.0 // Base noise floor
        val dbFs = 20.0 * log10(rms)
        val spl = dbFs + DEFAULT_CALIBRATION_OFFSET + userOffset
        return spl.coerceIn(32.0, 125.0)
    }

    /**
     * NIOSH clinical standard:
     * Allowed exposure time halves every 3 dB increase above 85 dB.
     * T_allowed = 8 / 2^((L - 85) / 3) hours
     */
    fun allowedExposureHours(dbSpl: Double): Double {
        if (dbSpl < 85.0) return Double.MAX_VALUE
        return 8.0 / 2.0.pow((dbSpl - 85.0) / 3.0)
    }

    /**
     * Calculates dose increment accumulated in a given interval (in seconds).
     * 100% represents the full daily safe limit.
     */
    fun doseIncrement(dbSpl: Double, durationSeconds: Double): Double {
        val allowedHours = allowedExposureHours(dbSpl)
        if (allowedHours == Double.MAX_VALUE) return 0.0
        val durationHours = durationSeconds / 3600.0
        return (durationHours / allowedHours) * 100.0
    }

    fun computeRms(samples: FloatArray): Double {
        if (samples.isEmpty()) return 0.0
        var sumSquares = 0.0
        for (sample in samples) {
            sumSquares += sample * sample
        }
        return sqrt(sumSquares / samples.size)
    }

    fun getSafetyStatus(totalDosePercent: Double): SafetyStatus {
        return when {
            totalDosePercent < 50.0 -> SafetyStatus.SAFE
            totalDosePercent < 80.0 -> SafetyStatus.CAUTION
            totalDosePercent < 100.0 -> SafetyStatus.WARNING
            else -> SafetyStatus.DANGER
        }
    }

    /**
     * Consumer Byproduct: Estimated safe listening hours left at the current sound level.
     * Accurately accounts for user's Daily Safe Budget Ceiling Multiplier.
     * Returns remaining safe hours based on remaining daily budget and ambient exposure rate.
     */
    fun computeSafeTimeRemainingHours(
        currentDb: Double,
        currentDosePercent: Double,
        budgetMultiplier: Float = 1.0f
    ): Double {
        val totalBudgetPercent = 100.0 * budgetMultiplier.coerceIn(0.7f, 1.3f)
        if (currentDosePercent >= totalBudgetPercent) return 0.0

        val remainingBudgetPercent = (totalBudgetPercent - currentDosePercent).coerceAtLeast(0.0)
        val remainingDoseFraction = remainingBudgetPercent / 100.0 // Fraction of nominal 8h standard

        // Standard baseline allowance (8 hours at reference safe listening <=85 dB)
        val effectiveDb = currentDb.coerceIn(35.0, 115.0)
        val burnRate = if (effectiveDb <= 85.0) {
            1.0 // Standard nominal daily budget burn rate
        } else {
            // NIOSH 3 dB exchange rate above 85 dB
            2.0.pow((effectiveDb - 85.0) / 3.0)
        }

        val safeHours = (8.0 * remainingDoseFraction) / burnRate
        return safeHours.coerceAtLeast(0.0)
    }

    /**
     * Consumer Byproduct: Hours of quiet rest (<65 dB) required for inner-ear stereocilia recovery.
     */
    fun computeEarRestRecoveryHours(totalDosePercent: Double): Int {
        return when {
            totalDosePercent < 30.0 -> 0
            totalDosePercent < 60.0 -> 4
            totalDosePercent < 100.0 -> 8
            totalDosePercent < 150.0 -> 14
            else -> 24
        }
    }
}
