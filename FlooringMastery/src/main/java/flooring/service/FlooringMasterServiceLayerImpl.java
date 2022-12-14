package flooring.service;

import flooring.dao.FlooringMasteryOrderDao;
import flooring.dao.FlooringMasteryPersistenceException;
import flooring.dao.FlooringMasteryProductDao;
import flooring.dao.FlooringMasteryTaxDao;
import flooring.model.Order;
import flooring.model.Product;
import flooring.model.Tax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Map;

@Component
public class FlooringMasterServiceLayerImpl implements FlooringMasteryServiceLayer {

    @Autowired
    private FlooringMasteryTaxDao taxDao;
    @Autowired
    private FlooringMasteryProductDao productDao;
    @Autowired
    private FlooringMasteryOrderDao orderDao;

    private int lastOrderNumber;
    private final int SCALE = 2;
    private final RoundingMode MODE = RoundingMode.HALF_UP;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    /**
     * Loads taxes, products and lastOrderNumber
     * @throws FlooringMasteryPersistenceException if loading fails
     */
    @Override
    public void loadData() throws FlooringMasteryPersistenceException {
        taxDao.loadTaxes();
        productDao.loadProducts();
        loadLastOrderNumber();
    }

    /**
     * Validates that given String can be converted to LocalDate
     * with given formatter and is a date in the future
     * @param date String supposed to be converted to LocalDate
     * @return LocalDate
     * @throws InvalidInputException if validation fails
     */
    @Override
    public LocalDate validateDate(String date) throws InvalidInputException {
        LocalDate today = LocalDate.now();
        LocalDate orderDate;
        try {
            orderDate = LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Date should be in the future in the format 'MM-dd-yyyy'");
        }
        if (today.isAfter(orderDate)) {
            throw new InvalidInputException("Date should be in the future in the format 'MM-dd-yyyy'");
        }
        return orderDate;
    }

    /**
     * Validates that given name is not empty and consists of
     * letters, numbers, spaces, comas and periods only
     * @param name String supposed to be customer name
     * @throws InvalidInputException if validation fails
     */
    @Override
    public void validateName(String name) throws InvalidInputException {
        if (name.trim().isEmpty() || !name.matches("[A-Za-z0-9\\.\\, ]+")) {
            throw new InvalidInputException(
                    "Name may not be blank and is limited to letters, numbers, periods and comas"
            );
        }
    }

    /**
     * Validates that given state can be found among loaded taxes
     * @param state String supposed to be state
     * @return Tax object for given state
     * @throws InvalidInputException if tax for given state doesn't exist
     */
    @Override
    public Tax validateState(String state) throws InvalidInputException {
        Tax tax = taxDao.getTax(state.toLowerCase());
        if (tax == null) {
            throw new InvalidInputException("Invalid state name");
        }
        return tax;
    }

    /**
     * Validates that given productType can be found among loaded products
     * @param type String supposed to be productType
     * @return Product object
     * @throws InvalidInputException if product of given type was not found
     */
    @Override
    public Product validateType(String type) throws InvalidInputException {
        Product product = productDao.getProduct(type.toLowerCase());
        if (product == null) {
            throw new InvalidInputException("Invalid product type");
        }
        return product;
    }

    /**
     * Creates Order object
     * @param date order date as LocalDate
     * @param customerName customer name as String
     * @param tax Tax object
     * @param product Product object
     * @param area area as BigDecimal
     * @return Order object
     */
    @Override
    public Order createOrder(LocalDate date, String customerName, Tax tax, Product product, BigDecimal area) {
        Order order = new Order(
                date, customerName, tax.getStateName(),
                tax.getTaxRate(), product.getProductType(), area,
                product.getCostPerSquareFoot(), product.getLaborCostPerSquareFoot()
        );
        return order;
    }

    /**
     * Generates and sets orderNumber. Saves given order to persistent storage
     * @param order Order object to save
     * @throws FlooringMasteryPersistenceException if saving fails
     */
    @Override
    public void saveOrder(Order order) throws FlooringMasteryPersistenceException {
        int orderNumber = generateOrderNumber();
        order.setOrderNumber(orderNumber);
        orderDao.uploadOrder(order);
    }

    /**
     * Gets collection of all available products
     * @return Collection of Product objects
     */
    @Override
    public Collection<Product> getAllProducts() {
        return productDao.getAllProducts();
    }

    @Override
    public BigDecimal convertToBigDecimal(double area) {
        return new BigDecimal(area).setScale(SCALE, MODE);
    }

    @Override
    public BigDecimal convertToBigDecimal(String area) {
        return new BigDecimal(area).setScale(SCALE, MODE);
    }

