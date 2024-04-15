package fr.isen.gomez.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Log

class BleViewModel : ViewModel() {
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private val _devicesList = MutableStateFlow<List<String>>(emptyList())
    val devicesList: StateFlow<List<String>> = _devicesList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private val handler = Handler(Looper.getMainLooper())
    private val SCAN_PERIOD: Long = 10000 // 10 seconds

    fun updatePermissions(granted: Boolean) {
        Log.d("BleViewModel", "updatePermissions: Permissions granted = $granted")
        _errorMessage.value = if (!granted) "Necessary permissions not granted." else null
    }

    fun initBle(context: Context) {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            Log.d("BleViewModel", "initBle: Bluetooth not available or not enabled")
            _errorMessage.value = "Bluetooth not available or not enabled."
            return
        }
        bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
        Log.d("BleViewModel", "BLE is initialized and ready to scan.")
    }

    fun toggleBleScan() {
        if (_isScanning.value) {
            stopBleScan()
        } else {
            startBleScan()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startBleScan() {
        bluetoothLeScanner?.let { scanner ->
            _isScanning.value = true
            scanner.startScan(scanCallback)
            handler.postDelayed({
                if (_isScanning.value) stopBleScan()
            }, SCAN_PERIOD)
            Log.d("BleViewModel", "Scanning started.")
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopBleScan() {
        bluetoothLeScanner?.stopScan(scanCallback)
        _isScanning.value = false
        Log.d("BleViewModel", "Scanning stopped.")
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d("BleViewModel", "Scan result received: ${result.device.address}")
            val deviceAddress = result.device.address
            if (!_devicesList.value.contains(deviceAddress)) {
                val updatedList = _devicesList.value.toMutableList()
                updatedList.add(deviceAddress)
                _devicesList.value = updatedList
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.d("BleViewModel", "Scan failed with error: $errorCode")
        }
    }
}
