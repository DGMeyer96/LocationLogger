package com.example.locationlogger

import android.location.Location
import kotlinx.serialization.Serializable

// https://kotlinlang.org/docs/serialization.html#serialize-and-deserialize-json
@Serializable
data class LocationAndSensorData(
    val pitch: Float, val roll: Float, val azimuth: Float,
    val latitude: Double, val longitude: Double, val bearing: Float, val altitude: Double,
    val time: Long, val speed: Float) {

    companion object {
        fun fromLocationAndSensors(location: Location, sensors: FloatArray): LocationAndSensorData {
            return LocationAndSensorData(
                pitch = sensors[0],
                roll = sensors[1],
                azimuth = sensors[2],
                latitude = location.latitude,
                longitude = location.longitude,
                bearing = location.bearing,
                altitude = location.altitude,
                time = location.time,
                speed = location.speed
            )
        }
    }
}