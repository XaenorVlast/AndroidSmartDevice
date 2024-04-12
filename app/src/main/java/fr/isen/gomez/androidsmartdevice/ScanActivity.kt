package fr.isen.gomez.androidsmartdevice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

class ScanActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val bleViewModel: BleViewModel = viewModel()
            bleViewModel.initBle(this@ScanActivity)
            ScanActivityUI(bleViewModel)
        }
    }
}
