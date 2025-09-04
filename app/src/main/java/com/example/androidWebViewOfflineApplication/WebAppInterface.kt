package com.example.androidWebViewOfflineApplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.webkit.JavascriptInterface
import android.widget.Toast

/**
 * The bridge class that exposes native Android functions to the JavaScript code in the WebView.
 *
 * @param context The application context, needed to access system services like SharedPreferences and AlarmManager.
 */
class WebAppInterface(private val context: Context) {

    private val sharedPrefs = context.getSharedPreferences("StudyPlannerPrefs", Context.MODE_PRIVATE)
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Saves the list of lessons as a JSON string to SharedPreferences.
     * This method is callable from JavaScript as `AndroidBridge.saveLessons(...)`.
     */
    @JavascriptInterface
    fun saveLessons(lessonsJson: String) {
        with(sharedPrefs.edit()) {
            putString("lessons_json", lessonsJson)
            apply()
        }
    }

    /**
     * Loads the lessons JSON string from SharedPreferences.
     * This method is callable from JavaScript as `AndroidBridge.loadLessons()`.
     * @return The saved JSON string, or null if nothing is saved.
     */
    @JavascriptInterface
    fun loadLessons(): String? {
        return sharedPrefs.getString("lessons_json", null)
    }

    /**
     * Schedules a precise alarm using the native Android AlarmManager.
     * This is reliable and will trigger even if the app is in the background or the device is asleep.
     * This method is callable from JavaScript as `AndroidBridge.setAlarm(...)`.
     *
     * @param timestamp The exact time in milliseconds (since epoch) when the alarm should fire.
     * @param message The message to be displayed in the system notification.
     */
    @JavascriptInterface
    fun setAlarm(timestamp: Long, message: String) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_MESSAGE", message)
        }

        // A PendingIntent is a token that you give to another application (like AlarmManager)
        // which allows that application to execute a piece of your application's code.
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0, // A unique request code. For a single alarm, 0 is fine.
            intent,
            // Flags are crucial for how the PendingIntent behaves.
            // FLAG_UPDATE_CURRENT: If the PendingIntent already exists, update it with the new extra data.
            // FLAG_IMMUTABLE: The PendingIntent cannot be modified by the receiving app. Required for newer Android versions.
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        // RTC_WAKEUP ensures the alarm fires at the specified time and wakes up the device if it's asleep.
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent)
    }

    /**
     * Shows a native Android Toast message.
     * This is a simple, non-blocking way to give feedback.
     * This method is callable from JavaScript as `AndroidBridge.showToast(...)`.
     */
    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}