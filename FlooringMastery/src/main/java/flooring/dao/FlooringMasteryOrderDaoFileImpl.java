package flooring.dao;

import flooring.model.Order;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class FlooringMasteryOrderDaoFileImpl implements FlooringMasteryOrderDao {

    private final String DELIMITER = "::";

    // Partial path for persistent storage
    private final String ORDER_FILE_PATH;

    // Path for orders' export
    private final String ORDER_BACKUP_PATH;

    // file to store lastOrderNumber
    private final String ORDER_NUMBER_FILE;

    // Formatter for persistent storage
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyyy");

    // Formatter for export
    private final DateTimeFormatter backupFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    // Header for persistent storage
    private final String HEADER = "OrderNumber::CustomerName::State::TaxRate::ProductType::" +
            "Area::CostPerSquareFoot::LaborCostPerSquareFoot::MaterialCost::LaborCost::Tax::Total";

    // Header for export
    private final String BACKUP_HEADER = HEADER + "::OrderDate";

    public FlooringMasteryOrderDaoFileImpl() {
        ORDER_FILE_PATH = "Orders";
        ORDER_BACKUP_PATH = "Backup/DataExport.txt";
        ORDER_NUMBER_FILE = "Data/OrderNumber.txt";
    }

    public FlooringMasteryOrderDaoFileImpl(String orderFile, String backupFile, String numberFile) {
        ORDER_FILE_PATH = orderFile;
        ORDER_BACKUP_PATH = backupFile;
        ORDER_NUMBER_FILE = numberFile;
    }

    /**
     * Checks if file for this date exists, if not - creates file and writes HEADER.
     * Adds given order to this file.
     * @param order Order object to upload
     * @throws FlooringMasteryPersistenceException when IOException occurs
     */
    @Override
    public void uploadOrder(Order order) throws FlooringMasteryPersistenceException {
        PrintWriter out;
        String fileName = ORDER_FILE_PATH + "/Order_" + order.getDate().format(formatter) + ".txt";
        try {
            // check if file exists
            new FileReader(fileName);
        } catch (FileNotFoundException e) {
            try {
                // create file and write header
                out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
                out.println(HEADER);
                out.flush();
                out.close();
            } catch (IOException err) {
                throw new FlooringMasteryPersistenceException("Cannot upload data");
            }
        }
        // add order info to the file
        try {
            out = new PrintWriter(new FileWriter(fileName, true));
        } catch (IOException e) {
            throw new FlooringMasteryPersistenceException("Cannot upload order data");
        }
        out.println(marshallData(order));
        out.flush();
        out.close();
    }

    /**
     * If file with orders for this date exists reads this file line by line,
     * create Order object for each line and puts this object to orderMap as
     * a value with orderNumber as a key
     * @param date LocalDate object
     * @return Map with orderNumbers as Keys and Order objects as values
     * @throws FlooringMasteryPersistenceException when file for given date doesn't exist
     */
    @Override
    public Map<Integer, Order> getOrdersForDate(LocalDate date) throws FlooringMasteryPersistenceException {
        Scanner in;
        String fileName = ORDER_FILE_PATH + "/Order_" + date.format(formatter) + ".txt";
        try {
            // check if file exists
            in = new Scanner(new BufferedReader(new FileReader(fileName)));
        } catch (FileNotFoundException e) {
            throw new FlooringMasteryPersistenceException("Cannot load data");
        }
        Map<Integer, Order> orderMap = new HashMap<>();
        // read the header
        in.nextLine();
        String currentLine;
        Order currentOrder;
        while (in.hasNextLine()) {
            currentLine = in.nextLine();
            currentOrder = unmarshallData(currentLine);
            currentOrder.setDate(date);
            orderMap.put(currentOrder.getOrderNumber(), currentOrder);
        }
        return orderMap;
    }

    /**
     * Iterates through Collection of Order objects and writes them
     * to the corresponding file
     * @param orders Collection of orders to upload
     * @param date LocalDate object associated with orderDate
     * @throws FlooringMasteryPersistenceException in case of IOException
     */
    @Override
    public void uploadModifiedOrders(Collection<Order> orders, LocalDate date) throws FlooringMasteryPersistenceException {
        PrintWriter out;
        String fileName = ORDER_FILE_PATH + "/Order_" + date.format(formatter) + ".txt";
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        } catch (IOException e) {
            throw new FlooringMasteryPersistenceException("Failed to upload data");
        }
        out.println(HEADER);
        out.flush();
        orders.stream()
               .forEach((order) -> {
                            out.println(marshallData(order));
                            out.flush();
                        }
                        );
        out.close();
    }

    /**
     * Iterates through all files with orders in ORDER_FILE_PATH, reads
     * them line by line and writes each line to the backup file
     * (ORDER_BACKUP_PATH)
     * @throws FlooringMasteryPersistenceException when loading or uploading fails
     */
    @Override
    public void exportOrders() throws FlooringMasteryPersistenceException {
        PrintWriter out;
        Scanner in;
        // create backup file and writes header
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(ORDER_BACKUP_PATH)));
        } catch (IOException e) {
            throw new FlooringMasteryPersistenceException("Cannot upload data");
        }
        out.println(BACKUP_HEADER);
        out.flush();
        // get array of order files
        File f = new File(ORDER_FILE_PATH);
        String[] fileNames = f.list();
        if (fileNames == null) {
            return;
        }
        LocalDate date;
        // iterate through order files
        for (String fileName : fileNames) {
            try {
                in = new Scanner(new BufferedReader(new FileReader(ORDER_FILE_PATH + "/" +fileName)));
            } catch (FileNotFoundException e) {
                throw new FlooringMasteryPersistenceException("Cannot load data");
            }
            // read header
            in.nextLine();
            String currentLine;
            date = LocalDate.parse(fileName.substring(6, 14), formatter);
            while (in.hasNextLine()) {
                currentLine = in.nextLine();
                out.println(currentLine + DELIMITER + date.format(backupFormatter));
                out.flush();
            }
        }
        out.close();
    }

    /**
     * Load orderNumber from file or return 1
     * @return saved orderNumber or 1
     */
    @Override
    public int loadOrderNumber() {
        Scanner in;
        try {
            in = new Scanner(new BufferedReader(new FileReader(ORDER_NUMBER_FILE)));
            return in.nextInt();
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * Upload lastOrderNumber to persistent file
     * @throws FlooringMasteryPersistenceException if uploading fails
     */
    @Override
    public void uploadOrderNumber(int orderNumber) throws FlooringMasteryPersistenceException {
        PrintWriter out;
        try {
            out = new PrintWriter(new FileWriter(ORDER_NUMBER_FILE));
            out.println(orderNumber);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new FlooringMasteryPersistenceException("Cannot upload data");
        }
    }
    /**
     * Converts Order object to String
     * @param order Order object that should be converted to String
     * @return String with order information
     */
    private String marshallData(Order order) {
        return order.getOrderNumber() + DELIMITER + order.getCustomerName() +
                DELIMITER + order.getState() + DELIMITER + order.getTaxRate() +
                DELIMITER + order.getProductType() + DELIMITER + order.getArea() +
                DELIMITER + order.getCostPerSquareFoot() + DELIMITER +
                order.getLaborCostPerSquareFoot() + DELIMITER + order.getMaterialCost() +
                DELIMITER + order.getLaborCost() + DELIMITER + order.getTax() +
                DELIMITER + order.getTotal();
    }

    /**
     * Creates Order object from the given String
     * @param orderAsString String with order information
     * @return Order object
     */
    private Order unmarshallData(String orderAsString) {
        String[] orderArray = orderAsString.split(DELIMITER);
        Order order = new Order();
        order.setOrderNumber(Integer.parseInt(orderArray[0]));
        order.setCustomerName(orderArray[1]);
        order.setState(orderArray[2]);
        order.setTaxRate(new BigDecimal(orderArray[3]));
        order.setProductType(orderArray[4]);
        order.setArea(new BigDecimal(orderArray[5]));
        order.setCostPerSquareFoot(new BigDecimal(orderArray[6]));
        order.setLaborCostPerSquareFoot(new BigDecimal(orderArray[7]));
        return order;
    }

}
