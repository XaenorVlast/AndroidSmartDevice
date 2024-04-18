package fr.isen.gomez.androidsmartdevice

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.isen.gomez.androidsmartdevice.ui.theme.LightBlue


@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanActivityUI(viewModel: BleViewModel = viewModel()) {
    val context = LocalContext.current // Get the local context
    val isScanning by viewModel.isScanning.collectAsState()
    val devicesList by viewModel.devicesList.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Scan BLE") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightBlue,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = if (isScanning) R.drawable.pause else R.drawable.play),
                    contentDescription = if (isScanning) "Arrêter le scan" else "Démarrer le scan",
                    modifier = Modifier
                        .size(150.dp)
                        .clickable { viewModel.toggleBleScan() }
                )
                Text(if (isScanning) "Scanning..." else "Appuyez pour scanner",
                    modifier = Modifier.align(Alignment.CenterHorizontally))

                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                if (devicesList.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(devicesList) { device ->
                            DeviceItem(viewModel, context, device) // Corrected parameter order
                        }
                    }
                } else {
                    Text("Aucun appareil détecté",
                        modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}

enum class ConnectionState {
    Disconnected, Connecting, Connected, Failed
}

@Composable
fun DeviceItem(viewModel: BleViewModel, context: Context, device: BleViewModel.BluetoothDeviceInfo) {
    var connectionState by remember { mutableStateOf(ConnectionState.Disconnected) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(connectionState) {
        if (connectionState == ConnectionState.Connecting) {
            viewModel.connectToDevice(context, device.address) { success, error ->
                if (success) {
                    connectionState = ConnectionState.Connected
                    context.startActivity(Intent(context, ConnectedActivity::class.java))  // Navigate when connected
                } else {
                    errorMessage = error
                    connectionState = ConnectionState.Failed
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = connectionState == ConnectionState.Disconnected) { connectionState = ConnectionState.Connecting }
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(device.name ?: "Unknown Device", style = MaterialTheme.typography.bodyLarge)
        Text(device.address, style = MaterialTheme.typography.bodySmall)
        when (connectionState) {
            ConnectionState.Connecting -> CircularProgressIndicator()
            ConnectionState.Failed -> Text("Failed: $errorMessage", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
            else -> {}
        }
        Divider()
    }
}


