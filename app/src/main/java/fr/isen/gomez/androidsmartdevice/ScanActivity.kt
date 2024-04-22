package fr.isen.gomez.androidsmartdevice

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import fr.isen.gomez.androidsmartdevice.moteur.BleViewModel
import fr.isen.gomez.androidsmartdevice.moteur.BleViewModelFactory
import fr.isen.gomez.androidsmartdevice.moteur.BluetoothServiceManager
import fr.isen.gomez.androidsmartdevice.moteur.PermissionsHelper
import fr.isen.gomez.androidsmartdevice.vue.ScanActivityUI

class ScanActivity : ComponentActivity() {

    private lateinit var bleViewModel: BleViewModel
    private lateinit var permissionsHelper: PermissionsHelper

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            handlePermissionsResult(permissions)
        }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionsHelper = PermissionsHelper(this)
        // Utilisation de l'instance singleton de BluetoothServiceManager
        val bluetoothManager = BluetoothServiceManager.getInstance(this)

        // Créer BleViewModel en utilisant BleViewModelFactory
        val viewModelFactory = BleViewModelFactory(bluetoothManager)
        bleViewModel = ViewModelProvider(this, viewModelFactory).get(BleViewModel::class.java)

        setContent {
            ScanActivityUI(bleViewModel)
        }

        checkPermissionsAndInitialize()
    }

    private fun handlePermissionsResult(permissions: Map<String, Boolean>) {
        val allPermissionsGranted = permissions.entries.all { it.value }
        bleViewModel.updatePermissions(allPermissionsGranted)
        if (!allPermissionsGranted) {
            Toast.makeText(this, "Permission denied. Unable to perform Bluetooth operations.", Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(android.os.Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        checkPermissionsAndInitialize()
    }

    private fun checkPermissionsAndInitialize() {
        if (permissionsHelper.hasAllPermissions()) {
            bleViewModel.updatePermissions(true)
        } else {
            permissionsHelper.requestNeededPermissions(requestPermissionLauncher)
        }
    }
}
