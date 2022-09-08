package ru.webwarehouse.calltracker.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import ru.webwarehouse.calltracker.BuildConfig
import ru.webwarehouse.calltracker.R
import java.util.Locale

class UserAgent private constructor(private val context: Context) {

    val userAgent: String get() {
        val model = getDeviceModel()
        val osVersion = getOsVersion()
        val locale = getDefaultLocale()
        val appName = getAppName()
        val appVersion = getAppVersion()

        return "$appName/$appVersion (Android $osVersion; $model; $locale)"
    }

    private fun getDeviceModel(): String {
        return Build.MODEL
    }

    private fun getOsVersion(): String {
        return Build.VERSION.RELEASE
    }

    private fun getDefaultLocale(): String {
        val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }

        val lang = locale.language
        val country = locale.country

        return "${lang}_$country"
    }

    private fun getAppName(): String {
        return context.getString(R.string.app_name)
    }

    private fun getAppVersion(): String {
        return BuildConfig.VERSION_NAME
    }

    /**
     * I suppressed warnings and you can only pass only application context here, not activity context
     */
    companion object {

        fun createInstance(appContext: Application) {
            _instance = UserAgent(appContext)
        }

        @SuppressLint("StaticFieldLeak")
        private var _instance: UserAgent? = null

        val instance: UserAgent get() = _instance!!

    }

}
