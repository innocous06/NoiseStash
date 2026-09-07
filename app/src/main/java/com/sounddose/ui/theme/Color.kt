package com.sounddose.ui.theme

import androidx.compose.ui.graphics.Color

// Pure Minimalist Consumer Palette: Dual Theme
// ==========================================
// DARK THEME PALETTE (Charcoal Black & Sleek Silver)
// ==========================================
val DarkCharcoalBg = Color(0xFF07090E)         // Deep Pitch Charcoal Black Canvas
val DarkCharcoalBgElevated = Color(0xFF0F131D) // Slightly elevated charcoal
val DarkVisualizerSilver = Color(0xFFCBD5E1)   // Sleek Silver for visualizer & active pills
val DarkVisualizerSilverGlow = Color(0xFF94A3B8)// Silver mid-tone
val DarkLineArtSilver = Color(0xFF64748B)      // Sleek silver-slate for generative lines
val DarkSmudgeCard = Color(0x12FFFFFF)         // 7% translucent glass
val DarkSmudgeCardElevated = Color(0x22FFFFFF) // 13% white glass
val DarkSmudgeBorder = Color(0x1FFFFFFF)       // 12% delicate edge
val DarkSmudgeBorderSubtle = Color(0x0EFFFFFF) // 5% subtle edge
val DarkTextPrimary = Color(0xFFFFFFFF)        // Pure crisp white
val DarkTextSecondary = Color(0xFF94A3B8)      // Cool slate
val DarkTextTertiary = Color(0xFF475569)       // Muted slate

// Active and inactive pill styling for Dark Mode (matching reference image)
val DarkPillActiveBg = Color(0xFFCBD5E1)       // Solid sleek silver capsule
val DarkPillActiveText = Color(0xFF090D16)     // Deep charcoal contrast text
val DarkPillInactiveBorder = Color(0x40475569) // 1dp subtle border
val DarkPillInactiveText = Color(0xFF94A3B8)   // Muted slate text

// ==========================================
// LIGHT THEME PALETTE (Light Yellow Amber & Vibrant Orange)
// ==========================================
val LightAmberBg = Color(0xFFFEF9E7)           // Warm Light Yellow Amber Canvas
val LightAmberBgElevated = Color(0xFFFFF4D2)   // Soft elevated amber
val LightVisualizerOrange = Color(0xFFF97316)  // Vibrant Orange for sound visualizer
val LightVisualizerAmber = Color(0xFFD97706)   // Warm Deep Amber
val LightLineArtOrange = Color(0xFFFB923C)     // Luminous orange for generative lines
val LightSmudgeCard = Color(0xB8FFFFFF)        // Translucent frosted white over amber
val LightSmudgeCardElevated = Color(0xEBFFFFFF)// Elevated frosted white
val LightSmudgeBorder = Color(0x40E2D4B7)      // Soft warm amber-sand border
val LightSmudgeBorderSubtle = Color(0x25E2D4B7)// Very faint border
val LightTextPrimary = Color(0xFF1C1917)       // Deep warm charcoal text
val LightTextSecondary = Color(0xFF78716C)     // Warm stone slate
val LightTextTertiary = Color(0xFFA8A29E)      // Muted stone

// Active and inactive pill styling for Light Mode (matching reference image)
val LightPillActiveBg = Color(0xFF1C1917)      // Solid deep charcoal capsule
val LightPillActiveText = Color(0xFFFFFBEB)    // Crisp amber-white text
val LightPillInactiveBorder = Color(0x4078716C)// 1dp subtle border
val LightPillInactiveText = Color(0xFF78716C)  // Warm slate text

// ==========================================
// SEMANTIC DOSE HEALTH LEVELS (Consistent Audiology Standards)
// ==========================================
val DoseSafe = Color(0xFF10B981)               // Serene Emerald (<50%)
val DoseCaution = Color(0xFFF59E0B)            // Warm Amber (50-79%)
val DoseWarning = Color(0xFFF97316)            // Vibrant Orange (80-99%)
val DoseDanger = Color(0xFFEF4444)             // Laser Crimson (>=100%)

// Legacy compatibility aliases
val OledBlack = DarkCharcoalBg
val DeepCanvas = DarkCharcoalBg
val SmudgeCard = DarkSmudgeCard
val SmudgeCardElevated = DarkSmudgeCardElevated
val SmudgeBorder = DarkSmudgeBorder
val SmudgeBorderSubtle = DarkSmudgeBorderSubtle
val TextPrimary = DarkTextPrimary
val TextSecondary = DarkTextSecondary
val TextTertiary = DarkTextTertiary
val BgMidnight = DarkCharcoalBg
val BgDark = DarkCharcoalBg
val SurfaceGlass = DarkSmudgeCard
val SurfaceGlassElevated = DarkSmudgeCardElevated
val GlassBorder = DarkSmudgeBorder
val GlassBorderSubtle = DarkSmudgeBorderSubtle
val CardDark = DarkSmudgeCard
val CardBorder = DarkSmudgeBorder
val NpuGold = Color(0xFFFBBF24)
val NpuGlow = Color(0x33FBBF24)

fun getDoseColor(percent: Double): Color {
    return when {
        percent < 50.0 -> DoseSafe
        percent < 80.0 -> DoseCaution
        percent < 100.0 -> DoseWarning
        else -> DoseDanger
    }
}
