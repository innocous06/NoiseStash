package com.sounddose.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class AppDatabase private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _totalDoseToday = MutableStateFlow<Double?>(0.0)
    val totalDoseToday: StateFlow<Double?> = _totalDoseToday.asStateFlow()

    private val _categoryBreakdownToday = MutableStateFlow<List<CategoryDose>>(emptyList())
    val categoryBreakdownToday: StateFlow<List<CategoryDose>> = _categoryBreakdownToday.asStateFlow()

    private val _last7DaysSummary = MutableStateFlow<List<DailyDoseSummary>>(emptyList())
    val last7DaysSummary: StateFlow<List<DailyDoseSummary>> = _last7DaysSummary.asStateFlow()

    init {
        refreshFlows(LocalDate.now().toString())
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS dose_entries (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp INTEGER NOT NULL,
                date TEXT NOT NULL,
                dbLevel REAL NOT NULL,
                doseIncrement REAL NOT NULL,
                category TEXT NOT NULL,
                rawLabel TEXT NOT NULL,
                confidence REAL NOT NULL,
                locationTag TEXT
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_date ON dose_entries(date)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS dose_entries")
        onCreate(db)
    }

    fun insertEntry(entry: DoseEntry) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("timestamp", entry.timestamp)
            put("date", entry.date)
            put("dbLevel", entry.dbLevel)
            put("doseIncrement", entry.doseIncrement)
            put("category", entry.category)
            put("rawLabel", entry.rawLabel)
            put("confidence", entry.confidence)
            put("locationTag", entry.locationTag)
        }
        db.insert("dose_entries", null, values)
        refreshFlows(entry.date)
    }

    fun insertEntries(entries: List<DoseEntry>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (entry in entries) {
                val values = ContentValues().apply {
                    put("timestamp", entry.timestamp)
                    put("date", entry.date)
                    put("dbLevel", entry.dbLevel)
                    put("doseIncrement", entry.doseIncrement)
                    put("category", entry.category)
                    put("rawLabel", entry.rawLabel)
                    put("confidence", entry.confidence)
                    put("locationTag", entry.locationTag)
                }
                db.insert("dose_entries", null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        refreshFlows(LocalDate.now().toString())
    }

    fun getTotalDoseForDate(date: String): Double? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(doseIncrement) FROM dose_entries WHERE date = ?",
            arrayOf(date)
        )
        var total: Double? = null
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        return total
    }

    fun queryCategoryBreakdown(date: String): List<CategoryDose> {
        val db = readableDatabase
        val list = mutableListOf<CategoryDose>()
        val cursor = db.rawQuery(
            """
            SELECT category, SUM(doseIncrement) as totalDose 
            FROM dose_entries 
            WHERE date = ? 
            GROUP BY category 
            ORDER BY totalDose DESC
            """.trimIndent(),
            arrayOf(date)
        )
        while (cursor.moveToNext()) {
            val cat = cursor.getString(0)
            val dose = cursor.getDouble(1)
            list.add(CategoryDose(cat, dose))
        }
        cursor.close()
        return list
    }

    fun queryLast7DaysSummary(): List<DailyDoseSummary> {
        val db = readableDatabase
        val list = mutableListOf<DailyDoseSummary>()
        val cursor = db.rawQuery(
            """
            SELECT date, SUM(doseIncrement) as totalDose, COUNT(*) as entryCount 
            FROM dose_entries 
            GROUP BY date 
            ORDER BY date ASC 
            LIMIT 7
            """.trimIndent(),
            null
        )
        while (cursor.moveToNext()) {
            val d = cursor.getString(0)
            val dose = cursor.getDouble(1)
            val count = cursor.getInt(2)
            list.add(DailyDoseSummary(d, dose, count))
        }
        cursor.close()
        return list
    }

    private fun refreshFlows(date: String) {
        scope.launch {
            _totalDoseToday.value = getTotalDoseForDate(date) ?: 0.0
            _categoryBreakdownToday.value = queryCategoryBreakdown(date)
            _last7DaysSummary.value = queryLast7DaysSummary()
        }
    }

    companion object {
        private const val DB_NAME = "sounddose.db"
        private const val DB_VERSION = 1

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppDatabase(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
