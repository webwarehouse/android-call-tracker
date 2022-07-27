package ru.webwarehouse.calltracker.ui

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.webwarehouse.calltracker.R
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private var dialog: Dialog? = null

    override fun onStart() {
        super.onStart()
        checkForPermissions()
    }

    override fun onStop() {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
        super.onStop()
    }

    private fun checkForPermissions() {
        Timber.d("checkForPermissions called")

        val callLogIsGranted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED

        val phoneStateIsGranted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED

        when {
            !callLogIsGranted && !phoneStateIsGranted -> {
                val rationaleCallLog = shouldShowRequestPermissionRationale(Manifest.permission.READ_CALL_LOG)
                val rationalPhoneState = shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)
                if (rationaleCallLog || rationalPhoneState) {
                    showGoToSettingsToGrantPermissionDialog()
                } else {
                    requestPermissions(arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE), 0)
                }
            }

            !callLogIsGranted -> {
                val rationaleCallLog = shouldShowRequestPermissionRationale(Manifest.permission.READ_CALL_LOG)
                if (rationaleCallLog) {
                    showGoToSettingsToGrantPermissionDialog()
                } else {
                    requestPermissions(arrayOf(Manifest.permission.READ_CALL_LOG), 1)
                }
            }

            !phoneStateIsGranted -> {
                val rationalPhoneState = shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)
                if (rationalPhoneState) {
                    showGoToSettingsToGrantPermissionDialog()
                } else {
                    requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), 2)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (PackageManager.PERMISSION_DENIED in grantResults) {
            showGoToSettingsToGrantPermissionDialog()
        }
    }

    private fun showGoToSettingsToGrantPermissionDialog() {
        Timber.d("showGoToSettingsToGrantPermissionDialog called")
        dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.missed_permissions)
            .setMessage(R.string.call_log_permission_required)
            .setNegativeButton(R.string.exit) { _, _ ->
                dialog!!.dismiss()
                finish()
            }
            .setPositiveButton(R.string.settings) { _, _ ->
                dialog!!.dismiss()
                goToSettingsToRequestCallLogPermission()
            }
            .create()

        dialog!!.setCancelable(false)

        dialog!!.show()

    }

    private fun goToSettingsToRequestCallLogPermission() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts("package", packageName, null)
        }

        startActivity(intent)
    }

}
