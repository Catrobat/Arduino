HardwareTestingBox
==================

This project is necessary for automated hardware feature testing of PocketCode.
It contains the software of the Arduino HardwareTestingBox, which is (should be) connected to the Jenkinsserver.

The binary "sensorserver_release.hex" is flushed to Arduino Mega, what is the core component of the SensorServer.

To work on the Arduino code (.ino), copy the content of the libraries folder (Arduino/HardwareTestingBox/libraries/*) to the sketchbook libraries folder (../Document/Arduino/libraries/)
Open the sketch "sensorserver.ino" and include the added libraries in the Menu "Sketch -> Include Library -> Ethernet/..." 
A detailed documentation can be found at (https://www.arduino.cc/en/Guide/Libraries)

