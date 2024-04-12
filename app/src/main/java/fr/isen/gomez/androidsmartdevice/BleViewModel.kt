package fr.isen.gomez.androidsmartdevice

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BleViewModel : ViewModel() {
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private val _devicesList = MutableStateFlow<List<String>>(emptyList())
    val devicesList: StateFlow<List<String>> = _devicesList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Initialisation
    fun initBle(context: Context) {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            // Le dispositif ne prend pas en charge le Bluetooth
            _errorMessage.value = "Bluetooth non disponible sur cet appareil."
            return
        }
        if (!bluetoothAdapter.isEnabled) {
            // Le Bluetooth n'est pas activé, proposer à l'utilisateur de l'activer
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            context.startActivity(enableBtIntent)
            // Vous devriez attendre un résultat de retour dans une activité avec startActivityForResult()
            // Pour vérifier si l'utilisateur a activé le Bluetooth
        } else {
            // Prêt pour le scan
            _errorMessage.value = null
            startBleScan()
        }
    }

    // Commence ou arrête le scan BLE
    fun toggleBleScan() {
        _isScanning.value = !_isScanning.value
        if (_isScanning.value) {
            // Commence le scan
            startBleScan()  // Supposons que ceci démarre le processus de scan
        } else {
            // Arrête le scan
            stopBleScan()  // Supposons que ceci arrête le processus de scan
        }
    }

    private fun startBleScan() {
        // Implémentez la logique pour démarrer le scan des appareils BLE ici
        // Mettez à jour _devicesList avec les résultats du scan
        // Exemple: _devicesList.value = listOf("Device 1", "Device 2")
    }

    private fun stopBleScan() {
        // Implémentez la logique pour arrêter le scan BLE ici
        // Nettoyer la liste ou gérer les arrêts appropriés
    }
}
