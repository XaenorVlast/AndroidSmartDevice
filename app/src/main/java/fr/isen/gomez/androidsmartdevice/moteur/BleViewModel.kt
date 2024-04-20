package fr.isen.gomez.androidsmartdevice.moteur

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow

class BleViewModel(private val bluetoothManager: BluetoothServiceManager) : ViewModel() {

    // Ces StateFlows sont exposés en tant que valeurs immuables à l'extérieur de la classe
    val isScanning: StateFlow<Boolean> = bluetoothManager.isScanning
    val devicesList: StateFlow<List<BluetoothDeviceInfo>> = bluetoothManager.devicesList
    val errorMessage: MutableStateFlow<String?> = bluetoothManager.errorMessage

    // Met à jour le message d'erreur en cas de permissions non accordées
    fun updatePermissions(granted: Boolean) {
        if (!granted) {
            errorMessage.value = "Necessary permissions not granted."
        }
    }

    // Initialise le Bluetooth via le BluetoothServiceManager
    fun initBle() {
        bluetoothManager.initBle()
    }

    // Bascule l'état du scan Bluetooth
    fun toggleBleScan() {
        if (isScanning.value) {
            bluetoothManager.stopBleScan()
        } else {
            bluetoothManager.startBleScan()
        }
    }

    // Tente de se connecter à un appareil Bluetooth
    fun connectToDevice(context: Context, deviceAddress: String, onConnected: (Boolean, String?) -> Unit) {
        bluetoothManager.connectToDevice(context, deviceAddress, onConnected)
    }

    // Classe de données pour les informations sur les appareils Bluetooth
    data class BluetoothDeviceInfo(val name: String?, val address: String)
}
