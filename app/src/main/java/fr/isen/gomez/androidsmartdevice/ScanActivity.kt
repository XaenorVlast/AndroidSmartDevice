package fr.isen.gomez.androidsmartdevice

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import android.widget.Toast
import androidx.annotation.RequiresApi

class ScanActivity : ComponentActivity() {
    private lateinit var bleViewModel: BleViewModel

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ScanActivity", "onCreate: Activity creation started")
        bleViewModel = ViewModelProvider(this)[BleViewModel::class.java]

        setContent {
            ScanActivityUI(bleViewModel)
        }

        checkPermissionsAndInitialize()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkPermissionsAndInitialize() {
        Log.d("ScanActivity", "checkPermissionsAndInitialize: Checking permissions")
        val allPermissionsNeeded = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION // Ajout de la permission ACCESS_BACKGROUND_LOCATION
        )

        if (allPermissionsNeeded.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
            Log.d("ScanActivity", "All permissions are already granted.")
            bleViewModel.updatePermissions(true)
            bleViewModel.initBle(this)
        } else {
            if (allPermissionsNeeded.any { shouldShowRequestPermissionRationale(it) }) {
                showRationaleDialog(allPermissionsNeeded)
            } else {
                Log.d("ScanActivity", "Requesting permissions.")
                requestPermissionLauncher.launch(allPermissionsNeeded)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        Log.d("ScanActivity", "Permissions callback triggered.")
        val allPermissionsGranted = permissions.entries.all { it.value }
        Log.d("ScanActivity", "All permissions granted: $allPermissionsGranted")
        bleViewModel.updatePermissions(allPermissionsGranted)
        if (allPermissionsGranted) {
            Log.d("ScanActivity", "Permissions granted, initializing BLE.")
            bleViewModel.initBle(this)
        } else {
            Log.d("ScanActivity", "Permission denied")
            Toast.makeText(this, "Permission denied. Unable to perform Bluetooth operations.", Toast.LENGTH_LONG).show()
        }
    }

    private fun showRationaleDialog(permissions: Array<String>) {
        Log.d("ScanActivity", "Showing rationale dialog for permissions.")
        AlertDialog.Builder(this)
            .setTitle("Permission Needed")
            .setMessage("This app needs location and Bluetooth permissions to access Bluetooth devices. Please allow all requested permissions.")
            .setPositiveButton("OK") { dialog, which ->
                Log.d("ScanActivity", "OK clicked on rationale dialog, requesting permissions.")
                requestPermissionLauncher.launch(permissions)
            }
            .setNegativeButton("Cancel") { dialog, which ->
                Log.d("ScanActivity", "Cancel clicked on rationale dialog, permissions not granted.")
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
