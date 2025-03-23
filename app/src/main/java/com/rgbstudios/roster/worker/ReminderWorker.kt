package com.rgbstudios.roster.worker

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rgbstudios.roster.data.cache.DataStoreManager
import com.rgbstudios.roster.utils.showNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: return Result.failure()
        val message = inputData.getString("message") ?: return Result.failure()
        val staffId = inputData.getString("staffId") ?: return Result.failure()
        val delayOption = inputData.getString("delayOption") ?: return Result.failure()

        showNotification(applicationContext, title, message)

        CoroutineScope(Dispatchers.IO).launch {
            val workId = id.toString()
            val reminder = DataStoreManager.Reminder(staffId, title, message, delayOption, workId)
            DataStoreManager.removeReminder(applicationContext, reminder)
        }

        return Result.success()
    }
}
