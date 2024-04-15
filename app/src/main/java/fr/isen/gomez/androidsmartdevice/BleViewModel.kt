package fr.isen.gomez.androidsmartdevice

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Log
import androidx.core.content.ContextCompat
import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*

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
        if (granted) {
            _isScanning.value = false // Reset scanning state
        }
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

    @RequiresApi(Build.VERSION_CODES.S)
    fun toggleBleScan(context: Context) {
        if (_isScanning.value) {
            stopBleScan(context)
        } else {
            startBleScan(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startBleScan(context: Context) {
        // Assurez-vous que toutes les permissions nécessaires, y compris la permission de localisation en arrière-plan, sont accordées
        val permissionsGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (permissionsGranted) {
            bluetoothLeScanner?.let { scanner ->
                _isScanning.value = true
                scanner.startScan(scanCallback)
                handler.postDelayed({
                    if (_isScanning.value) stopBleScan(context)
                }, SCAN_PERIOD)
                Log.d("BleViewModel", "Scanning started.")
            } ?: run {
                Log.d("BleViewModel", "startBleScan: Error initializing BLE scanner.")
                _errorMessage.value = "Error initializing BLE scanner."
            }
        } else {
            Log.d("BleViewModel", "startBleScan: Necessary permissions not granted.")
            _errorMessage.value = "Necessary permissions not granted."
        }
    }


    private fun stopBleScan(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            bluetoothLeScanner?.stopScan(scanCallback)
            _isScanning.value = false
            Log.d("BleViewModel", "Scanning stopped.")
        } else {
            Log.d("BleViewModel", "stopBleScan: BLUETOOTH_SCAN permission not granted.")
            _errorMessage.value = "BLUETOOTH_SCAN permission not granted."
        }
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
