package com.example.locationlogger

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationlogger.ui.theme.LocationLoggerTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.GsonBuilder
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/*
 * References
 * https://developer.android.com/develop/sensors-and-location/location/retrieve-current
 * https://developer.android.com/develop/sensors-and-location/sensors/sensors_overview
 */
class MainActivity : ComponentActivity(), SensorEventListener {
    private val TAG = "LocationLogger"
    private val REQUESTING_LOCATION_UPDATES_KEY = "requesting_location_updates_key"

    // LOCATION
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationRequest = LocationRequest.Builder(1000).build()
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Log.i(TAG, "onLocationResult()")
            requestingLocationUpdates = true
            for (location in locationResult.locations){
                // Update UI with location data
                currentLocationString.value = location.toString()
                // Remove the first item if we are beyond size
                if(locationList.size >= MAX_LOCATION_COUNT) {
                    // write to file
                    writeJSONToFile(applicationContext)
                }
                // Get Sensor Data
                updateOrientationAngles()
                // Add new location to the front
                locationList.add(LocationAndSensorData.fromLocationAndSensors(location, orientationAngles))

                Log.i(TAG, "locationList.size - ${locationList.size}")
                currentLatitude.value = location.latitude.toString()
                currentLongitude.value = location.longitude.toString()
                currentBearing.value = location.bearing.toString()
                currentAltitude.value = location.altitude.toString()
                currentAccuracy.value = location.accuracy.toString()
                currentTime.value = location.time.toString()
                currentSpeed.value = location.speed.toString()
            }
        }
    }
    private var requestingLocationUpdates = false

    // SENSORS
    private lateinit var sensorManager: SensorManager

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPermission()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        recordData.observe(this) {
            Log.i(TAG, "recordData: $it")
            if(it) { startRecordingData() }
            else { stopRecordingData() }
        }

        updateValuesFromBundle(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Setup UI
        enableEdgeToEdge()
        setContent {
            LocationLoggerTheme {
                MainView(viewModel())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume()")
    }

    override fun onStop() {
        Log.i(TAG, "onStop()")
        stopRecordingData()

        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, recordData.value == true)
        super.onSaveInstanceState(outState)
    }

    private fun getPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
                }
            }
        }

        // ...

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    @Deprecated("Use updateOrientationAngles() for API > 20")
    private fun getDeviceOrientation() {
        // Rotation matrix based on current readings from accelerometer and magnetometer.
        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)

        // Express the updated rotation matrix as three orientation angles.
        val orientationAngles = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        devicePitch.floatValue = orientationAngles[0]
        deviceRoll.floatValue = orientationAngles[1]
        deviceAzimuth.floatValue = orientationAngles[2]
    }

    // Get readings from accelerometer and magnetometer. To simplify calculations,
    // consider storing these readings as unit vectors.
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    private fun updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        // "rotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        // "orientationAngles" now has up-to-date information.

        Log.i(TAG, "updateOrientationAngles() - $orientationAngles")
        devicePitch.floatValue = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
        deviceRoll.floatValue = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
        deviceAzimuth.floatValue = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    @SuppressLint("MissingPermission")
    private fun startRecordingData() {
        Log.i(TAG, "startLocationUpdates()")

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        if(recordData.value != true) recordData.value = true
    }

    private fun stopRecordingData() {
        Log.i(TAG, "stopLocationUpdates()")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this)
        writeJSONToFile(applicationContext)
        if(recordData.value != false) recordData.value = false
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            recordData.value = savedInstanceState.getBoolean(
                REQUESTING_LOCATION_UPDATES_KEY)
        }
    }

    private fun writeJSONToFile(context: Context) {
        if(locationList.size > 0) {
            try {
                Log.i(TAG, "writeJSONToFile()")
                // write to file
                val gson = GsonBuilder().setPrettyPrinting().create()
                // Get data and time
                val calendar = Calendar.getInstance()
                // Format Date
                val simpleDateFormat = SimpleDateFormat("dd_MM_yyyy", Locale.US)
                val date = simpleDateFormat.format(calendar.time).toString()
                // Format Time
                val simpleTimeFormat = SimpleDateFormat("HH_mm_ss", Locale.US)
                val time = simpleTimeFormat.format(calendar.time).toString()
                // Get file and save to /data/data/com.example.locationlogger/files/
                val file = File(context.filesDir, "location_data_${date}_${time}.json")
                val output = BufferedWriter(FileWriter(file))
                // Write file contents
                output.write(gson.toJson(locationList))
                // Close the writer
                output.close()
                // Clear list and start adding again
                locationList.clear()

                Log.i(TAG, "${file.name} written to ${context.filesDir}")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to write file: $e")
            }
        }
    }
}