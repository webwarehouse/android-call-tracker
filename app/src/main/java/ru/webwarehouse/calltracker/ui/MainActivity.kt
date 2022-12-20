package ru.webwarehouse.calltracker.ui

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.webwarehouse.calltracker.R
import ru.webwarehouse.calltracker.service.TrackerService
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkForPermissions()
        } else {
            launchForegroundServiceIfNotYet()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return (Navigation.findNavController(this, R.id.fragmentContainer).navigateUp()
                || super.onSupportNavigateUp())
    }

    private fun launchForegroundServiceIfNotYet() {
        if (!TrackerService.isActive()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, TrackerService::class.java))
            } else {
                startService(Intent(this, TrackerService::class.java))
            }
        }
    }

    override fun onStop() {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
        super.onStop()
    }

    @RequiresApi(Build.VERSION_CODES.M)
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

            else -> {
                launchForegroundServiceIfNotYet()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty()) return

        if (PackageManager.PERMISSION_DENIED in grantResults) {
            showGoToSettingsToGrantPermissionDialog()
        } else {
            launchForegroundServiceIfNotYet()
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
            R.id.item_settings -> onGoToSettingsSelected()
            R.id.item_logs -> onGoToLogsSelected()
            else -> return false
        }
        return true
    }

    private fun onGoToSettingsSelected() {
        if (navController.currentDestination?.id != R.id.settingsFragment) {
            navController.navigate(R.id.settingsFragment)
        }
    }

    private fun onGoToLogsSelected() {
        if (navController.currentDestination?.id != R.id.logFragment) {
            navController.navigate(R.id.logFragment)
        }
    }
}
