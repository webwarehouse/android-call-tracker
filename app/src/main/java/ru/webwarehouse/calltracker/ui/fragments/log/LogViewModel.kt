package ru.webwarehouse.calltracker.ui.fragments.log

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(private val prefs: SharedPreferences) : ViewModel() {

    fun getLog(): List<String> {
        return prefs.all.map { it.key }.filter { it.startsWith("b/") }.sortedBy { it.removePrefix("b/").toLong() }.map {
            val millis = it.removePrefix("b/").toLong()
            val message = prefs.getString(it, null)

            val c = Calendar.getInstance().apply {
                clear()
                timeInMillis = millis
            }

            val time = "${c.get(Calendar.HOUR_OF_DAY)}:${c.get(Calendar.MINUTE)}"

            return@map "$time : $message"
        }.reversed()
    }

    fun getNumbers(): List<String> {
        return prefs.all.map { it.key }.filter { it.startsWith("n/") }.map {
            return@map "${it.removePrefix("n/")} : ${prefs.getString(it, null)}"
        }
    }

}
