package fr.isen.gomez.androidsmartdevice

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanActivityUI(viewModel: BleViewModel = viewModel()) {
    val isScanning = viewModel.isScanning.collectAsState()
    val devicesList = viewModel.devicesList.collectAsState()
    val errorMessage = viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Scan BLE") }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = if (isScanning.value) R.drawable.pause else R.drawable.play),
                    contentDescription = if (isScanning.value) "Arrêter le scan" else "Démarrer le scan",
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { viewModel.toggleBleScan() }
                )
                Text(if (isScanning.value) "Scanning..." else "Appuyez pour scanner")
            }

            errorMessage.value?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Divider()

            LazyColumn {
                items(devicesList.value) { device ->
                    Text(device)
                    Divider()
                }
            }
        }
    }
}
