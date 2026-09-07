package com.sounddose

import android.app.Application
import android.content.Context
import com.sounddose.data.AppDatabase
import com.sounddose.data.SeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SoundDoseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Pre-populate 7-day realistic demo data on first launch
        val prefs = getSharedPreferences("sounddose_prefs", Context.MODE_PRIVATE)
        val isSeeded = prefs.getBoolean("demo_data_seeded", false)

        if (!isSeeded) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getInstance(applicationContext)
                val demoEntries = SeedData.generateDemoData()
                db.insertEntries(demoEntries)
                prefs.edit().putBoolean("demo_data_seeded", true).apply()
            }
        }
    }
}
