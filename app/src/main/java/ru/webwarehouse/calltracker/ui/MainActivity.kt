package ru.webwarehouse.calltracker.ui

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.webwarehouse.calltracker.R
import ru.webwarehouse.calltracker.ui.fragments.log.LogFragment
import ru.webwarehouse.calltracker.util.isAccessibilitySettingsOn
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val navController: NavController by lazy {
        findNavController(R.id.fragmentContainer)
    }

    private val viewModel by viewModels<MainViewModel>()

    private fun askForLaunchService() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.enable_accessibility_service)
            .setMessage(R.string.accessibility_service_rationale)
            .setPositiveButton(R.string.enable) { _, _  ->
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivityForResult(intent, 0)
            }
            .setNegativeButton(R.string.i_refuse, null)
            .show()

        viewModel.onAccessibilityRequested()
    }

    /* private fun requestRole() {
        val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        startActivityForResult(intent, REQUEST_ID)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ID) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                // Your app is now the call screening app
            } else {
                // Your app is not the call screening app
            }
        }
    }*/

    /*companion object {
        private const val REQUEST_ID = 1
    }*/

    private var dialog: Dialog? = null

    override fun onStart() {
        super.onStart()
        checkForPermissions()

        if (!viewModel.isAccessibilityAlreadyRequested() && !isAccessibilitySettingsOn(this)) {
            askForLaunchService()
        }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_service -> showGoToSettingsToGrantPermissionDialog()
            R.id.item_logs -> onGoToLogsSelected()
            else -> return false
        }
        return true
    }

    private fun onGoToLogsSelected() {
        if (navController.currentDestination?.id != R.id.logFragment) {
            navController.navigate(R.id.logFragment)
        }
    }
}
