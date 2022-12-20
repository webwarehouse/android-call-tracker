package ru.webwarehouse.calltracker.ui.fragments.home

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.webwarehouse.calltracker.R
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val prefs: SharedPreferences) : ViewModel() {

    fun getOperatorCode(context: Context): String {
        return prefs.getString(context.getString(R.string.key_operator_id), null) ?: ""
    }

}
