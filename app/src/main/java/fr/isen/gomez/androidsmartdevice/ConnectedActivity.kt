package fr.isen.gomez.androidsmartdevice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import fr.isen.gomez.androidsmartdevice.moteur.BleViewModel
import fr.isen.gomez.androidsmartdevice.moteur.BleViewModelFactory
import fr.isen.gomez.androidsmartdevice.moteur.BluetoothServiceManager
import fr.isen.gomez.androidsmartdevice.vue.ConnectedActivityUI

class ConnectedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bluetoothManager = BluetoothServiceManager.getInstance(this)
        val viewModelFactory = BleViewModelFactory(bluetoothManager)
        val viewModel = ViewModelProvider(this, viewModelFactory)[BleViewModel::class.java]

        setContent {
            ConnectedActivityUI(viewModel)
        }
    }
}



