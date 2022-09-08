package ru.webwarehouse.calltracker.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.telephony.TelephonyManager
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import ru.webwarehouse.calltracker.repository.CallsRepository
import ru.webwarehouse.calltracker.util.PrefsUtil
import ru.webwarehouse.calltracker.util.logToPrefs
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PhoneStateReceiver : BroadcastReceiver() {

    private lateinit var context: Context

    @Inject
    lateinit var repo: CallsRepository

    @Inject
    lateinit var prefs: SharedPreferences

    override fun onReceive(context: Context?, intent: Intent?) {

        Timber.v("received")

        if (intent == null || context == null) return

        this.context = context

        when (intent.action) {
            "android.intent.action.PHONE_STATE" -> {
                val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

                if (number.isNullOrEmpty()) {
                    return
                }

                val needToProcess =
                    !prefs.getString(PrefsUtil.API_URL, null).isNullOrEmpty() &&
                    !prefs.getString(PrefsUtil.OPERATOR_ID, null).isNullOrEmpty()

                if (!needToProcess) {
                    toast("r-need")
                    return
                }

                when (intent.getStringExtra(TelephonyManager.EXTRA_STATE)) {
                    TelephonyManager.EXTRA_STATE_RINGING -> onRinged(number)
                    TelephonyManager.EXTRA_STATE_OFFHOOK -> onHooked(number)
                    TelephonyManager.EXTRA_STATE_IDLE -> onIdle(number)
                }
            } else -> {
                toast("wrong")
            }
        }
    }

    private fun onRinged(number: String) {
        Timber.i("$number ringed")
        toast("r-ringed: $number")

        try {
            repo.onRinged(number)
        } catch (e: Exception) {
            logToPrefs(e.toString(), prefs)
        }
    }

    private fun onHooked(number: String) {
        Timber.i("$number hooked")
        toast("r-hooked: $number")

        try {
            repo.onHooked(number)
        } catch (e: Exception) {
            logToPrefs(e.toString(), prefs)
        }
    }

    private fun onIdle(number: String) {
        Timber.i("$number idle")
        toast("r-idle: $number")

        try {
            repo.onIdle(number)
        } catch (e: Exception) {
            logToPrefs(e.toString(), prefs)
        }
    }

    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        logToPrefs(text, prefs)
    }

}
