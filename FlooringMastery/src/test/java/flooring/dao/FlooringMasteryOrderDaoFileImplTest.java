package flooring.dao;

import flooring.model.Order;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class FlooringMasteryOrderDaoFileImplTest {

    private Order testOrder;
    private FlooringMasteryOrderDao testOrderDao;

    private DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("MMddyyyy");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private String orderAsString;
    private Scanner in;
    private PrintWriter out;

    private final String HEADER = "OrderNumber::CustomerName::State::TaxRate::ProductType::" +
            "Area::CostPerSquareFoot::LaborCostPerSquareFoot::MaterialCost::LaborCost::Tax::Total";
    private final String ORDER_FILE = "src/test/resources/testOrders";
    private final String BACKUP_HEADER = HEADER + "::OrderDate";
    private final String BACKUP_FILE = "src/test/resources/testBackup/backup.txt";
    private final String DELIMITER = "::";

    @BeforeEach
    void setUp() {
        testOrder = new Order(
            LocalDate.parse("12-12-2012",dateFormatter), "testName", "testState",
            new BigDecimal("10.00"), "testType", new BigDecimal("100.00"),
            new BigDecimal("10.0"), new BigDecimal("5.0")
            );
        testOrder.setOrderNumber(1);
        orderAsString = testOrder.getOrderNumber() + DELIMITER +
                testOrder.getCustomerName() + DELIMITER + testOrder.getState() +
                DELIMITER + testOrder.getTaxRate() + DELIMITER +
                testOrder.getProductType() + DELIMITER + testOrder.getArea() +
                DELIMITER + testOrder.getCostPerSquareFoot() + DELIMITER +
                testOrder.getLaborCostPerSquareFoot() + DELIMITER + testOrder.getMaterialCost()
                + DELIMITER + testOrder.getLaborCost() + DELIMITER + testOrder.getTax()
                + DELIMITER + testOrder.getTotal();
        testOrderDao = new FlooringMasteryOrderDaoFileImpl(ORDER_FILE, BACKUP_FILE);
    }

    @AfterEach
    void tearDown() {
        try {
            FileUtils.cleanDirectory(new File("src/test/resources/testOrders"));
        } catch (IOException e) {
            fail("tearDown fails during cleaning directory");
        }
    }

    @Test
    void testUploadOrderCreatesFile() {
        String testFile = "src/test/resources/testOrders" + "/Order_" +
                testOrder.getDate().format(fileFormatter) + ".txt";
        assertFalse(new File(testFile).exists());
        try {
            testOrderDao.uploadOrder(testOrder);
        } catch (FlooringMasteryPersistenceException e) {
            fail("unexpected exception during uploading order");
        }
        assertTrue(new File(testFile).exists());
    }

    @Test
    void testUploadOrderAddsOrderToExistingFile() {
        String testFile = "src/test/resources/testOrders" + "/Order_" +
                testOrder.getDate().format(fileFormatter) + ".txt";
        // write 2 empty lines
        try {
            out = new PrintWriter(new FileWriter(testFile));
            out.println();
            out.flush();
            out.println();
            out.flush();
            out.close();
        } catch (IOException e) {
            fail("Creation of testFile fails");
        }
        // add order to file
        try {
            testOrderDao.uploadOrder(testOrder);
        } catch (FlooringMasteryPersistenceException e) {
            fail("Unexpected exception during uploading order");
        }
        try {
            in = new Scanner(new BufferedReader(new FileReader(testFile)));
        } catch (FileNotFoundException e) {
            fail("Fail was not created");
        }
        // read 2 empty lines
        String empty = in.nextLine();
        assertTrue(empty.isEmpty());
        empty = in.nextLine();
        assertTrue(empty.isEmpty());
        // read order
        String order = in.nextLine();
        assertEquals(orderAsString, order);
        assertFalse(in.hasNextLine());
    }

    @Test
    void testUploadOrderWritesHeaderAndOrderToNewFile() {
        String testFile = "src/test/resources/testOrders" + "/Order_" +
                testOrder.getDate().format(fileFormatter) + ".txt";
        assertFalse(new File(testFile).exists());
        try {
            testOrderDao.uploadOrder(testOrder);
        } catch (FlooringMasteryPersistenceException e) {
            fail("Unexpected exception during uploading order");
        }
        try {
            in = new Scanner(new BufferedReader(new FileReader(testFile)));
        } catch (FileNotFoundException e) {
            fail("Fail was not created");
        }
        String header = in.nextLine();
        String order = in.nextLine();
        assertEquals(HEADER, header);
        assertEquals(orderAsString, order);
    }

    @Test
    void testGetOrdersForWrongDateThrowsException() {
        assertThrows(
                FlooringMasteryPersistenceException.class,
                () -> testOrderDao.getOrdersForDate(LocalDate.parse("12-12-2012", dateFormatter))
        );
    }

    @Test
    void testGetOrdersForCorrectDate() {
        // create file with order
        try {
            testOrderDao.uploadOrder(testOrder);
        } catch (FlooringMasteryPersistenceException e) {
            fail("Unexpected exception during uploading order");
        }
        Map<Integer, Order> orders = null;
        try {
            orders = testOrderDao.getOrdersForDate(testOrder.getDate());
        } catch (FlooringMasteryPersistenceException e) {
            fail("Loading orders fails");
        }
        Order order = orders.get(testOrder.getOrderNumber());
        assertNotNull(order);
        assertEquals(testOrder, order);
        assertEquals(testOrder.getArea(), order.getArea());
        assertEquals(testOrder.getDate(), order.getDate());
        assertEquals(testOrder.getState(), order.getState());
        assertEquals(testOrder.getMaterialCost(), order.getMaterialCost());
        assertEquals(testOrder.getTotal(), order.getTotal());
        assertEquals(testOrder.getCustomerName(), order.getCustomerName());
        assertEquals(1, orders.values().size());
    }

    @Test
    void testUploadModifiedOrders() {
        Order beforeEditing = null;
        Order afterEditing = null;
        try {
            // upload order
            testOrderDao.uploadOrder(testOrder);
            // read uploaded order
            beforeEditing = testOrderDao.getOrdersForDate(testOrder.getDate())
                    .get(testOrder.getOrderNumber());
            // modify order
            testOrder.setArea(new BigDecimal("0"));
            testOrder.setState("newState");
            testOrder.setCustomerName("newName");
            testOrder.setProductType("newType");
            // upload modified order
            Collection<Order> orderCollection = new ArrayList<>();
            orderCollection.add(testOrder);
            testOrderDao.uploadModifiedOrders(orderCollection, testOrder.getDate());
            // read modified order
            afterEditing = testOrderDao.getOrdersForDate(testOrder.getDate())
                    .get(testOrder.getOrderNumber());
        } catch (FlooringMasteryPersistenceException e) {
            fail("Loading or uploading fails");
        }
        assertNotEquals(beforeEditing,  testOrder);
        assertEquals(afterEditing, testOrder);
        assertEquals(testOrder.getCustomerName(), afterEditing.getCustomerName());
        assertEquals(testOrder.getArea(), afterEditing.getArea());
        assertEquals(testOrder.getState(), afterEditing.getState());
        assertEquals(testOrder.getProductType(), afterEditing.getProductType());
    }

    @Test
    void testExportOrders() {
        try {
            // create one file
            testOrderDao.uploadOrder(testOrder);
            // export it
            testOrderDao.exportOrders();
        } catch (FlooringMasteryPersistenceException e) {
            fail("Fails to upload or export data");
        }
        try {
            in = new Scanner(new BufferedReader(new FileReader(BACKUP_FILE)));
        } catch (FileNotFoundException e) {
            fail("Backup file wasn't created");
        }
        assertEquals(BACKUP_HEADER, in.nextLine());
        String expectedLine = orderAsString + DELIMITER + testOrder.getDate().format(dateFormatter);
        assertEquals(expectedLine, in.nextLine());
        assertFalse(in.hasNextLine());
    }

    @Test
    void testExportOrdersRewriteExistingFile() {
        // write to backup file
        try {
            out = new PrintWriter(new FileWriter(BACKUP_FILE));
            out.println();
            out.flush();
            out.println();
            out.flush();
            out.close();
        } catch (IOException e) {
            fail("Fails to create backup file");
        }
        try {
            testOrderDao.uploadOrder(testOrder);
            testOrderDao.exportOrders();
        } catch (FlooringMasteryPersistenceException e) {
            fail("Fails to upload or export data");
        }
        try {
            in = new Scanner(new BufferedReader(new FileReader(BACKUP_FILE)));
        } catch (FileNotFoundException e) {
            fail("Backup file wasn't created");
        }
        assertEquals(BACKUP_HEADER, in.nextLine());
        String expectedLine = orderAsString + DELIMITER + testOrder.getDate().format(dateFormatter);
        assertEquals(expectedLine, in.nextLine());
        assertFalse(in.hasNextLine());
    }
}