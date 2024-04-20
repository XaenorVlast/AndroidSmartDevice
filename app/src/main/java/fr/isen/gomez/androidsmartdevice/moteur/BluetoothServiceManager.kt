package fr.isen.gomez.androidsmartdevice.moteur

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import android.util.Log

class BluetoothServiceManager(private val context: Context) {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private val handler = Handler(Looper.getMainLooper())
    private val SCAN_PERIOD: Long = 10000

    val isScanning = MutableStateFlow(false)
    val devicesList = MutableStateFlow<List<BleViewModel.BluetoothDeviceInfo>>(emptyList())
    val errorMessage = MutableStateFlow<String?>(null)

    init {
        initializeBluetoothAdapter()
    }

    private fun initializeBluetoothAdapter() {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            errorMessage.value = "Bluetooth not available or not enabled."
        } else {
            bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
            Log.d("BluetoothServiceManager", "BLE is initialized and ready to scan.")
        }
    }

    fun initBle() {
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            Log.d("BluetoothServiceManager", "initBle: Bluetooth not available or not enabled")
            errorMessage.value = "Bluetooth not available or not enabled."
        } else {
            bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
            Log.d("BluetoothServiceManager", "BLE is initialized and ready to scan.")
        }
    }

    @SuppressLint("MissingPermission")
    fun startBleScan() {
        bluetoothLeScanner?.let { scanner ->
            isScanning.value = true
            scanner.startScan(scanCallback)
            handler.postDelayed({
                if (isScanning.value) stopBleScan()
            }, SCAN_PERIOD)
            Log.d("BluetoothManager", "Scanning started.")
        }
    }

    @SuppressLint("MissingPermission")
    fun stopBleScan() {
        bluetoothLeScanner?.stopScan(scanCallback)
        isScanning.value = false
        Log.d("BluetoothManager", "Scanning stopped.")
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val deviceName = result.device.name ?: "Unknown Device"
            val deviceAddress = result.device.address
            val deviceInfo = BleViewModel.BluetoothDeviceInfo(deviceName, deviceAddress)
            val updatedList = devicesList.value.toMutableList()
            if (!updatedList.any { it.address == deviceAddress }) {
                updatedList.add(deviceInfo)
                devicesList.value = updatedList
            }
            Log.d("BluetoothManager", "Scan result received: Name: $deviceName, Address: $deviceAddress")
        }

        override fun onScanFailed(errorCode: Int) {
            errorMessage.value = "Scan failed with error: $errorCode"
            Log.d("BluetoothManager", "Scan failed with error: $errorCode")
        }
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(context: Context, deviceAddress: String, onConnected: (Boolean, String?) -> Unit) {
        val device: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(deviceAddress)
        if (device == null) {
            onConnected(false, "Device not found.")
            return
        }

        device.connectGatt(context, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    onConnected(true, null)
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    onConnected(false, "Disconnected unexpectedly.")
                }
            }
        })
    }
}
