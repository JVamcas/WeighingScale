import java.io.UnsupportedEncodingException
import javax.usb.UsbDevice
import javax.usb.UsbException
import javax.usb.UsbHostManager
import javax.usb.UsbHub
import javax.usb.UsbConst

import kotlin.experimental.or

const val ID_VENDOR: Short = 0x05ba
const val ID_PRODUCT: Short = 0x000a


fun main() {

    // Get the USB services and dump information about them
    val scale = WeighingScaleReader()


    // Dump the root USB hub
//    listUsBDevices(services.rootUsbHub)

//    val device = findDevice(services.rootUsbHub, ID_VENDOR, ID_PRODUCT)
//    val irp = device?.createUsbControlIrp(
//        (UsbConst.REQUESTTYPE_DIRECTION_IN
//                or UsbConst.REQUESTTYPE_TYPE_STANDARD
//                or UsbConst.REQUESTTYPE_RECIPIENT_DEVICE),
//        UsbConst.REQUEST_GET_CONFIGURATION,
//        0.toShort(),
//        0.toShort()
//    )
//    irp?.data = byteArrayOf()
//    device?.syncSubmit(irp)
//
//    device?.apply {
//
//        val config = device.activeUsbConfiguration
//        println(config.usbInterfaces)
//        val iface = config.getUsbInterface(3.toByte())
//
//        //claim this device by force
//        iface.claim { true }
////        val endpoint = iface.getUsbEndpoint()
//
//        try {
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            iface.release()
//        }
//    }
}

fun listUsBDevices(hub: UsbHub) {
    hub.attachedUsbDevices.forEach {
        val desc = it as UsbDevice
        println("Device Description is:\n${desc.usbDeviceDescriptor}")
    }
}

fun findDevice(hub: UsbHub, vendorId: Short, productId: Short): UsbDevice? {

    hub.attachedUsbDevices.forEach {
        val device = it as UsbDevice
        val devDesc = device.usbDeviceDescriptor
        if (devDesc.idVendor() == vendorId && devDesc.idProduct() == productId) return device
    }

    return null
}

/**
 * Dumps the name of the specified device to stdout.
 *
 * @param device
 * The USB device.
 * @throws UnsupportedEncodingException
 * When string descriptor could not be parsed.
 * @throws UsbException
 * When string descriptor could not be read.
 */
@Throws(UnsupportedEncodingException::class, UsbException::class)
private fun dumpName(device: UsbDevice?) {
    // Read the string descriptor indices from the device descriptor.
    // If they are missing then ignore the device.
    val desc = device!!.usbDeviceDescriptor
    val iManufacturer = desc.iManufacturer()
    val iProduct = desc.iProduct()
    if (iManufacturer.toInt() == 0 || iProduct.toInt() == 0) return

    // Dump the device name
    println(
        device.getString(iManufacturer) + " "
                + device.getString(iProduct)
    )
}

/**
 * Processes the specified USB device.
 *
 * @param device
 * The USB device to process.
 */
private fun processDevice(device: UsbDevice) {
    // When device is a hub then process all child devices
    if (device.isUsbHub) {
        val hub = device as? UsbHub

        hub?.attachedUsbDevices?.forEach {
            processDevice(it as UsbDevice)
        }

    } else {
        try {
            dumpName(device)
        } catch (e: Exception) {
            // On Linux this can fail because user has no write permission
            // on the USB device file. On Windows it can fail because
            // no libusb device driver is installed for the device
            System.err.println("Ignoring problematic device: $e")
        }
    }
}
