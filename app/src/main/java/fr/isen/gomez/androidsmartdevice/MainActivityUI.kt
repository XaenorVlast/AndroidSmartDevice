package fr.isen.gomez.androidsmartdevice

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.gomez.androidsmartdevice.ui.theme.Purple80

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainActivityUI(onStartScan: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Application BLE Scanner") },
                modifier = Modifier.background(color = Purple80).fillMaxWidth()
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.ic_ble_icon), contentDescription = "BLE Icon")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Bienvenue dans l'application BLE Scanner. Appuyez sur le bouton ci-dessous pour commencer à scanner les appareils BLE à proximité.")
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onStartScan) {
                Text("Démarrer le scan")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainActivityUI() {
    MainActivityUI(onStartScan = {})
}
