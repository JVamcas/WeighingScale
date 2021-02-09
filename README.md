# WeighingScale  - using Kotlin/ Java

This program makes use of [this api](https://fazecast.github.io/jSerialComm/) to read an old weighing scale (Masmattic Scale) at our factory.

The scale is connected to the computer via a USB-Serial Converter


## How to use it:

1. Install the correct drivers for the USB-Serial Adapter
2. A virtual serial com port (e.g. COM3) will be created on the computer, use that for connecting to the scale
3. Make sure the scale configuration is correct i.e the:
  * baudrate - very important, you might get garbage data if you get this wrong
  * number of stop bits
  * data bits
  * parity bits
  
  
** For this scale the serial data format configuration was [2400, 8, 2, 1]
