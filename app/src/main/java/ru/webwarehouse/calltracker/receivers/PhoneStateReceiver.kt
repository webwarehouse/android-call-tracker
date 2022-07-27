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

        if (intent == null || context == null) return

        this.context = context

        when (intent.action) {
            "android.intent.action.PHONE_STATE" -> {
                val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                if (number.isNullOrEmpty()) {
                    if (intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_RINGING) {
                        toast("r-ringed-empty")
                    }
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
                    TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                        onHookedOff(number)
                        //TelephonyManager.EXTRA_
                    }
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
        repo.onRinged(number)
    }

    private fun onHookedOff(number: String) {
        Timber.i("$number hooked")
        toast("r-hooked: $number")
        repo.onHooked(number)
    }

    private fun onIdle(number: String) {
        Timber.i("$number idle")
        toast("r-idle: $number")
        repo.onIdle(number)
    }

    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

}
