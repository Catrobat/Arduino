#ifndef DEFINES_H
#define DEFINES_H

#include <Arduino.h>

#define STREAM_DELAY_MS 2
#define SERIAL_BUFFER_SIZE 128

#define TCP_PORT 6789
#define TCP_CONNECTION_TIMEOUT_MS 10000

///
/// Input and output pins used on the ArduinoMega board
///
#define PIN_LIGHT_SENSOR A10
#define PIN_VIBRATION_SENSOR A11
#define PIN_STATUS_LED 39
#define PIN_PN532_CS 9
#define PIN_ETHERNET_CS 10

///
/// supported command identifiers
///
#define COMMAND_INVALID_COMMAND   255
#define COMMAND_TAG_EMULATE       '0'
#define COMMAND_VIB_MEASUREMENT   '1'
#define COMMAND_LIGHT_MEASUREMENT '2'
#define COMMAND_VIB_CALIBRATE     '3'

///
/// Light measurement related constants
///
#define LIGHT_SENSOR_THRESHOLD 500
#define LIGHT_MEASUREMENT_RESP_OFF "0LIGHT_END"
#define LIGHT_MEASUREMENT_RESP_ON "1LIGHT_END"

///
/// Vibration measurement related constants
///
#define VIB_MEASUREMENT_RESP_OFF "0VIBRATION_END"
#define VIB_MEASUREMENT_RESP_ON "1VIBRATION_END"
#define BUFFER_SIZE_VIBRATION 2048

///
/// Tag emulation related constants
///
#define CMD_NFC_EMULATION_STARTED "STARTED_NFC_EMULATION"
#define CMD_NFC_EMULATION_TIMEOUT "TIMEDOUT_NFC_EMULATION"
#define CMD_NFC_EMULATION_FINISHED "FINISHED_NFC_EMULATION"
#define NDEF_MODIFIED "NDEF_MODIFIED:"
#define CMD_NFC_TAG_WRITEABLE '0'
#define NFC_EMULATION_TIMEOUT_MS 5000


#endif // DEFINES_H
