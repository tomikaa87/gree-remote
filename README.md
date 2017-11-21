# gree-remote

This project aims to provide an open-source library for controlling [Gree Smart Air Conditioners](http://global.gree.com/). The implementation is based on the reverse-engineered proprietary, JSON-based protocol used by these units. Also there are remote control app implementations for multiple platforms:
* Qt/C++ for Windows/macOS
* ObjectiveC/Cocoa for macOS
* Java for Android

### Getting started

The first step is to clone this repository: `git clone https://github.com/tomikaa87/gree-remote.git`. 
Don't forget to checkout all the submodules using `git submodule update --init --recursive`.

Qt application:
* Compile the CryptoPP using the provided build script in `3rdparty`. This is only necessary for the native C++/Qt library, Android uses Java's crypto library.
* Open `GreeRemote.pro` from the root of the checkout directory
* Compile the project

Android application:
* Open the project from `GreeRemoteAndroid` in Android Studio
* Run the application on the selected device. Keep in mind that the Gree library must access the local network using WiFi, which is not available in the emulator. You must use a physical device.

### Prerequisites

For the Qt library and application:
* Qt 5.9.1
* XCode 9 for macOS, Visual Studio (2017 is preferred) for Windows

For the Android application:
* Android Sutdio 3

For the macOS application:
* XCode 9

### Remarks

This project is in a very early development stage.
Currently only a basic device discovery and binding is implemented.

There is so much to do:
- Implement device control with all the air conditioning unit features (mode, temperature etc.)
- Implement querying device status
- Create a basic view for a device model
- Implement initial device setup (direct WiFi connection to a unit to set SSID and password of the home WiFi AP)
- At a later phase, firmware update capability could be added
- Command line options could be added to be able to use the app from a terminal

## License

This project is licensed under the GPL License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

I would like to thank the additional work to:
* [oroce](https://github.com/oroce)
* [jllcunha](https://github.com/jllcunha)

## Protocol details

This information is based on the implementation of the official [Gree Smart Android App](https://play.google.com/store/apps/details?id=com.gree.smarthome) and the network packets obtained via [Wireshark](https://www.wireshark.org/). The current implementation is incomplete, for example it doesn't have the ability to update the firmware on the AC unit.

The communication protocol uses unicast and broadcast UDP messages sent to port 7000.

### Message encryption and encoding

The protocol uses `pack`-type messages to deliver data from and to the device in a (somewhat) secure way. This message contains a field named `pack`, which encapsulates an another JSON object.

Packs created in the following way:
* Encrypt the JSON with AES128/ECB with PKCS-7 padding using either the generic or the device-specific AES key
* Encode the encrypted binary data using Base64

Decoding a pack is the same process, but in reverse order.
The generic AES key is used for reading scan results and binding devices, the device-specific key is used for direct communication (requesting status, changing parameters etc.).

### Device discovery (scanning)

In order to find all the devices on the network, a scan packet must be broadcasted. This package is a very simple JSON object:

```json
{
  "t": "scan"
}
```

All connected device will send a response JSON like this one:

```json
{
  "t": "pack",
  "i": 1,
  "uid": 0,
  "cid": "<device's MAC address, e.g. 00123456789a>",
  "tcid": "",
  "pack": "<base64 encoded, encrypted data>"
}
```

This is a generic `pack`-type response which has a `pack` field that contains an embedded JSON object. The `pack` is encrypted with AES128/ECB and encoded in Base64. This response is encrypted using the "Generic AES key" which is the same for all devices.

Contents of `pack` should look like this:

```json
{
  "t": "dev",
  "cid": "<MAC address>",
  "bc": "gree",
  "brand": "gree",
  "catalog": "gree",
  "mac": "<MAC address>",
  "mid": "10001",
  "model": "gree",
  "name": "<friendly name of the unit>",
  "series": "gree",
  "vender": "1",
  "ver": "V1.1.13",
  "lock": 0
}
```

You can obtain some basic information (e.g. device's friendly name, software version etc.) from this object.

### Binding to a specific device

In order to communicate with a specific device and obtain the device's unique encryption key, you must bind to it using the following request JSON:

```json
{
  "cid": "app",
  "i": 1,
  "pack": "<encrypted, encoded pack>",
  "t": "pack",
  "tcid": "<MAC address>",
  "uid": 0
}
```

`pack` must have the following content:

```json
{
  "mac": "<MAC address>",
  "t": "bind",
  "uid": 0
}
```

If the binding request succeeds, you should have the following response:

```json
{
  "t": "pack",
  "i": 1,
  "uid": 0,
  "cid": "<MAC address>",
  "tcid": "app",
  "pack": "<encrypted, encoded pack>"
}
```

The `pack` of this response should look like this:

```json
{
  "t": "bindok",
  "mac": "<MAC address>",
  "key": "<unique AES key>",
  "r": 200
}
```

The AES key in the `key` field is used to send control packets to a specific device.

### Reading status of a device

To get the status of the device, a generic `pack` type request must be sent to it:

```json
{
  "cid": "app",
  "i": 0,
  "pack": "<encrypted, encoded pack>",
  "t": "pack",
  "tcid": "<MAC address>",
  "uid": 0
}
```

The `pack` of this request must contain a status request object:

```json
{
  "cols": [
    "Pow", 
    "Mod", 
    "SetTem", 
    "WdSpd", 
    "Air", 
    "Blo", 
    "Health", 
    "SwhSlp", 
    "Lig", 
    "SwingLfRig", 
    "SwUpDn", 
    "Quiet", 
    "Tur", 
    "StHt", 
    "TemUn", 
    "HeatCoolType", 
    "TemRec", 
    "SvSt"
  ],
  "mac": "<MAC address>",
  "t": "status"
}
```

In this object you must define which parameters you are interested in. All of them has a numerical value. The official Gree Smart app uses these fields:

* `Pow`: power state of the device
  * 0: off
  * 1: on
  
* `Mod`: mode of operation
  * 0: auto
  * 1: cool
  * 2: dry
  * 3: fan
  * 4: heat  
  
* "SetTem" and "TemUn": set temperature and temperature unit
  * if `TemUn` = 0, `SetTem` is the set temperature in Celsius
  * if `TemUn` = 1, `SetTem` is the set temperature is Fahrenheit
  
* `WdSpd`: fan speed
  * 0: auto
  * 1: low
  * 2: medium-low (not available on 3-speed units)
  * 3: medium
  * 4: medium-high (not available on 3-speed units)
  * 5: high

* `Air`: controls the state of the fresh air valve (not available on all units)
  * 0: off
  * 1: on

* `Blo`: "Blow" or "X-Fan", this function keeps the fan running for a while after shutting down. Only usable in Dry and Cool mode

* `Health`: controls Health ("Cold plasma") mode, only for devices equipped with "anion generator", which absorbs dust and kills bacteria
  * 0: off
  * 1: on
  
* `SwhSlp`: sleep mode, which gradually changes the temperature in Cool, Heat and Dry mode
  * 0: off
  * 1: on

* `Lig`: turns all indicators and the display on the unit on or off
  * 0: off
  * 1: on

* `SwingLfRig`: controls the swing mode of the horizontal air blades (not available on all units)
  * 0: default
  * `TODO:` figure out which are the other possible values 

* `SwUpDn`: controls the swing mode of the vertical air blades
  * 0: default
  * 1: swing in full range
  * 2: fixed in the upmost position (1/5)
  * 3: fixed in the middle-up position (2/5)
  * 4: fixed in the middle position (3/5)
  * 5: fixed in the middle-low position (4/5)
  * 6: fixed in the lowest position (5/5)
  * 7: swing in the lowest region (5/5)
  * 8: swing in the middle-low region (4/5)
  * 9: swing in the middle region (3/5)
  * 10: swing in the middle-up region (2/5)
  * 11: swing in the upmost region (1/5)

* `Quiet`: controls the Quiet mode which slows down the fan to its most quiet speed. Not available in Dry and Fan mode.
  * 0: off
  * 1: on
  
* `Tur`: sets fan speed to the maximum. Fan speed cannot be changed while active and only available in Dry and Cool mode.
  * 0: off
  * 1: on

* `StHt`: unknown

* `HeatCoolType`: unknown

* `TemRec`: unknown, probably sets the temperature display mode (indoor, outdoor etc.)

* `SvSt`: energy saving mode
  * 0: off
  * 1: on

If the status request succeeds, you should have the following object in the response `pack`:

```json
{
  "t": "dat",
  "mac": "<MAC address>",
  "r": 200,
  "cols": [
    "Pow", 
    "Mod", 
    "SetTem", 
    "WdSpd", 
    "Air", 
    "Blo",
    "Health", 
    "SwhSlp", 
    "Lig", 
    "SwingLfRig", 
    "SwUpDn", 
    "Quiet", 
    "Tur", 
    "StHt", 
    "TemUn", 
    "HeatCoolType", 
    "TemRec", 
    "SvSt"
  ],
  "dat": [1, 1, 25, 1, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0]
}
```

In this object, `cols` defines the parameter names and `dat` defines the values for them.

Since the device won't send you a status update packet when you change a setting using the remote control or the app, you should poll the it periodically.

### Controlling the device

In order to set a parameter of a device, you must send a command packet to it. It's a simple `pack`-type object with the following JSON encoded into it:

```json
{
  "cid": "app",
  "i": 0,
  "pack": "<encrypted, encoded pack>",
  "t": "pack",
  "tcid": "<MAC address>",
  "uid": 0
}
```

`pack`:
```json
{
  "opt": ["TemUn", "SetTem"],
  "p": [0, 27],
  "t": "cmd"
}
```

In this object, `opt` contains the names of the parameters you want to set and `p` contains the values for them. The type of the pack is `cmd`. If the request succeeds, you should have the following response pack:

```json
{
  "t": "pack",
  "i": 0,
  "uid": 0,
  "cid": "<MAC address>",
  "tcid": "",
  "pack": "<encrypted, encoded pack>"
}
```

`pack`:
```json
{
  "t": "res",
  "mac": "<MAC address>",
  "r": 200,
  "opt": ["TemUn", "SetTem"],
  "p": [0, 27],
  "val": [0, 27]
}
```

In this object, `r` is the response code (not sure if there are other values than 200 because the device won't send you anythin if the request fails), `opt` contains the name of the parameters you set, `p` and `val` contains the values for them.

### Scheduling

There is a simple scheduler implementation which can turn on or off your device automatically. New scheduling item can be added via the following packet (thanks to [oroce](https://github.com/oroce) for the details):

```json
{
  "cmd": [
    {
      "mac": [
        "<MAC address>"
      ],
      "opt": [
        "Pow"
      ],
      "p": [
        0
      ]
    }
  ],
  "enable": 0,
  "hr": 20,
  "id": 0,
  "min": 40,
  "name": "5363686564756c65",
  "sec": 0,
  "t": "setT",
  "tz": 1,
  "week": [
    0,
    0,
    1,
    0,
    0,
    1,
    0
  ]
}
```

In this object, `cmd` defines which device you want to address (`mac`), which parameters you want to set (`opt`) and which are the values for them (`p`). `enable` controls the state of this scheduling item, `hr` and `min` is the time, `name` is the name of the item encoded into ASCII bytes in hexadecimal format, `tz` is the time zone (probably an offset value) and `week` defines on which weekdays the device will execute the command, begining with Sunday.

### Synchronizing the time on the device

There is a way to synchronize the internal clock of the device, but at the moment the command packet for that is unknown.

### Remarks

For the sake of simplicity, you can send device control messages to the broadcast address instead of the IP of the device, because the `tcid` field addresses the device properly. With this little trick you can omit storing IP addresses for specific devices.
