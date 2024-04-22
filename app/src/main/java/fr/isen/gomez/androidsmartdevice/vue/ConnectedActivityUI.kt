package fr.isen.gomez.androidsmartdevice.vue

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.isen.gomez.androidsmartdevice.moteur.BleViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectedActivityUI(viewModel: BleViewModel) {
    val ledState by viewModel.ledState.observeAsState(initial = BooleanArray(3))

    Scaffold(
        topBar = { TopAppBar(title = { Text("Contrôle de l'appareil BLE") }) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text("LED Controls", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
            ledState.forEachIndexed { index, isOn ->
                LedControlSwitch(
                    label = "LED ${index + 1}",
                    isOn = isOn,
                    onToggle = { isChecked -> viewModel.toggleLed(index, isChecked) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun LedControlSwitch(label: String, isOn: Boolean, onToggle: (Boolean) -> Unit) {
    var isChecked by remember { mutableStateOf(isOn) }

    LaunchedEffect(isOn) {
        isChecked = isOn
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label)
        Switch(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                onToggle(it)
            }
        )
    }
}
