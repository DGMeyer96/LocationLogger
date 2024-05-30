package com.example.locationlogger

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    fun startRecordingData() {
        Log.i("MainViewModel", "startRecordingData()")
        recordData.value = true
    }

    fun stopRecordingData() {
        Log.i("MainViewModel", "stopRecordingData()")
        recordData.value = false
    }
}