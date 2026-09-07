package com.sounddose.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.sounddose.dose.NoiseCategory
import com.sounddose.dose.SafetyStatus

object CategoryIconHelper {

    fun getCategoryIcon(category: NoiseCategory): ImageVector {
        return when (category) {
            NoiseCategory.TRAFFIC -> Icons.Outlined.DirectionsCar
            NoiseCategory.CONSTRUCTION -> Icons.Outlined.Construction
            NoiseCategory.HORN -> Icons.Outlined.Campaign
            NoiseCategory.MUSIC -> Icons.Outlined.MusicNote
            NoiseCategory.INDUSTRIAL -> Icons.Outlined.PrecisionManufacturing
            NoiseCategory.AMBIENT -> Icons.Outlined.GraphicEq
        }
    }

    fun getSafetyIcon(status: SafetyStatus): ImageVector {
        return when (status) {
            SafetyStatus.SAFE -> Icons.Outlined.CheckCircle
            SafetyStatus.CAUTION -> Icons.Outlined.WarningAmber
            SafetyStatus.WARNING -> Icons.Outlined.ReportProblem
            SafetyStatus.DANGER -> Icons.Outlined.Dangerous
        }
    }
}
