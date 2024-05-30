package com.example.locationlogger

import android.location.Location
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Job

val recordData = MutableLiveData(false)

//region LOCATION
const val MAX_LOCATION_COUNT = 600  // poll rate of 1sec, so 10min of data
val currentLocationString = mutableStateOf("No Current Location")
val locationList = mutableStateListOf<LocationAndSensorData>()
val currentLatitude = mutableStateOf("")
val currentLongitude = mutableStateOf("")
val currentBearing = mutableStateOf("")
val currentAltitude = mutableStateOf("")
val currentAccuracy = mutableStateOf("")
val currentTime = mutableStateOf("")
val currentSpeed = mutableStateOf("")
//endregion

//region SENSORS
val accelerometerReading = FloatArray(3)
val magnetometerReading = FloatArray(3)
val rotationMatrix = FloatArray(9)
val orientationAngles = FloatArray(3)
val devicePitch = mutableFloatStateOf(0f)
val deviceRoll = mutableFloatStateOf(0f)
val deviceAzimuth = mutableFloatStateOf(0f)
//endregion