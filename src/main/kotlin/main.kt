import javax.usb.UsbHostManager

fun main() {

    // Get the USB services and dump information about them
    val services = UsbHostManager.getUsbServices()
    val hub = services.rootUsbHub
//    WeighingScaleReader.listUsBDevices(hub)


//    val device = UsbScale.findDevice(idVendor, idProduct)
//    val scale = UsbScale(device!!)
    val scale = WeighingScaleReader()
    scale.readDevice()
//
//    try {
//        while (true) {
//            scale.syncSubmit()
//        }
//    } finally {
//        scale.close()
//    }
}
