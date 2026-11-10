package org.proven.tresenrattla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.proven.tresenrattla.ui.GameScreen
import org.proven.tresenrattla.ui.theme.TresEnRattlaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TresEnRattlaTheme {
                GameScreen()
            }
        }
    }
}
