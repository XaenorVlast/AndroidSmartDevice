package fr.isen.gomez.androidsmartdevice

import android.Manifest
import android.app.AlertDialog

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

class ScanActivity : ComponentActivity() {
    private lateinit var bleViewModel: BleViewModel

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bleViewModel = ViewModelProvider(this)[BleViewModel::class.java]
        setContent {
            ScanActivityUI(bleViewModel)
        }
        checkPermissionsAndInitialize()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        checkPermissionsAndInitialize()
    }

    private fun checkPermissionsAndInitialize() {
        if (hasAllPermissions()) {
            bleViewModel.updatePermissions(true)
            bleViewModel.initBle(this)
        } else {
            requestNeededPermissions()
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val allPermissionsGranted = permissions.entries.all { it.value }
        bleViewModel.updatePermissions(allPermissionsGranted)
        if (allPermissionsGranted) {
            bleViewModel.initBle(this)
        } else {
            Toast.makeText(this, "Permission denied. Unable to perform Bluetooth operations.", Toast.LENGTH_LONG).show()
        }
    }


    private fun requestNeededPermissions() {
        requestPermissionLauncher.launch(getAllPermissionsForBLE())
    }




    private fun getAllPermissionsForBLE(): Array<String> {
        var allPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            allPermissions += arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_ADMIN)
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            allPermissions += arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        return allPermissions
    }
    private fun hasAllPermissions(): Boolean {
        val allPermissions = getAllPermissionsForBLE()
        return allPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }


}
