package cz.utb.fai.dgapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.utb.fai.dgapp.ui.RoundListScreen
import cz.utb.fai.dgapp.ui.RoundListViewModel
import cz.utb.fai.dgapp.ui.theme.DGAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DGAppTheme {
                Surface {
                    val vm: RoundListViewModel = viewModel ( factory = RoundListViewModel.Factory)

                    RoundListScreen(
                        uiState = vm.uiState,
                        onRefresh = { vm.loadRounds(forceRefresh = true) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoundPreview() {
    DGAppTheme {
        Surface {
            val vm: RoundListViewModel =
                viewModel ( factory = RoundListViewModel.Factory)

            RoundListScreen(
                uiState = vm.uiState,
                onRefresh = { vm.loadRounds(forceRefresh = true) }
            )
        }
    }
}
