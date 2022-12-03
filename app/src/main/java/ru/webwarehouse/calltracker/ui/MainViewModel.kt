package ru.webwarehouse.calltracker.ui

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.webwarehouse.calltracker.util.PrefsUtil
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val prefs: SharedPreferences) : ViewModel() {



}
