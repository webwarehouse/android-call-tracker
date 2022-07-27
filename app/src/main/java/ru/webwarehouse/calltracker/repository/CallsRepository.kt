package ru.webwarehouse.calltracker.repository

import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.webwarehouse.calltracker.network.calls.ActiveCallPatch
import ru.webwarehouse.calltracker.network.calls.ActiveCallResponse
import ru.webwarehouse.calltracker.network.calls.CallsApi
import ru.webwarehouse.calltracker.network.calls.CallsApiService
import ru.webwarehouse.calltracker.util.PrefsUtil
import timber.log.Timber
import javax.inject.Inject

class CallsRepository @Inject constructor(private val prefs: SharedPreferences) {

    private val retrofitService: CallsApiService by lazy {
        var apiUrl = prefs.getString(PrefsUtil.API_URL, null)!!
        if (!apiUrl.endsWith("/")) {
            apiUrl = "$apiUrl/"
        }
        CallsApi(apiUrl).retrofitService
    }

    private val operatorCode: String by lazy {
        return@lazy prefs.getString(PrefsUtil.OPERATOR_ID, null)!!
    }

    fun onRinged(number: String) {
        prefs.edit().putString("n/$number", "incoming").commit()
        CoroutineScope(Dispatchers.IO).launch {
            val body = ActiveCallPatch(
                phone = number,
                type = "incoming",
                operator = operatorCode,
            )
            val task = retrofitService.patchActiveCall(body)
            task.enqueue(object : Callback<ActiveCallResponse> {
                override fun onResponse(call: Call<ActiveCallResponse>, response: Response<ActiveCallResponse>) {
                    Timber.i("onResponse called; body = ${response.body()}")
                    val id = response.body()!!.id
                    prefs.edit().putString("n/$number", "incoming|$id").commit()
                    Timber.e(prefs.getString("n/+79911313028", null))
                }

                override fun onFailure(call: Call<ActiveCallResponse>, t: Throwable) {
                    Timber.e("onFailure; error = $t")
                }

            })
        }
    }

    fun onHooked(number: String) {
        if ("n/$number" in prefs.all.keys) {
            // Incoming call
            val id = prefs.getString("n/$number", null)!!.split("|").elementAt(1)
            prefs.edit().putString("n/$number", "incoming|$id|${System.currentTimeMillis()}").commit()
        } else {
            // Outgoing call
            val startedAt = System.currentTimeMillis()
            val body = ActiveCallPatch(
                phone = number,
                type = "outgoing",
                operator = operatorCode,
            )

            val task = retrofitService.patchActiveCall(body)
            Timber.e(task.request().url().toString())
            task.enqueue(object : Callback<ActiveCallResponse> {
                override fun onResponse(call: Call<ActiveCallResponse>, response: Response<ActiveCallResponse>) {
                    Timber.i("onResponse called; body = ${response.body()}")
                    val id = response.body()!!.id
                    prefs.edit().putString("n/$number", "incoming|$id|$startedAt").commit()
                    Timber.e(prefs.getString("n/+79911313028", null))
                }

                override fun onFailure(call: Call<ActiveCallResponse>, t: Throwable) {
                    Timber.e("onFailure; error = $t")
                }

            })
        }
    }

    fun onIdle(number: String) {
        val data = prefs.getString("n/$number", null)?.split("|") ?: return
        val id = data.elementAt(1)
        val timeInMillis = System.currentTimeMillis() - data.elementAt(2).toLong()
        val timeInSeconds = timeInMillis / 1000
        prefs.edit().remove("n/$number").commit()
    }

}
