package ru.webwarehouse.calltracker.ui.fragments.home

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.webwarehouse.calltracker.util.PrefsUtil
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val prefs: SharedPreferences) : ViewModel() {

    fun onSave(apiUrl: String, operatorId: String): Boolean {
        prefs.edit().apply {
            putString(PrefsUtil.API_URL, apiUrl)
            putString(PrefsUtil.OPERATOR_ID, operatorId)
        }.apply()
        return true
    }

    fun getApiUrl(): String {
        return prefs.getString(PrefsUtil.API_URL, null) ?: ""
    }

    fun getOperatorCode(): String {
        return prefs.getString(PrefsUtil.OPERATOR_ID, null) ?: ""
    }

}
