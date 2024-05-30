package com.example.locationlogger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true, widthDp = 1080, heightDp = 1920)
@Composable
fun MainView(viewModel: MainViewModel) {
    val isRecordingData by remember { mutableStateOf(recordData) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 72.dp, horizontal = 36.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(isRecordingData.value == true) {
            Button(
                onClick = { viewModel.stopRecordingData() },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(text = "Stop")
            }
        } else {
            Button(
                onClick = { viewModel.startRecordingData() },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(text = "Start")
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Sensor Data",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Pitch: ${devicePitch.floatValue}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Roll: ${deviceRoll.floatValue}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Azimuth: ${deviceAzimuth.floatValue}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.onBackground)
        ) {}

        Text(
            text = "Current Location",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Latitude: ${currentLatitude.value}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Longitude: ${currentLongitude.value}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Accuracy: ${currentAccuracy.value}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Altitude: ${currentAltitude.value}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Bearing: ${currentBearing.value}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.onBackground)
        ) {}

        Text(
            text = "Location History",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Column(
            modifier = Modifier
                .wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start
        ) {
            locationList.forEachIndexed { index, location ->
                LocationListItem(index, location)
            }
        }
    }
}

@Composable
private fun LocationListItem(index: Int, data: LocationAndSensorData) {
    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Location Data[$index]",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Latitude: ${data.latitude}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Longitude: ${data.longitude}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Bearing: ${data.bearing}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Altitude: ${data.altitude}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Time: ${data.time}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Speed: ${data.speed}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.onBackground)
        ) {}
    }
}