package flooring.service;

import flooring.dao.FlooringMasteryPersistenceException;
import flooring.model.Order;
import flooring.model.Product;
import flooring.model.Tax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FlooringMasterServiceLayerFileImplTest {

    private FlooringMasteryServiceLayer testService;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");


    @BeforeEach
    void setUp() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.scan(FlooringMasterServiceLayerFileImplTest.class.getPackageName());
        applicationContext.refresh();
        testService = applicationContext.getBean("testService", FlooringMasteryServiceLayer.class);
        try {
            testService.loadData();
        } catch (FlooringMasteryPersistenceException e) {
            fail("Loading data wasn't stubbed");
        }
    }

    @Test
    void testValidateDateWithWrongInputFails() {
        assertThrows(InvalidInputException.class, () -> testService.validateDate("112"));
    }

    @Test
    void testValidateDateWithWrongFormatFails() {
        assertThrows(InvalidInputException.class, () -> testService.validateDate("11/12/2030"));
    }

    @Test
    void testValidateDateFromPastFails() {
        LocalDate date = LocalDate.now();
        date = date.minusDays(10);
        String dateString = date.format(formatter);
        assertThrows(InvalidInputException.class, () -> testService.validateDate(dateString));
    }

    @Test
    void validateDateWithRightFormatInFuture() {
        LocalDate date = LocalDate.now();
        date = date.plusDays(10);
        String dateString = date.format(formatter);
        try {
            assertEquals(date, testService.validateDate(dateString));
        } catch (InvalidInputException e) {
            fail("Unexpected format fail");
        }
    }

    @Test
    void testValidateEmptyNameFails() {
        assertThrows(InvalidInputException.class, () -> testService.validateName(""));
    }

    @Test
    void testValidateNameWithWrongCharactersFails() {
        assertThrows(InvalidInputException.class, () -> testService.validateName("!@#"));
    }

    @Test
    void testValidateNameWithLetterNumberComaPeriodSpace() {
        try {
            testService.validateName("a, 1.");
        } catch (InvalidInputException e) {
            fail("Valid name should not throw exception");
        }
    }

    @Test
    void testValidateNotExistingStateFails() {
        assertThrows(InvalidInputException.class, () -> testService.validateType("unknown"));
    }

    @Test
    void testValidateExistingState() throws FlooringMasteryPersistenceException {
        Tax tax = null;
        try {
            tax = testService.validateState("state");
        } catch (InvalidInputException e) {
            fail("Valid state should not throw exception");
        }
        assertEquals("state", tax.getStateName());
        assertEquals(new BigDecimal("10.0"), tax.getTaxRate());
    }

    @Test
    void testValidateNotExistingTypeThrowsException() {
        assertThrows(InvalidInputException.class, () -> testService.validateType("unknown"));
    }

    @Test
    void testValidateExistingType() {
        Product product = null;
        try {
            product = testService.validateType("product");
        } catch (InvalidInputException e) {
            fail("Valid product type should not throw exception");
        }
        assertEquals("product", product.getProductType());
        assertEquals(new BigDecimal("5.00"), product.getCostPerSquareFoot());
        assertEquals(new BigDecimal("15.00"), product.getLaborCostPerSquareFoot());
    }

    @Test
    void testCreateOrder() {
        Product product = new Product(new String[] {"type", "5.55", "7.77"});
        Tax tax = new Tax(new String[] {"s", "state", "5.50"});
        LocalDate date = LocalDate.parse("12-12-2000", formatter);
        String name = "name";
        BigDecimal area = new BigDecimal("1000.55");
        Order order = testService.createOrder(date, name, tax, product, area);
        assertEquals(product.getProductType(), order.getProductType());
        assertEquals(product.getCostPerSquareFoot(), order.getCostPerSquareFoot());
        assertEquals(product.getLaborCostPerSquareFoot(), order.getLaborCostPerSquareFoot());
        assertEquals(tax.getStateName(), order.getState());
        assertEquals(tax.getTaxRate(), order.getTaxRate());
        assertEquals(name, order.getCustomerName());
        assertEquals(date, order.getDate());
    }

    @Test
    void testSaveOrder() {
        // create order
        Product product = new Product(new String[] {"type", "5.55", "7.77"});
        Tax tax = new Tax(new String[] {"s", "state", "5.50"});
        LocalDate date = LocalDate.parse("12-12-2000", formatter);
        String name = "name";
        BigDecimal area = new BigDecimal("1000.55");
        Order order = testService.createOrder(date, name, tax, product, area);
        // test saving
        try {
            testService.saveOrder(order);
        } catch (FlooringMasteryPersistenceException e) {
            fail("Saving order was not stubbed");
        }
        assertEquals(10, order.getOrderNumber());
        try {
            testService.saveOrder(order);
        } catch (FlooringMasteryPersistenceException e) {
            fail("Saving order was not stubbed");
        }
        assertEquals(11, order.getOrderNumber());
    }

    @Test
    void testGetAllProducts() {
        Collection<Product> products = testService.getAllProducts();
        assertEquals(1, products.size());
        Product product = new Product(new String[] {"product", "5.00", "15.00"});
        assertTrue(products.contains(product));
    }

    @Test
    void testParseDateWithCorrectFormat() {
        LocalDate date;
        String dateString = "12-12-2000";
        try {
            date = testService.parseDate(dateString);
            assertEquals(LocalDate.parse(dateString, formatter), date);
        } catch (InvalidInputException e) {
            fail("Correct date format should not throw exception");
        }
    }

    @Test
    void testParseDateWithWrongFormat() {
        String dateString = "12/12/2000";
        try {
            testService.parseDate(dateString);
            fail("Wrong date format should throw exception");
        } catch (InvalidInputException e) {
        }
    }

    @Test
    void testParseDateWithWrongData() {
        String dateString = "wrong";
        try {
            testService.parseDate(dateString);
            fail("Data that cannot be parsed as date should throw exception");
        } catch (InvalidInputException e) {
        }
    }

    @Test
    void testGetOrdersForNotExistingDate() {
        LocalDate date = LocalDate.parse("12-12-2000", formatter);
        assertThrows(ItemNotFoundException.class, () -> testService.getOrders(date));
    }

    @Test
    void testGetOrdersAfterCreatingOrder() {
        // create order
        Product product = new Product(new String[]{"type", "5.55", "7.77"});
        Tax tax = new Tax(new String[]{"s", "state", "5.50"});
        LocalDate date = LocalDate.parse("12-12-2000", formatter);
        String name = "name";
        BigDecimal area = new BigDecimal("1000.55");
        Order order = testService.createOrder(date, name, tax, product, area);
        // check before saving
        assertThrows(ItemNotFoundException.class, () -> testService.getOrders(date));
        // check after saving
        try {
            testService.saveOrder(order);
        } catch (FlooringMasteryPersistenceException e) {
            fail("Saving order was not stubbed");
        }
        try {
            Collection<Order> orders = testService.getOrders(date);
            assertTrue(orders.contains(order));
        } catch (ItemNotFoundException e) {
            fail("Order was created and should not throw exception");
        }
    }

    @Test
    void testGetOrderMapForWrongDate() {
        // create order
        Product product = new Product(new String[]{"type", "5.55", "7.77"});
        Tax tax = new Tax(new String[]{"s", "state", "5.50"});
        LocalDate date = LocalDate.parse("12-12-2000", formatter);
        String name = "name";
        BigDecimal area = new BigDecimal("1000.55");
        Order order = testService.createOrder(date, name, tax, product, area);
        assertThrows(ItemNotFoundException.class, () -> testService.getOrders(date));
        // save order
        try {
            testService.saveOrder(order);
        } catch (FlooringMasteryPersistenceException e) {
            fail("Saving order was not stubbed");
        }
        // check for another date
        assertThrows(
                ItemNotFoundException.class,
                () -> testService.getOrderMap(order.getDate().plusDays(1), order.getOrderNumber()));
    }

    @Test
    void testGetOrderMapForWrongNumber() {
        // create order
        Product product = new Product(new String[]{"type", "5.55", "7.77"});
        Tax tax = new Tax(new String[]{"s", "state", "5.50"});
        LocalDate date = LocalDate.parse("12-12-2000", formatter);
        String name = "name";
        BigDecimal area = new BigDecimal("1000.55");
        Order order = testService.createOrder(date, name, tax, product, area);
        assertThrows(ItemNotFoundException.class, () -> testService.getOrders(date));
        // save order
        try {
            testService.saveOrder(order);
        } catch (FlooringMasteryPersistenceException e) {
            fail("Saving order was not stubbed");
        }
        // check for another number
        assertThrows(
                ItemNotFoundException.class,
                () -> testService.getOrderMap(order.getDate(), order.getOrderNumber()+ 1));
    }

     @Test
    void testGetOrderMapForCorrectDateAndNumber() {
        // create order
        Product product = new Product(new String[]{"type", "5.55", "7.77"});
        Tax tax = new Tax(new String[]{"s", "state", "5.50"});
        LocalDate date = LocalDate.parse("12-12-2000", formatter);
        String name = "name";
        BigDecimal area = new BigDecimal("1000.55");
        Order order = testService.createOrder(date, name, tax, product, area);
        assertThrows(ItemNotFoundException.class, () -> testService.getOrders(date));
        // save order
        try {
            testService.saveOrder(order);
        } catch (FlooringMasteryPersistenceException e) {
            fail("Saving order was not stubbed");
        }
        // check for the same date and number
        Map<Integer, Order> orderMap = new HashMap<>();
        try {
            orderMap = testService.getOrderMap(order.getDate(), order.getOrderNumber());
        } catch (ItemNotFoundException e) {
            fail("Correct date and number should not throw exception");
        }
        assertNotNull(orderMap.get(order.getOrderNumber()));
        assertEquals(order, orderMap.get(order.getOrderNumber()));
    }

    @Test
    void updateOrder() {
        // create 2 products, update first with second
        Product product = new Product(new String[]{"type", "5.55", "7.77"});
        Product newProduct = new Product(new String[]{"newType", "12.34", "3.45"});
        Tax tax = new Tax(new String[]{"s", "state", "5.50"});
        Tax newTax = new Tax(new String[]{"as", "newState", "4.50"});
        LocalDate date = LocalDate.parse("12-12-2000", formatter);
        String name = "name";
        String newName = "newName";
        BigDecimal area = new BigDecimal("1000.55");
        BigDecimal newArea = new BigDecimal("1200.50");
        Order order = testService.createOrder(date, name, tax, product, area);
        testService.updateOrder(order, newName, newTax, newProduct, newArea);
        assertEquals(newName, order.getCustomerName());
        assertNotEquals(name, order.getCustomerName());
        assertNotEquals(area, order.getArea());
        assertEquals(newArea, order.getArea());
        assertNotEquals(tax.getStateName(), order.getState());
        assertEquals(newTax.getStateName(), order.getState());
        assertNotEquals(tax.getTaxRate(), order.getTaxRate());
        assertEquals(newTax.getTaxRate(), order.getTaxRate());
        assertNotEquals(product.getProductType(), order.getProductType());
        assertEquals(newProduct.getProductType(), order.getProductType());
        assertNotEquals(product.getCostPerSquareFoot(), order.getCostPerSquareFoot());
        assertEquals(newProduct.getCostPerSquareFoot(), order.getCostPerSquareFoot());
        assertNotEquals(product.getLaborCostPerSquareFoot(), order.getLaborCostPerSquareFoot());
        assertEquals(newProduct.getLaborCostPerSquareFoot(), order.getLaborCostPerSquareFoot());
    }

    @Test
    void testDeleteOrders() {
        // create 2 orders
        LocalDate date = LocalDate.parse("12-12-2000", formatter);
        Order firstOrder = new Order(
                date, "firstName", "firstState", new BigDecimal("5.00"),
                "firstType", new BigDecimal("1000"),
                new BigDecimal("2.50"), new BigDecimal("3.00")
        );
        firstOrder.setDate(date);
        firstOrder.setOrderNumber(1);
        Order secondOrder = new Order(
                date, "secondName", "secondState", new BigDecimal("4.00"),
                "secondType", new BigDecimal("600"),
                new BigDecimal("2.60"), new BigDecimal("3.24")
        );
        secondOrder.setDate(date);
        secondOrder.setOrderNumber(2);
        Map<Integer, Order> orderMap = new HashMap<>();
        orderMap.put(1, firstOrder);
        orderMap.put(2, secondOrder);
        // delete first
        try {
            testService.deleteOrder(orderMap, 1, date);
        } catch (FlooringMasteryPersistenceException e) {
            fail("OrderDao was not stubbed");
        }
        assertThrows(ItemNotFoundException.class, () -> testService.getOrderMap(date, 1));
        // check that second wasn't deleted
        try {
            Map<Integer, Order> mapAfterDeleting = testService.getOrderMap(date, 2);
            assertNotNull(mapAfterDeleting.get(2));
            assertEquals(secondOrder, mapAfterDeleting.get(2));
        } catch (ItemNotFoundException e) {
            fail("Not removed order should not throw exception");
        }
    }

    @Test
    void editOrder() {
        // create order
        LocalDate date = LocalDate.parse("12-12-2000", formatter);
        Order firstOrder = new Order(
                date, "firstName", "firstState", new BigDecimal("5.00"),
                "firstType", new BigDecimal("1000"),
                new BigDecimal("2.50"), new BigDecimal("3.00")
        );
        firstOrder.setDate(date);
        firstOrder.setOrderNumber(1);
        // create new order to update first one
        Order secondOrder = new Order(
                date, "secondName", "secondState", new BigDecimal("4.00"),
                "secondType", new BigDecimal("600"),
                new BigDecimal("2.60"), new BigDecimal("3.24")
        );
        secondOrder.setDate(date);
        secondOrder.setOrderNumber(1);
        Map<Integer, Order> orderMap = new HashMap<>();
        orderMap.put(1, firstOrder);
        // edit first order
        try {
            testService.editOrder(orderMap, 1, secondOrder, date);
        } catch (FlooringMasteryPersistenceException e) {
            fail("OrderDao was not stubbed");
        }
        // check that after editing it is the second one
        try {
            Map<Integer, Order> mapAfterEditing = testService.getOrderMap(date, 1);
            assertNotEquals(firstOrder, mapAfterEditing.get(1));
            assertEquals(secondOrder, mapAfterEditing.get(1));
        } catch (ItemNotFoundException e) {
            fail("Existing order should not throw exception");
        }
    }

}