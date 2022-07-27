package ru.webwarehouse.calltracker.util

import android.util.Patterns

object Validator {

    @JvmStatic
    fun isValidUrl(s: String?): Boolean {
        if (s.isNullOrBlank()) return false
        if (!Patterns.WEB_URL.matcher(s).matches()) return false
        if ("http://" !in s && "https://" !in s) return false
        return true
    }

    @JvmStatic
    fun isValidOperatorId(s: String?): Boolean {
        if (s.isNullOrBlank()) return false
        return true
    }

}
