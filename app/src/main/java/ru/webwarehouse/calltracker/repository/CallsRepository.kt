package ru.webwarehouse.calltracker.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.webwarehouse.calltracker.R
import ru.webwarehouse.calltracker.network.calls.*
import timber.log.Timber
import javax.inject.Inject

@SuppressLint("ApplySharedPref")
class CallsRepository @Inject constructor(
    private val prefs: SharedPreferences,
    private val context: Context,
) {

    private val retrofitService: CallsApiService by lazy {
        val apiUrl = "https://${prefs.getString(context.getString(R.string.key_api_url), null)!!}/"
        CallsApi(apiUrl).retrofitService
    }

    private val operatorCode: String by lazy {
        return@lazy prefs.getString(context.getString(R.string.key_operator_id), null)!!
    }

    fun onRinged(number: String) {
        prefs.edit().putString("n/$number", "incoming").commit()
        val token = "Bearer ${prefs.getString(context.getString(R.string.key_token), null) ?: ""}"
        CoroutineScope(Dispatchers.IO).launch {
            val body = ActiveCallPost(
                phone = number,
                type = "incoming",
                operator = operatorCode,
            )

            val task = retrofitService.postActiveCall(body, token)
            task.enqueue(object : Callback<ActiveCallResponse> {

                override fun onResponse(call: Call<ActiveCallResponse>, response: Response<ActiveCallResponse>) {
                    Timber.i("onResponse called; body = ${response.body()}")
                    val id = response.body()!!.id
                    prefs.edit().putString("n/$number", "incoming|$id").commit()
                }

                override fun onFailure(call: Call<ActiveCallResponse>, t: Throwable) {
                    Timber.e("onFailure; error = $t")
                }

            })
        }
    }

    fun onHooked(number: String) {
        val token = "Bearer ${prefs.getString(context.getString(R.string.key_token), null) ?: ""}"
        if ("n/$number" in prefs.all.keys) {
            // Incoming call
            val id = prefs.getString("n/$number", null)!!.split("|").elementAt(1)
            prefs.edit().putString("n/$number", "incoming|$id|${System.currentTimeMillis()}").commit()
        } else {
            // Outgoing call
            val startedAt = System.currentTimeMillis()
            val body = ActiveCallPost(
                phone = number,
                type = "outgoing",
                operator = operatorCode,
            )

            val task = retrofitService.postActiveCall(body, token)
            Timber.e(task.request().url().toString())

            task.enqueue(object : Callback<ActiveCallResponse> {
                override fun onResponse(call: Call<ActiveCallResponse>, response: Response<ActiveCallResponse>) {
                    Timber.i("onResponse called; body = ${response.body()}")
                    val id = response.body()!!.id
                    prefs.edit().putString("n/$number", "outgoing|$id|$startedAt").commit()
                }

                override fun onFailure(call: Call<ActiveCallResponse>, t: Throwable) {
                    Timber.e("onFailure; error = $t")
                }
            })
        }
    }

    fun onIdle(number: String) {
        val data = prefs.getString("n/$number", null)?.split("|") ?: return
        val token = "Bearer ${prefs.getString(context.getString(R.string.key_token), null) ?: ""}"
        val editor = prefs.edit()
        prefs.all.keys.filter { "n/" in it }.forEach {
            editor.remove(it).commit()
        }
        editor.commit()

        val timeInSeconds: Long

        // Most likely rejected incoming call
        if (data.size != 3) {
            if (data.elementAt(0) == "incoming") {
                timeInSeconds = 0L
            } else {
                // If this code called then something wrong happens
                timeInSeconds = 0L
            }
        } else {
            val timeInMillis = System.currentTimeMillis() - data.elementAt(2).toLong()
            timeInSeconds = timeInMillis / 1000
        }

        val id = data.elementAt(1)

        val task =
            retrofitService.postEndedCall(
                id,
                EndedCallPut(duration = timeInSeconds.toInt()),
                token,
            )

        task.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Timber.e("onFailure; error = $t")
            }
        })
    }

}
