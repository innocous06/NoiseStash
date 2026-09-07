package com.sounddose.dose

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DoseEngineTest {

    @Test
    fun testBelow85DbYieldsZeroDose() {
        val allowedHours = DoseEngine.allowedExposureHours(80.0)
        assertEquals(Double.MAX_VALUE, allowedHours, 0.001)

        val increment = DoseEngine.doseIncrement(80.0, 3600.0)
        assertEquals(0.0, increment, 0.001)
    }

    @Test
    fun test85DbNioshStandardLimit() {
        val allowedHours = DoseEngine.allowedExposureHours(85.0)
        assertEquals(8.0, allowedHours, 0.001)

        // 8 hours at 85 dB should equal 100% dose
        val eightHoursDose = DoseEngine.doseIncrement(85.0, 8.0 * 3600.0)
        assertEquals(100.0, eightHoursDose, 0.01)

        // 1 hour at 85 dB should equal 12.5% dose
        val oneHourDose = DoseEngine.doseIncrement(85.0, 3600.0)
        assertEquals(12.5, oneHourDose, 0.01)
    }

    @Test
    fun testExchangeRate3dBHalvesTime() {
        // 88 dB -> 4 hours allowed
        val hoursAt88 = DoseEngine.allowedExposureHours(88.0)
        assertEquals(4.0, hoursAt88, 0.01)

        // 91 dB -> 2 hours allowed
        val hoursAt91 = DoseEngine.allowedExposureHours(91.0)
        assertEquals(2.0, hoursAt91, 0.01)

        // 94 dB -> 1 hour allowed
        val hoursAt94 = DoseEngine.allowedExposureHours(94.0)
        assertEquals(1.0, hoursAt94, 0.01)

        // 1 hour at 94 dB = 100% dose
        val oneHourAt94 = DoseEngine.doseIncrement(94.0, 3600.0)
        assertEquals(100.0, oneHourAt94, 0.01)
    }

    @Test
    fun testHighNoiseLevels() {
        // 100 dB -> 0.25 hours (15 minutes)
        val hoursAt100 = DoseEngine.allowedExposureHours(100.0)
        assertEquals(0.25, hoursAt100, 0.01)

        // 15 minutes at 100 dB should yield ~100% dose
        val fifteenMinDose = DoseEngine.doseIncrement(100.0, 900.0)
        assertEquals(100.0, fifteenMinDose, 0.5)
    }

    @Test
    fun testSafetyStatusTransitions() {
        assertEquals(SafetyStatus.SAFE, DoseEngine.getSafetyStatus(0.0))
        assertEquals(SafetyStatus.SAFE, DoseEngine.getSafetyStatus(49.9))
        assertEquals(SafetyStatus.CAUTION, DoseEngine.getSafetyStatus(50.0))
        assertEquals(SafetyStatus.CAUTION, DoseEngine.getSafetyStatus(79.9))
        assertEquals(SafetyStatus.WARNING, DoseEngine.getSafetyStatus(80.0))
        assertEquals(SafetyStatus.WARNING, DoseEngine.getSafetyStatus(99.9))
        assertEquals(SafetyStatus.DANGER, DoseEngine.getSafetyStatus(100.0))
        assertEquals(SafetyStatus.DANGER, DoseEngine.getSafetyStatus(145.0))
    }

    @Test
    fun testRmsCalculation() {
        val samples = floatArrayOf(0.5f, -0.5f, 0.5f, -0.5f)
        val rms = DoseEngine.computeRms(samples)
        assertEquals(0.5, rms, 0.001)

        val emptySamples = floatArrayOf()
        assertEquals(0.0, DoseEngine.computeRms(emptySamples), 0.001)
    }

    @Test
    fun testSafeTimeRemainingHours() {
        // Below 85 dB with 20% dose (80% remaining budget) -> 6.4 hours remaining
        val safeBelow85 = DoseEngine.computeSafeTimeRemainingHours(75.0, 20.0)
        assertEquals(6.4, safeBelow85, 0.01)

        // At 85 dB with 0% dose -> 8.0 hours remaining
        val fullBudget85 = DoseEngine.computeSafeTimeRemainingHours(85.0, 0.0)
        assertEquals(8.0, fullBudget85, 0.01)

        // At 85 dB with 50% dose -> 4.0 hours remaining
        val halfBudget85 = DoseEngine.computeSafeTimeRemainingHours(85.0, 50.0)
        assertEquals(4.0, halfBudget85, 0.01)

        // With budget multiplier of 1.25 -> 10.0 hours remaining at 0% dose
        val customBudget = DoseEngine.computeSafeTimeRemainingHours(85.0, 0.0, 1.25f)
        assertEquals(10.0, customBudget, 0.01)

        // At 100% dose -> 0.0 hours remaining
        val zeroBudget = DoseEngine.computeSafeTimeRemainingHours(90.0, 100.0)
        assertEquals(0.0, zeroBudget, 0.001)
    }

    @Test
    fun testEarRestRecoveryHours() {
        assertEquals(0, DoseEngine.computeEarRestRecoveryHours(20.0))
        assertEquals(4, DoseEngine.computeEarRestRecoveryHours(50.0))
        assertEquals(8, DoseEngine.computeEarRestRecoveryHours(85.0))
        assertEquals(14, DoseEngine.computeEarRestRecoveryHours(120.0))
        assertEquals(24, DoseEngine.computeEarRestRecoveryHours(160.0))
    }
}
