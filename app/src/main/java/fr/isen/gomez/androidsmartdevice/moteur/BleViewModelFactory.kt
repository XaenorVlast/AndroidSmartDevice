package fr.isen.gomez.androidsmartdevice.moteur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BleViewModelFactory(private val bluetoothManager: BluetoothServiceManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BleViewModel(bluetoothManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
