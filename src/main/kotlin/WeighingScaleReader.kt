import javafx.beans.property.SimpleStringProperty
import javax.usb.*
import javax.usb.event.UsbPipeDataEvent
import javax.usb.event.UsbPipeErrorEvent
import javax.usb.event.UsbPipeListener
import kotlin.experimental.or

class WeighingScaleReader(property: SimpleStringProperty? = null) {


    private val idVendor: Short = 0x05ba
    private val idProduct: Short = 0x000a
    var device: UsbDevice? = null
    private var iFace: UsbInterface? = null

    init {
        val services = UsbHostManager.getUsbServices()
        device = findDevice(services.rootUsbHub, idVendor, idProduct)
        device?.apply {
            val irp = createUsbControlIrp(
                (
                        UsbConst.REQUESTTYPE_DIRECTION_IN
                                or UsbConst.REQUESTTYPE_TYPE_STANDARD
                                or UsbConst.REQUESTTYPE_RECIPIENT_DEVICE),
                UsbConst.REQUEST_GET_CONFIGURATION,
                0.toShort(),
                0.toShort()
            )
            irp?.data = byteArrayOf()
            syncSubmit(irp)

            val config = activeUsbConfiguration
            println("Interfaces: ${config.usbInterfaces}")
            //need this usb interface value
            iFace = config.getUsbInterface(3.toByte())
            readDevice()
        }
    }

    private fun readDevice() {
        iFace?.apply {
            claim { true } //1. claim the interface
            val endpoint = getUsbEndpoint(0x83.toByte())
            val pipe = endpoint.usbPipe //2 get the usbpipe

            try {
                pipe.open()
                pipe.addUsbPipeListener(object : UsbPipeListener{
                    override fun errorEventOccurred(event: UsbPipeErrorEvent) {
                        val error = event.usbException
                        println(error.message)
                        pipe.close()
                        iFace?.release()
                    }

                    override fun dataEventOccurred(event: UsbPipeDataEvent) {
                        val data = String(event.data)
                        println("Data: $data")
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
//                pipe.close()
//                iFace?.release()
            }
        }
    }


    fun listUsBDevices(hub: UsbHub) {
        hub.attachedUsbDevices.forEach {
            val desc = it as UsbDevice
            println("Device Description is:\n${desc.usbDeviceDescriptor}")
        }
    }

    private fun findDevice(hub: UsbHub, vendorId: Short, productId: Short): UsbDevice? {

        hub.attachedUsbDevices.forEach {
            val device = it as UsbDevice
            val devDesc = device.usbDeviceDescriptor
            if (devDesc.idVendor() == vendorId && devDesc.idProduct() == productId) return device
        }

        return null
    }
}

