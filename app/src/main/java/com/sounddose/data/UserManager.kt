package com.sounddose.data

import android.content.Context
import android.content.SharedPreferences

data class UserProfile(
    val name: String,
    val age: Int,
    val profession: String,
    val environment: String = UserManager.DEFAULT_ENVIRONMENT,
    val budgetMultiplier: Float,
    val isOnboarded: Boolean
)

class UserManager(context: Context) {
    private val prefs: SharedPreferences = run {
        val newPrefs = context.getSharedPreferences("noisestash_user_prefs", Context.MODE_PRIVATE)
        val oldPrefs = context.getSharedPreferences("noicestash_user_prefs", Context.MODE_PRIVATE)
        if (oldPrefs.all.isNotEmpty() && newPrefs.all.isEmpty()) {
            val editor = newPrefs.edit()
            oldPrefs.all.forEach { (k, v) ->
                when (v) {
                    is String -> editor.putString(k, v)
                    is Int -> editor.putInt(k, v)
                    is Boolean -> editor.putBoolean(k, v)
                    is Float -> editor.putFloat(k, v)
                    is Long -> editor.putLong(k, v)
                }
            }
            editor.apply()
        }
        newPrefs
    }

    companion object {
        private const val KEY_NAME = "key_user_name"
        private const val KEY_AGE = "key_user_age"
        private const val KEY_PROFESSION = "key_user_profession"
        private const val KEY_ENVIRONMENT = "key_user_environment"
        private const val KEY_BUDGET_MULT = "key_budget_multiplier"
        private const val KEY_IS_ONBOARDED = "key_is_onboarded"
        private const val KEY_THEME_IS_DARK = "key_theme_is_dark"

        const val DEFAULT_NAME = "Alex"
        const val DEFAULT_AGE = 22
        const val DEFAULT_PROFESSION = "Commuter"
        const val DEFAULT_ENVIRONMENT = "Metro & Transit Commute"
        const val DEFAULT_BUDGET_MULT = 1.0f

        val ENVIRONMENTS = listOf(
            "Metro & Transit Commute",
            "Construction & Heavy Industry",
            "High-Traffic Urban Roadside",
            "Open Office & Coworking",
            "Campus & College",
            "Quiet Residential / Remote"
        )

        val PROFESSIONS = listOf(
            "Student",
            "Musician / DJ",
            "Construction / Factory",
            "Commuter / Driver",
            "Office Worker",
            "Healthcare",
            "Other"
        )

        @Volatile
        private var instance: UserManager? = null

        fun getInstance(context: Context): UserManager {
            return instance ?: synchronized(this) {
                instance ?: UserManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun getUserProfile(): UserProfile {
        val name = prefs.getString(KEY_NAME, DEFAULT_NAME) ?: DEFAULT_NAME
        val age = prefs.getInt(KEY_AGE, DEFAULT_AGE)
        val profession = prefs.getString(KEY_PROFESSION, DEFAULT_PROFESSION) ?: DEFAULT_PROFESSION
        val environment = prefs.getString(KEY_ENVIRONMENT, DEFAULT_ENVIRONMENT) ?: DEFAULT_ENVIRONMENT
        val budgetMultiplier = prefs.getFloat(KEY_BUDGET_MULT, DEFAULT_BUDGET_MULT)
        val isOnboarded = prefs.getBoolean(KEY_IS_ONBOARDED, false)

        return UserProfile(
            name = name,
            age = age,
            profession = profession,
            environment = environment,
            budgetMultiplier = budgetMultiplier,
            isOnboarded = isOnboarded
        )
    }

    fun saveUserProfile(
        name: String,
        age: Int,
        profession: String,
        environment: String = DEFAULT_ENVIRONMENT,
        budgetMultiplier: Float = 1.0f
    ) {
        prefs.edit()
            .putString(KEY_NAME, name.trim().ifEmpty { DEFAULT_NAME })
            .putInt(KEY_AGE, age.coerceIn(12, 90))
            .putString(KEY_PROFESSION, profession)
            .putString(KEY_ENVIRONMENT, environment)
            .putFloat(KEY_BUDGET_MULT, budgetMultiplier.coerceIn(0.7f, 1.3f))
            .apply()
    }

    fun saveUserProfile(name: String, age: Int, profession: String, budgetMultiplier: Float) {
        val currentEnv = getUserProfile().environment
        saveUserProfile(name, age, profession, currentEnv, budgetMultiplier)
    }

    fun setOnboarded(completed: Boolean) {
        prefs.edit().putBoolean(KEY_IS_ONBOARDED, completed).apply()
    }

    fun setBudgetMultiplier(multiplier: Float) {
        prefs.edit().putFloat(KEY_BUDGET_MULT, multiplier.coerceIn(0.7f, 1.3f)).apply()
    }

    fun getBudgetMultiplier(): Float {
        return prefs.getFloat(KEY_BUDGET_MULT, DEFAULT_BUDGET_MULT)
    }

    fun isDarkTheme(): Boolean {
        return prefs.getBoolean(KEY_THEME_IS_DARK, true)
    }

    fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_THEME_IS_DARK, isDark).apply()
    }

    fun calculateRecommendedLimitDBSpl(age: Int, profession: String, environment: String = DEFAULT_ENVIRONMENT): Pair<Double, String> {
        val baseLimit = 85.0 // NIOSH baseline
        return when {
            age < 20 -> {
                Pair(82.0, "Sensitive Ear Profile: Protective 82 dB ceiling to preserve inner ear hair cells.")
            }
            profession.contains("Musician", ignoreCase = true) -> {
                Pair(83.0, "Acoustic Artist Profile: Heightened sensitivity for musical frequency spectrum.")
            }
            environment.contains("Construction", ignoreCase = true) || profession.contains("Construction", ignoreCase = true) || profession.contains("Industrial", ignoreCase = true) -> {
                Pair(85.0, "Industrial Shield: Strict NIOSH 85 dB threshold with mandatory quiet breaks.")
            }
            environment.contains("Transit", ignoreCase = true) || profession.contains("Commuter", ignoreCase = true) || profession.contains("Driver", ignoreCase = true) -> {
                Pair(84.0, "Urban Commuter: Shielding against sudden horn clusters and highway drone.")
            }
            age > 50 -> {
                Pair(83.0, "Presbycusis Care: Gentle ceiling preserving high-frequency clarity.")
            }
            else -> {
                Pair(baseLimit, "Standard Wellness: Balanced 85 dB daily acoustic bank.")
            }
        }
    }
}
