import javafx.beans.property.SimpleStringProperty
import javax.usb.*
import javax.usb.event.UsbPipeDataEvent
import javax.usb.event.UsbPipeErrorEvent
import javax.usb.event.UsbPipeListener

class WeighingScaleReader(property: SimpleStringProperty? = null) {

    private val iDVendor: Short = 0x067b
    private val iDProduct: Short = 0x2303

    var device: UsbDevice? = null
    private var iFace: UsbInterface? = null
    private lateinit var usbPipe: UsbPipe
    private val data = ByteArray(8)

    init {
        val services = UsbHostManager.getUsbServices()
        val hub = services.rootUsbHub
        device = findDevice(hub, iDVendor, iDProduct)//1. find the usb device
        device?.apply {
            val config = activeUsbConfiguration
            println("Interfaces: ${config.usbInterfaces}")
            iFace = config.usbInterfaces[0] as UsbInterface //2. obtain device interface
        }
    }

    fun readDevice() {
        iFace?.apply {
            println("Active $isActive")
            println("Claimed $isClaimed")
            println("Active interfaces $activeSetting")
            claim { true } //3. claim the interface
            val endpoint = usbEndpoints[0] as UsbEndpoint
            usbPipe = endpoint.usbPipe //4. get the UsbPipe

            try {
                usbPipe.open() //5. open the pipe and attach listener
                usbPipe.addUsbPipeListener(object : UsbPipeListener {
                    override fun errorEventOccurred(event: UsbPipeErrorEvent) {
                        val error = event.usbException
                        println(error.message)
                        close()
                    }

                    override fun dataEventOccurred(event: UsbPipeDataEvent) {
                        val data = String(event.data)
                        println("Data: $data")
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                close()
            }
        }

        try {
            while (true) {//can have an atomic variable here to listen for close instructions
//                syncSubmit()
                usbPipe.asyncSubmit(data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
    }

//    fun syncSubmit() {
//        try {
//            usbPipe.asyncSubmit(data)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    fun close() {
        usbPipe.close()
        iFace?.release()
    }

    companion object {
        fun listUsBDevices(hub: UsbHub) {
            hub.attachedUsbDevices.forEach {
                if (it is UsbHub)
                    listUsBDevices(it)
                else {
                    val desc = it as UsbDevice
                    println(desc.usbDeviceDescriptor)
                }
            }
        }

        private fun findDevice(hub: UsbHub, vendorId: Short, productId: Short): UsbDevice? {
            hub.attachedUsbDevices.forEach {
                if (it is UsbHub)
                    return findDevice(it, vendorId, productId)

                val device = it as UsbDevice
                val devDesc = device.usbDeviceDescriptor
                if (devDesc.idVendor() == vendorId && devDesc.idProduct() == productId) return device
            }
            return null
        }
    }
}

