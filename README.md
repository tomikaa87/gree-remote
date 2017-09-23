# gree-remote
Simple remote control utility for Gree Smart air conditioners written in C++ using Qt.

The application is only tested on macOS at the moment, but it should work on Windows as well.
The projects depends on CryptoPP which should compile on Windows as well.
Qt 5.9.1 is recommended.

This project is in a very early development stage.
Currently only a basic device discovery and binding is implemented.

There is so much to do:
- Implement device control with all the air conditioning unit features (mode, temperature etc.)
- Implement querying device status
- Create a basic view for a device model
- Implement initial device setup (direct WiFi connection to a unit to set SSID and password of the home WiFi AP)
- At a later phase, firmware update capability could be added
- Command line options could be added to be able to use the app from a terminal
