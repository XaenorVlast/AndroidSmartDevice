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

class BluetoothServiceManager private constructor() {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private val handler = Handler(Looper.getMainLooper())
    private val SCAN_PERIOD: Long = 10000

    val isScanning = MutableStateFlow(false)
    val devicesList = MutableStateFlow<List<BleViewModel.BluetoothDeviceInfo>>(emptyList())
    val errorMessage = MutableStateFlow<String?>(null)

    private var bluetoothGatt: BluetoothGatt? = null
    private var onConnectedCallback: ((Boolean, String?) -> Unit)? = null

    companion object {
        @Volatile private var instance: BluetoothServiceManager? = null
        fun getInstance(context: Context): BluetoothServiceManager =
            instance ?: synchronized(this) {
                instance ?: BluetoothServiceManager().also { it.initializeAdapter(context); instance = it }
            }
    }

    private fun initializeAdapter(context: Context) {
        val bluetoothManager =
            context.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
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
        onConnectedCallback = onConnected
        val device: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(deviceAddress)
        if (device == null) {
            Log.e("BluetoothServiceManager", "Device not found with address: $deviceAddress")
            onConnected(false, "Device not found.")
            return
        }

        Log.d("BluetoothServiceManager", "Attempting to connect to device: $deviceAddress")
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    fun writeLedCharacteristic(ledIndex: Int, isOn: Boolean) {
        val value = when {
            isOn && ledIndex == 1 -> byteArrayOf(0x01) // Turn on LED 1
            isOn && ledIndex == 2 -> byteArrayOf(0x02) // Turn on LED 2
            isOn && ledIndex == 3 -> byteArrayOf(0x03) // Turn on LED 3
            else -> byteArrayOf(0x00)                  // Turn off the LED or all LEDs
        }

        if (bluetoothGatt != null) {
            val service = bluetoothGatt?.services?.getOrNull(2) // Service #3 at index 2
            val characteristic = service?.characteristics?.getOrNull(0) // First characteristic of service #3

            if (characteristic != null) {
                characteristic.value = value
                bluetoothGatt?.writeCharacteristic(characteristic)
            } else {
                Log.e("BluetoothServiceManager", "Invalid or not found characteristic.")
            }
        } else {
            Log.e("BluetoothServiceManager", "BluetoothGatt is null, connection not established.")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.i("BluetoothServiceManager", "Connected to GATT server.")
                    onConnectedCallback?.invoke(true, null)
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.i("BluetoothServiceManager", "Disconnected from GATT server.")
                    onConnectedCallback?.invoke(false, "Disconnected unexpectedly.")
                    gatt?.close()
                }
                else -> Log.w("BluetoothServiceManager", "Unknown GATT connection state: $newState")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("BluetoothServiceManager", "Services discovered.")
            } else {
                Log.w("BluetoothServiceManager", "Failed to discover services with status: $status")
            }
        }
    }
}
