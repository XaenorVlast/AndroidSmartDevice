    package fr.isen.gomez.androidsmartdevice

    import android.os.Bundle
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import android.content.Intent
    import fr.isen.gomez.androidsmartdevice.vue.MainActivityUI


    class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                MainActivityUI {
                    val intent = Intent(this, ScanActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
