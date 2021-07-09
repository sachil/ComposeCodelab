package xyz.sachil.staggeredgrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import xyz.sachil.staggeredgrid.ui.screen.StaggeredGridScreen
import xyz.sachil.staggeredgrid.ui.theme.ComposeCodelabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window,false)
        setContent {
            ComposeCodelabTheme {
                Scaffold(backgroundColor = MaterialTheme.colors.primary) {
                    StaggeredGridScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeCodelabTheme {
        ComposeCodelabTheme {
            Scaffold(backgroundColor = MaterialTheme.colors.primary) {
                StaggeredGridScreen()
            }
        }
    }
}