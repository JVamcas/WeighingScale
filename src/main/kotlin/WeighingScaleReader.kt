import com.fazecast.jSerialComm.*
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import java.lang.Exception
import java.nio.charset.Charset

class WeighingScaleReader(private val property: SimpleStringProperty) {

    private val comPort: SerialPort = SerialPort.getCommPort("COM3")
    private val stopByte = 0x0d.toByte()

    init {

        //[2400,7,n,1]

        comPort.baudRate = 2400
        /**Need to get this right as your scale might be very slow*/
        comPort.parity = SerialPort.NO_PARITY
        comPort.numStopBits = SerialPort.ONE_STOP_BIT
        comPort.numDataBits = 7
    }

    fun read() {
        comPort.addDataListener(object : SerialPortMessageListenerWithExceptions {
            override fun getListeningEvents() = SerialPort.LISTENING_EVENT_DATA_RECEIVED

            override fun serialEvent(p0: SerialPortEvent) {
                if (p0.eventType == SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
                    val data = p0.receivedData
                    if (data.size == 17) {
                        val weightArray = data.copyOfRange(2, 8)
                        val weight = String(weightArray, Charset.forName("UTF-8")).toInt()
                        Platform.runLater { property.set(weight.toString()) }
                    }
                }
            }

            override fun getMessageDelimiter() = byteArrayOf(stopByte)

            override fun delimiterIndicatesEndOfMessage() = true

            override fun catchException(p0: Exception) {
                stopRead()
                AbstractView.Error.showError(header = "Scale Error.", msg = "Unable to read from scale.")
            }
        })
        comPort.openPort()

    }

    fun stopRead() {
        try {
            comPort.closePort()
            comPort.removeDataListener()
        } catch (e: Exception) {
        }
    }
}


