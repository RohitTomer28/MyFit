// SaveStepsWorker.kt
package com.example.myfit_new.workers

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myfit_new.DataStoreSingleton.dataStore
import com.example.myfit_new.database.StepDatabaseHelper
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SaveStepsWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            // Get current step count from DataStore
            val preferences = context.dataStore.data.first()
            val currentSteps = preferences[intPreferencesKey("step_count")] ?: 0

            Log.d("SaveStepsWorker", "Current steps: $currentSteps")

            // Get today's date
            val today = LocalDate.now()
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formattedDate = today.format(dateFormatter)
            Log.d("SaveStepsWorker", "Formatted date: $formattedDate")

            // Save to database
            val dbHelper = StepDatabaseHelper(context)
            dbHelper.saveSteps(formattedDate, currentSteps)

            Log.d("SaveStepsWorker", "Saved steps for date: $formattedDate, steps: $currentSteps")
            dbHelper.close()

            return Result.success()
        } catch (e: Exception) {
            Log.e("SaveStepsWorker", "Error saving steps: ${e.message}", e)
            return Result.failure()
        }
    }
}