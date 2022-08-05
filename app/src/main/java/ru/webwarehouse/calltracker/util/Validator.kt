package ru.webwarehouse.calltracker.util

import android.util.Patterns

object Validator {

    @JvmStatic
    fun isValidUrl(s: String?): Boolean {
        if (s.isNullOrBlank()) return false
        if (!Patterns.DOMAIN_NAME.matcher(s).matches()) return false
        return true
    }

    @JvmStatic
    fun isValidOperatorId(s: String?): Boolean {
        if (s.isNullOrBlank()) return false
        return true
    }

}
