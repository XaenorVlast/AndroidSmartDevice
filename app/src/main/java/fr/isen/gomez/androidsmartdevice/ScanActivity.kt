package fr.isen.gomez.androidsmartdevice

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.isen.gomez.androidsmartdevice.moteur.BleViewModel
import fr.isen.gomez.androidsmartdevice.moteur.BluetoothServiceManager
import fr.isen.gomez.androidsmartdevice.moteur.PermissionsHelper

import fr.isen.gomez.androidsmartdevice.vue.ScanActivityUI


class ScanActivity : ComponentActivity() {

    private lateinit var bleViewModel: BleViewModel
    private lateinit var permissionsHelper: PermissionsHelper

    // Le lanceur de demande de permissions reste inchangé
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            handlePermissionsResult(permissions)
        }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation des instances nécessaires
        permissionsHelper = PermissionsHelper(this)  // Assurez-vous que ceci est fait en premier
        val bluetoothManager = BluetoothServiceManager(this)
        bleViewModel = BleViewModel(bluetoothManager)

        setContent {
            ScanActivityUI(bleViewModel)
        }

        checkPermissionsAndInitialize()  // Cette fonction dépend de permissionsHelper, donc doit être appelée après son initialisation
    }

    private fun handlePermissionsResult(permissions: Map<String, Boolean>) {
        val allPermissionsGranted = permissions.entries.all { it.value }
        bleViewModel.updatePermissions(allPermissionsGranted)
        if (allPermissionsGranted) {
            bleViewModel.initBle()
        } else {
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
            bleViewModel.initBle()
        } else {
            permissionsHelper.requestNeededPermissions(requestPermissionLauncher)
        }
    }
}
