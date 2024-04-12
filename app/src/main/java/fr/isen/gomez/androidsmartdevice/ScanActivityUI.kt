package fr.isen.gomez.androidsmartdevice

import android.annotation.SuppressLint
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.isen.gomez.androidsmartdevice.ui.theme.LightBlue

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanActivityUI(viewModel: BleViewModel = viewModel()) {
    val isScanning by viewModel.isScanning.collectAsState()
    val devicesList by viewModel.devicesList.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Scan BLE")
                },
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
                .padding(innerPadding), // Utiliser innerPadding qui est appliqué pour tenir compte de la TopAppBar
            contentAlignment = Alignment.Center // Centre le contenu à l'intérieur du Box
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
                            Text(device, modifier = Modifier.fillMaxWidth())
                            Divider()
                        }
                    }
                }
            }
        }
    }
}
