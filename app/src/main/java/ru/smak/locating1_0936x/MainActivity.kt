package ru.smak.locating1_0936x

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.smak.locating1_0936x.ui.theme.Locating1_0936xTheme

class MainActivity : ComponentActivity() {

    // 1.
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // 2. Только в onCreate
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){ isGranted ->
            viewModel.permissionGranted = when {
                isGranted.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> true // действия, если разрешение предоставлено для приближенного определения координат
                isGranted.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> true //  действия, если разрешение предоставлено для точного определения координат
                else -> false // В разрешении отказано
            }

        }

        // 3. Непосредственный запрос разрешений - в любом месте по необходимости
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Locating1_0936xTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Greeting(
                            isGranted = viewModel.permissionGranted,
                        )
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            items(viewModel.locationList) {
                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        Modifier.fillMaxWidth().padding(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Text(
                                            stringResource(R.string.lat, it.latitude),
                                        )
                                        Text(
                                            stringResource(R.string.lon, it.longitude),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(isGranted: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = if (isGranted) stringResource(R.string.access_granted) else stringResource(R.string.access_denied),
        modifier = modifier,
        fontSize = 20.sp
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Locating1_0936xTheme {
        Greeting(false)
    }
}