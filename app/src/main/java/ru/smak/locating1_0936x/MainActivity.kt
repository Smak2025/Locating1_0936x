package ru.smak.locating1_0936x

import android.Manifest
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.LineStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
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

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(this)

        enableEdgeToEdge()
        setContent {
            Locating1_0936xTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Greeting(
                            isGranted = viewModel.permissionGranted,
                        )
                        Map(
                            viewModel.locationList,
                            modifier = Modifier.fillMaxSize(),
                        )
//                        LazyColumn(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .padding(8.dp)
//                        ) {
//                            items(viewModel.locationList) {
//                                Card(
//                                    modifier = Modifier.fillMaxWidth()
//                                ) {
//                                    Column(
//                                        Modifier.fillMaxWidth().padding(8.dp),
//                                        verticalArrangement = Arrangement.spacedBy(16.dp)
//                                    ) {
//                                        Text(
//                                            stringResource(R.string.lat, it.latitude),
//                                        )
//                                        Text(
//                                            stringResource(R.string.lon, it.longitude),
//                                        )
//                                    }
//                                }
//                            }
//                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
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

@Composable
fun Map(
    locationList: List<Location>,
    modifier: Modifier = Modifier,
){
    fun moveToCurrentPosition(map: com.yandex.mapkit.map.Map){
        locationList.lastOrNull()?.let { currentLocation ->
            val currentPoint = Point(
                currentLocation.latitude,
                currentLocation.longitude
            )
            map.move(
                CameraPosition(
                    currentPoint,
                    17.0f,
                    0.0f,
                    45f
                )
            )
        }
    }

    fun createPolyLine() = Polyline(
        locationList.map { Point(it.latitude, it.longitude) }
    )

    AndroidView(
        factory = { context ->
            MapView(context).also{ view ->
                view.mapWindow.map.apply {
                    moveToCurrentPosition(this)
                }
            }
        },
        update = { view ->
            val line = createPolyLine()
            view.mapWindow.map.apply {
                moveToCurrentPosition(this)
                mapObjects.clear()
                mapObjects.addPolyline(line).apply {
                    style = LineStyle(
                        7f,
                        0f,
                        Color.Red.toArgb(),
                        5f,
                        false,
                        5f,
                        0f,
                        0f,
                        5f,
                        0f
                    )
                    setStrokeColor(Color.Green.toArgb())
                }
                mapObjects.addPlacemark { obj ->
                    line.points.lastOrNull()?.let {
                        obj.geometry = it
                        obj.setText("Текущее положение")
                        obj.setIcon(ImageProvider.fromResource(view.context, R.drawable.outline_add_location_24, false))
                    }
                }
            }
        },
        modifier = modifier)
}