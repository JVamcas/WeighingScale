
const val idVendor: Short = 0x12d1
const val idProduct: Short = 0x107d

fun main() {

    // Get the USB services and dump information about them
//    UsbScale.listUsBDevices()

    val device = UsbScale.findDevice(idVendor, idProduct)
    val scale = UsbScale(device!!)
    scale.open()

    try {
        while (true) {
            scale.syncSubmit()
        }
    } finally {
        scale.close()
    }
}
