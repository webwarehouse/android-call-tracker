package ru.webwarehouse.calltracker.util

import android.content.Context
import android.provider.Settings
import android.text.TextUtils.SimpleStringSplitter
import androidx.preference.PreferenceManager
import ru.webwarehouse.calltracker.service.MyAccessibilityService
import timber.log.Timber

private const val ACCESSIBILITY_ENABLED = 1

fun isAccessibilitySettingsOn(context: Context): Boolean {
    var accessibilityEnabled = 0
    val service: String =
        context.getPackageName().toString() + "/" + MyAccessibilityService::class.java.canonicalName
    try {
        accessibilityEnabled = Settings.Secure.getInt(
            context.getApplicationContext().getContentResolver(),
            Settings.Secure.ACCESSIBILITY_ENABLED
        )
    } catch (e: Settings.SettingNotFoundException) {
        Timber.e("Error finding setting, default accessibility to not found: %s", e.message)
        logToPrefs(e.message ?: "Error", PreferenceManager.getDefaultSharedPreferences(context))
    }
    val mStringColonSplitter = SimpleStringSplitter(':')
    if (accessibilityEnabled == ACCESSIBILITY_ENABLED) {
        val settingValue = Settings.Secure.getString(
            context.applicationContext.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        if (settingValue != null) {
            mStringColonSplitter.setString(settingValue)
            while (mStringColonSplitter.hasNext()) {
                val accessibilityService = mStringColonSplitter.next()
                if (accessibilityService.equals(service, ignoreCase = true)) {
                    return true
                }
            }
        }
    }
    return false
}