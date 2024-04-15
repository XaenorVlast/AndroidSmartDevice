package fr.isen.gomez.androidsmartdevice

import android.annotation.SuppressLint
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
                            DeviceItem(device)
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

@Composable
fun DeviceItem(device: BleViewModel.BluetoothDeviceInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Affiche le nom de l'appareil ou "Unknown Device" si le nom est null
        Text(device.name ?: "Unknown Device", style = MaterialTheme.typography.bodyLarge)
        // Affiche l'adresse de l'appareil
        Text(device.address, style = MaterialTheme.typography.bodySmall)
        Divider()
    }
}

