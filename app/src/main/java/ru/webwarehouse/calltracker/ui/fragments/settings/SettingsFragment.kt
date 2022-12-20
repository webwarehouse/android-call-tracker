package ru.webwarehouse.calltracker.ui.fragments.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.text.format.DateFormat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import ru.webwarehouse.calltracker.R
import java.util.*

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    lateinit var serviceInfoPreference: Preference
    lateinit var prefs: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        serviceInfoPreference = findPreference(getString(R.string.key_service_started_at))!!
        serviceInfoPreference.summary = getString(R.string.service_info, getServiceInfoSummaryDate())
    }

    private fun getServiceInfoSummaryDate(): String {
        val date: Long = prefs.getLong(getString(R.string.key_service_started_at), -1L)
        return DateFormat.format("MMM dd, hh:mm a", Date(date)).toString()
    }
}
