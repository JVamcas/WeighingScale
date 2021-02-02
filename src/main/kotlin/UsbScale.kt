import java.util.logging.Level
import java.util.logging.Logger
import javax.usb.*
import javax.usb.event.UsbPipeDataEvent
import javax.usb.event.UsbPipeErrorEvent
import javax.usb.event.UsbPipeListener
import kotlin.experimental.or

class UsbScale(private val device: UsbDevice) : UsbPipeListener {
    private var iFace: UsbInterface? = null
    private var usbPipe: UsbPipe? = null
    private val data = ByteArray(6)


    fun open() {
        device.apply {
            val irp = createUsbControlIrp(
                (
                        UsbConst.REQUESTTYPE_DIRECTION_IN
                                or UsbConst.REQUESTTYPE_TYPE_STANDARD
                                or UsbConst.REQUESTTYPE_RECIPIENT_DEVICE),
                UsbConst.REQUEST_GET_CONFIGURATION,
                0.toShort(),
                0.toShort()
            )
            irp?.data = data /*byteArrayOf(6)*/
            syncSubmit(irp)
        }
        val configuration = device.activeUsbConfiguration
        println("Interfaces: ${configuration.usbInterfaces}")
        iFace = configuration.usbInterfaces[2] as UsbInterface
        iFace?.apply {
            claim { true }

            //todo might throw an exception if there is no endpoints
            usbPipe = (usbEndpoints[0] as? UsbEndpoint)!!.usbPipe // there is only 1 endpoint
            usbPipe?.addUsbPipeListener(this@UsbScale)
            usbPipe?.open()
        }
    }

    fun syncSubmit() {
        try {
            usbPipe?.syncSubmit(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun close() {
        usbPipe?.close()
        iFace?.release()
    }

    override fun dataEventOccurred(evt: UsbPipeDataEvent) {

        println(evt.data)
        val data = evt.data.toString(Charsets.UTF_8)
        println("Data received: ${evt.data[0].toChar()}")
    }

    override fun errorEventOccurred(upee: UsbPipeErrorEvent) {
        Logger.getLogger(UsbScale::class.java.name).log(Level.SEVERE, "Scale Error", upee)
    }

    companion object {

        fun listUsBDevices() {
            val services = UsbHostManager.getUsbServices()
            val hub = services.rootUsbHub
            hub.attachedUsbDevices.forEach {
                val desc = it as UsbDevice
                println("Device Description is:\n${desc.usbDeviceDescriptor}")
            }
        }

        fun findDevice(vendorId: Short, productId: Short): UsbDevice? {
            val services = UsbHostManager.getUsbServices()

            services.rootUsbHub.attachedUsbDevices.forEach {
                val device = it as UsbDevice
                val devDesc = device.usbDeviceDescriptor
                if (devDesc.idVendor() == vendorId && devDesc.idProduct() == productId) return device
            }
            return null
        }
    }
}

