# LocationLogger
Location Logger for Android.  Logs location data and sensor data to a JSON which can be parsed in Godot, Unity, and Unreal.

JSON includes:
- Pitch
- Roll
- Yaw
- Latitude
- Longitude
- Bearing
- Altitude
- Time
- Speed

JSON files are saved to /data/data/com.example.locationlogger/files/ on Android device.
Location and Sensor updates occur every 1sec.
A JSON file is generated whenever the app is stopped, screen is closed, or 10min of data has been collected.

Tested on Pixel 6a running Anroid 13.

TODO: Get this working as a background service with the correct update frequency.
