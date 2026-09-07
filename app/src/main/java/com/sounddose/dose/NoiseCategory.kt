package com.sounddose.dose

enum class NoiseCategory(
    val label: String,
    val colorHex: Long,
    val iconKey: String
) {
    TRAFFIC("Traffic & Transit", 0xFF10B981, "car"),
    CONSTRUCTION("Construction Tools", 0xFFF59E0B, "construction"),
    HORN("Emergency Horn & Siren", 0xFFEF4444, "horn"),
    MUSIC("Loudspeaker & Audio", 0xFFA855F7, "music"),
    INDUSTRIAL("Industrial Machinery", 0xFF3B82F6, "factory"),
    AMBIENT("Ambient Background", 0xFF64748B, "ambient");

    companion object {
        fun fromYamnetLabel(label: String): NoiseCategory {
            val lower = label.lowercase()
            return when {
                lower.contains("vehicle") || lower.contains("car") ||
                lower.contains("truck") || lower.contains("bus") ||
                lower.contains("motorcycle") || lower.contains("engine") ||
                lower.contains("traffic") || lower.contains("tire") ||
                lower.contains("accelerating") || lower.contains("revving") -> TRAFFIC

                lower.contains("drill") || lower.contains("hammer") ||
                lower.contains("saw") || lower.contains("power tool") ||
                lower.contains("jackhammer") || lower.contains("construction") ||
                lower.contains("demolition") -> CONSTRUCTION

                lower.contains("horn") || lower.contains("honk") ||
                lower.contains("siren") || lower.contains("alarm") ||
                lower.contains("beep") || lower.contains("buzzer") -> HORN

                lower.contains("music") || lower.contains("singing") ||
                lower.contains("guitar") || lower.contains("drum") ||
                lower.contains("bass") || lower.contains("loudspeaker") ||
                lower.contains("beat") || lower.contains("song") -> MUSIC

                lower.contains("machine") || lower.contains("factory") ||
                lower.contains("grinder") || lower.contains("compressor") ||
                lower.contains("generator") || lower.contains("mechanical") ||
                lower.contains("pump") -> INDUSTRIAL

                else -> AMBIENT
            }
        }
    }
}
