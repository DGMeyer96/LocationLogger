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

Tested on Pixel 6a running Anroid 13.
