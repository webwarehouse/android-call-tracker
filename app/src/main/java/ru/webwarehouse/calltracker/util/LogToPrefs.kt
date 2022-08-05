package ru.webwarehouse.calltracker.util

import android.annotation.SuppressLint
import android.content.SharedPreferences

@SuppressLint("ApplySharedPref")
fun logToPrefs(message: String, prefs: SharedPreferences) {
    val time = System.currentTimeMillis().toString()
    prefs.edit().putString("b/$time", message).commit()
}
