package fr.isen.gomez.androidsmartdevice.moteur

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow

class BleViewModel(private val bluetoothManager: BluetoothServiceManager) : ViewModel() {

    val isScanning: StateFlow<Boolean> = bluetoothManager.isScanning
    val devicesList: StateFlow<List<BluetoothDeviceInfo>> = bluetoothManager.devicesList
    val errorMessage: MutableStateFlow<String?> = bluetoothManager.errorMessage

    private val _ledState = MutableLiveData<BooleanArray>()
    val ledState: LiveData<BooleanArray> = _ledState

    fun updatePermissions(granted: Boolean) {
        if (!granted) {
            errorMessage.value = "Necessary permissions not granted."
        }
    }

    fun toggleBleScan() {
        if (isScanning.value) {
            bluetoothManager.stopBleScan()
        } else {
            bluetoothManager.startBleScan()
        }
    }

    fun connectToDevice(context: Context, deviceAddress: String, onConnected: (Boolean, String?) -> Unit) {
        bluetoothManager.connectToDevice(context.applicationContext, deviceAddress, onConnected)
    }

    data class BluetoothDeviceInfo(val name: String?, val address: String)

    fun toggleLed(ledIndex: Int, isOn: Boolean) {
        bluetoothManager.writeLedCharacteristic(ledIndex + 1, isOn)

        _ledState.value = _ledState.value?.also {
            it[ledIndex] = isOn
        } ?: BooleanArray(3) { i -> i == ledIndex && isOn }
    }


}
