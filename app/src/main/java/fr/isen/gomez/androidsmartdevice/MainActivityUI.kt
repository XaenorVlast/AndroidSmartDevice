package fr.isen.gomez.androidsmartdevice

import android.annotation.SuppressLint

import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import fr.isen.gomez.androidsmartdevice.ui.theme.bleuGris
import fr.isen.gomez.androidsmartdevice.ui.theme.LightBlue

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainActivityUI(onStartScan: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Until Failure") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightBlue,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Réduire la taille de l'image, exemple avec 100.dp par 100.dp
            Image(
                painter = painterResource(id = R.drawable.bluetooth3),
                contentDescription = "BLE Icon",
                modifier = Modifier.size(200.dp) // Taille fixe de l'image
            )

            Button(onClick = onStartScan, colors = ButtonDefaults.buttonColors(LightBlue)) {
                Text("Démarrez le scan pour vous connecter.", color = Color.Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainActivityUI() {
    MainActivityUI(onStartScan = {})
}
