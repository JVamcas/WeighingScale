import gnu.io.NRSerialPort
import gnu.io.SerialPortEvent
import gnu.io.SerialPortEventListener
import javafx.beans.property.SimpleStringProperty
import java.io.DataInputStream
import java.io.DataOutputStream


fun main() {


    val property = SimpleStringProperty()
    WeighingScaleReader(property).read()
}
