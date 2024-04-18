package fr.isen.gomez.androidsmartdevice

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

class ConnectedActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Device Connected") })
                }
            ) { innerPadding ->
                // Apply innerPadding to the content to avoid overlap with the TopAppBar
                Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
                    Text("You are now connected to the device!", style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    }
}
