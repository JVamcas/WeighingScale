//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.usb.UsbConfiguration;
//import javax.usb.UsbDevice;
//import javax.usb.UsbDeviceDescriptor;
//import javax.usb.UsbEndpoint;
//import javax.usb.UsbException;
//import javax.usb.UsbHostManager;
//import javax.usb.UsbHub;
//import javax.usb.UsbInterface;
//import javax.usb.UsbPipe;
//import javax.usb.UsbServices;
//import javax.usb.event.UsbPipeDataEvent;
//import javax.usb.event.UsbPipeErrorEvent;
//import javax.usb.event.UsbPipeListener;
//
//public class FileClass implements UsbPipeListener {
//
//    private final UsbDevice device;
//    private UsbInterface iface;
//    private UsbPipe pipe;
//
//    private  Short idVendor  = 0x05ba;
//    private  Short idProduct  = 0x000a;
//    private final byte[] data = new byte[6];
//
//    private FileClass(UsbDevice device) {
//        this.device = device;
//    }
//
//    public static void main(String[] args) throws UsbException {
//
//        FileClass scale = FileClass.findScale();
//        scale.open();
//        try {
//            for (boolean i = true; i; i = true) {
//                scale.syncSubmit();
//            }
//        } finally {
//            scale.close();
//        }
//    }
//
//    public static FileClass findScale() throws UsbException {
//        UsbServices services = UsbHostManager.getUsbServices();
//        UsbHub rootHub = services.getRootUsbHub();
////        // Dymo M5 Scale:
////        UsbDevice device = findDevice(rootHub, (short) 0x0922, (short) 0x8003);
////        // Dymo M25 Scale:
////        if (device == null) {
//        UsbDevice device = findDevice(rootHub, FileClass.idVendor, FileClass.idProduct);
////        }
//        if (device == null) {
//            return null;
//        }
//        return new FileClass(device);
//    }
//
//    private static UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
//        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
//            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
//            if (desc.idVendor() == vendorId && desc.idProduct() == productId) {
//                return device;
//            }
//            if (device.isUsbHub()) {
//                device = findDevice((UsbHub) device, vendorId, productId);
//                if (device != null) {
//                    return device;
//                }
//            }
//        }
//        return null;
//    }
//
//    private void open() throws UsbException {
//        UsbConfiguration configuration = device.getActiveUsbConfiguration();
//        iface = configuration.getUsbInterface((byte) 0);
//        // this allows us to steal the lock from the kernel
//        iface.claim(usbInterface -> true);
//        final List<UsbEndpoint> endpoints = iface.getUsbEndpoints();
//        pipe = endpoints.get(0).getUsbPipe(); // there is only 1 endpoint
//        pipe.addUsbPipeListener(this);
//        pipe.open();
//    }
//
//    private void syncSubmit() throws UsbException {
//        pipe.syncSubmit(data);
//    }
//
//    public void close() throws UsbException {
//        pipe.close();
//        iface.release();
//    }
//
//    @Override
//    public void dataEventOccurred(UsbPipeDataEvent upde) {
//
//        boolean empty = data[1] == 2;
//        boolean overweight = data[1] == 6;
//        boolean negative = data[1] == 5;
//        boolean grams = data[2] == 2;
//        int scalingFactor = data[3];
//        int weight = (data[4] & 0xFF) + (data[5] << 8);
//
//        // int phoneWeights[] = new int[5];
//        // int minWeight = 142;
//        //int previous weight=0;
//
//        boolean EggsOnScale = false;
//        int[] EggsWeight = {440, 460};
//        int[] OneEgg = {60, 75};
//        int count = 0;
//        int oldweight = 0;
//
//        //oldweight = weight;
//        if (oldweight == weight) {
//            // do nothing
//        } else {
//            System.out.println("Weight Changes");
//			/* write DB
//
//
//			//if (weight < oldweight){
//			//then something was taken off
//			 *
//			 * remove equivalent amount of eggs from db.fridge.
//
//			//if(db.frdige == 0 eggs){
//				//order eggs
//			}
//
//			if(weight > oldweight){
//				somebody put eggs in the fridge.
//			}
//			*
//			*
//			*
//
//
//		}*/
//            oldweight = weight;
//        }
//
//        if (weight != oldweight) {
//            while (count <= 5000) {
//                count++;
//            }
//            while (count < 5)
//                weight = count++;
//		/*
//		 for(int i=0, i=Length(phoneWeights); i++) { phoneWeights[i] = minweight+i; }
//		 */
//
//            /*
//             * System.out.println(String.format("Weight = %,.1f%s", scaleWeight(weight,
//             * scalingFactor), grams ? "g" : "oz"));
//             */
//
//            System.out.println("My Weight: " + weight);
//		/*if(newweight != oldweight) {
//			oldweight = newweight;
//			write to db shopping list;
//		}*/
//
//            if (EggsWeight[0] <= weight && weight <= EggsWeight[1]) {
//                EggsOnScale = true;
//                System.out.println("6 Eggs on scale");
//                // write one phone to table in db.
//                try {
//                    // create a mysql database connection
//                    String myDriver = "com.mysql.jdbc.Driver";
//                    String myUrl = "jdbc:mysql://localhost:3306/smartfridge?autoReconnect=true&useSSL=false";
//                    Class.forName(myDriver);
//                    Connection conn = DriverManager.getConnection(myUrl, "root", "admin");
//
//                    // the mysql insert statement
//                    String query = " insert into fridge (name, UnitOfSale, ContentsQuantity, department, AverageSellingUnitWeight)"
//                            + " values (?, ?, ?, ?, ?)";
//
//                    // create the mysql insert preparedstatement
//                    PreparedStatement preparedStmt = conn.prepareStatement(query);
//                    preparedStmt.setString(1, "Eggs");
//                    preparedStmt.setInt(2, 1);
//                    preparedStmt.setInt(3, 6);
//                    preparedStmt.setString(4, "Milk, Butter & Eggs");
//                    preparedStmt.setBoolean(5, EggsOnScale);
//
//                    // execute the preparedstatement
//                    preparedStmt.execute();
//
//                } catch (Exception e) {
//                    //e.printStackTrace();
//                }
//            } else {
//			/*if(EggsWeight[0] - OneEgg[0] <= weight && weight <= EggsWeight[1] - OneEgg[1]) {
//				String query =" update fridge set ContentsQuantity = ContentsQuantity -1 where name ='Eggs' and ContentsQuantity > 0 ";
//				//System.out.println(ContentsQuantity + "Eggs On Scale");*/
//                oldweight = weight;
//            }
//        }
//    }
//    //} else{
//    //EggsOnScale = false;
//    //}
//
//    /*
//     * boolean empty = data[1] == 2; boolean overweight = data[1] == 6; boolean
//     * negative = data[1] == 5; boolean grams = data[2] == 2; int scalingFactor =
//     * data[3]; double DozenEggs = 144.0; //double OneEgg = 0.2; int
//     * AverageSellingUnitWeight = (data[4] & 0xFF) + (data[5] << 8);
//     *
//     * if (empty) { System.out.println("EMPTY"); } else if (overweight) {
//     * System.out.println("OVERWEIGHT"); } else if (negative) {
//     * System.out.println("NEGATIVE"); } else if (AverageSellingUnitWeight ==
//     * DozenEggs) { // then we have a dozen eggs in the fridge. // add 6 eggs to
//     * Fridge table in the database.
//     *
//     * try { // create a mysql database connection String myDriver =
//     * "com.mysql.cj.jdbc.Driver"; String myUrl =
//     * "jdbc:mysql://localhost:3306/smartfridge?autoReconnect=true&useSSL=false;";
//     * Class.forName(myDriver); Connection conn = DriverManager.getConnection(myUrl,
//     * "root", "admin");
//     *
//     * // the mysql insert statement String query =
//     * " insert into fridge (name, UnitOfSale, ContentsQuantity, department, AverageSellingUnitWeight)"
//     * + " values (?, ?, ?, ?, ?)";
//     *
//     * // create the mysql insert preparedstatement PreparedStatement preparedStmt =
//     * conn.prepareStatement(query); preparedStmt.setString(1, "Eggs");
//     * preparedStmt.setInt(2, 1); preparedStmt.setInt(3, 6);
//     * preparedStmt.setString(4, "Milk, Butter & Eggs"); preparedStmt.setDouble(5,
//     * DozenEggs);
//     *
//     * // execute the preparedstatement preparedStmt.execute();
//     *
//     * conn.close(); } catch (Exception e) { e.printStackTrace(); } }
//     *
//     * else if (AverageSellingUnitWeight == 0) { // goto TEsco API and order half
//     * dozen eggs. }
//     *
//     * else { // Use String.format since printf causes problems on remote exec
//     * System.out.println(String.format("Weight = %,.1f%s",
//     * scaleWeight(AverageSellingUnitWeight, scalingFactor), grams ? "g" : "oz")); }
//     */
//
//    private double scaleWeight(int weight, int scalingFactor) {
//        return weight * Math.pow(10, scalingFactor);
//    }
//
//    @Override
//    public void errorEventOccurred(UsbPipeErrorEvent upee) {
//        Logger.getLogger(FileClass.class.getName()).log(Level.SEVERE, "Scale Error", upee);
//    }
//}