    /**
     * Converts given String to LocalDate object
     * @param date date as String
     * @return date as LocalDate object
     * @throws InvalidInputException if given String was formatted wrong
     */
    @Override
    public LocalDate parseDate(String date) throws InvalidInputException {
         LocalDate orderDate;
        try {
            orderDate = LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Date should be in the format 'MM-dd-yyyy'");
        }
        return orderDate;
    }

    /**
     * Gets Collection of orders for given date
     * @param date LocalDate date to find orders
     * @return Collection of Order objects
     * @throws ItemNotFoundException if orders for given date don't exist
     */
    @Override
    public Collection<Order> getOrders(LocalDate date) throws ItemNotFoundException {
        Map<Integer, Order> orderMap;
        try {
            orderMap = orderDao.getOrdersForDate(date);
        } catch (FlooringMasteryPersistenceException e) {
            throw new ItemNotFoundException("Orders for " + date + " don't exist");
        }
        return orderMap.values();
    }

    /**
     * Searches order for given date and orderNumber
     * @param date LocalDate to search order
     * @param number orderNumber to search order
     * @return Map with all orders for given date as values and orderNumbers as keys
     * @throws ItemNotFoundException if order for given date and number wasn't found
     */
    @Override
    public Map<Integer, Order> getOrderMap(LocalDate date, int number) throws ItemNotFoundException {
        Map<Integer, Order> orderMap;
        try {
            orderMap = orderDao.getOrdersForDate(date);
        } catch (FlooringMasteryPersistenceException e) {
            throw new ItemNotFoundException("Orders for " + date + " don't exist");
        }
        Order order = orderMap.get(number);
        if (order == null) {
            throw new ItemNotFoundException("Order # " + number + " for " + date + " doesn't exist");
        }
        return orderMap;
    }

    /**
     * Modifies order's attributes without persistent saving
     * @param order Order object to modify
     * @param customerName String with new customerName
     * @param tax Tax object to update order with
     * @param product Product object to update order with
     * @param area BigDecimal with new area
     */
    @Override
    public void updateOrder(Order order, String customerName, Tax tax, Product product, BigDecimal area) {
        order.setCustomerName(customerName);
        order.setState(tax.getStateName());
        order.setTaxRate(tax.getTaxRate());
        order.setProductType(product.getProductType());
        order.setArea(area);
        order.setCostPerSquareFoot(product.getCostPerSquareFoot());
        order.setLaborCostPerSquareFoot(product.getLaborCostPerSquareFoot());
    }

    /**
     * Exports all existing orders from one persistent storage to another
     * @throws FlooringMasteryPersistenceException when export fails
     */
    @Override
    public void exportOrders() throws FlooringMasteryPersistenceException {
        orderDao.exportOrders();
    }

    /**
     * Deletes order for given date and orderNumber.
     * Update orders for given date accordingly
     * @param orderMap Map with orderNumbers as key and Order objects for given date as values
     * @param number int orderNumber to delete order
     * @param date LocalDate to delete order
     * @throws FlooringMasteryPersistenceException if updating orders without deleted one fails
     */
    @Override
    public void deleteOrder(Map<Integer, Order> orderMap, int number, LocalDate date) throws FlooringMasteryPersistenceException {
        orderMap.remove(number);
        orderDao.uploadModifiedOrders(orderMap.values(), date);
    }

    /**
     * Persistently edits order for given date and orderNumber with given Order object.
     * @param orderMap Map with orderNumbers as keys and Order objects as value
     * @param number int orderNumber to edit
     * @param order edited Order object to save
     * @param date LocalDate for edited object
     * @throws FlooringMasteryPersistenceException if saving edited order fails
     */
    @Override
    public void editOrder(Map<Integer, Order> orderMap, int number, Order order, LocalDate date) throws FlooringMasteryPersistenceException {
        orderMap.put(number, order);
        orderDao.uploadModifiedOrders(orderMap.values(), date);
    }

    /**
     * Gives lastOrderNumber and increments it
     * @return lastOrderNumber
     */
    private int generateOrderNumber() {
        return lastOrderNumber++;
    }

    /**
     * Set lastOrderNumber to loaded
     */
    private void loadLastOrderNumber() {
        lastOrderNumber = orderDao.loadOrderNumber();
    }

    /**
     * Upload lastOrderNumber to persistent storage through orderDao
     * @throws FlooringMasteryPersistenceException if uploading fails
     */
    public void uploadLastOrderNumber() throws FlooringMasteryPersistenceException {
        orderDao.uploadOrderNumber(lastOrderNumber);
    }

}
