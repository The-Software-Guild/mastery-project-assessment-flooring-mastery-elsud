package flooring.service;

import flooring.dao.FlooringMasteryPersistenceException;
import flooring.model.Order;
import flooring.model.Product;
import flooring.model.Tax;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public interface FlooringMasteryServiceLayer {

    /**
     * Loads all necessary data from persistent storage
     * @throws FlooringMasteryPersistenceException if loading fails
     */
    public void loadData() throws FlooringMasteryPersistenceException;

    /**
     * Converts given String to LocalDate and validates it
     * @param date String supposed to be converted to LocalDate
     * @return LocalDate object
     * @throws InvalidInputException if given String wasn't valid
     */
    public LocalDate validateDate(String date) throws InvalidInputException;

    /**
     * Validates given String
     * @param name String supposed to be customer name
     * @throws InvalidInputException if given String wasn't valid
     */
    public void validateName(String name) throws InvalidInputException;

    /**
     * Validates that given state exists
     * @param state String supposed to be state
     * @return Tax object for this state
     * @throws InvalidInputException if tax for given state doesn't exist
     */
    public Tax validateState(String state) throws InvalidInputException;

    /**
     * Validates that given productType exists
     * @param type String supposed to be productType
     * @return Product object
     * @throws InvalidInputException if product of given type doesn't exist
     */
    public Product validateType(String type) throws InvalidInputException;

    /**
     * Creates Order object
     * @param date order date as LocalDate
     * @param customerName customer name as String
     * @param tax Tax object
     * @param product Product object
     * @param area area as BigDecimal
     * @return Order object
     */
    public Order createOrder(
            LocalDate date, String customerName, Tax tax, Product product, BigDecimal area);

    /**
     * Saves given Order object to persistent storage
     * @param order Order object to save
     * @throws FlooringMasteryPersistenceException if saving fails
     */
    public void saveOrder(Order order) throws FlooringMasteryPersistenceException;

    /**
     * Gets collection of all available products
     * @return Collection of Product objects
     */
    public Collection<Product> getAllProducts();

    /**
     * Converts given double to BigDecimal
     * @param area area as double
     * @return area as BigDecimal
     */
    public BigDecimal convertToBigDecimal(double area);

    /**
     * Converts given String to BigDecimal
     * @param area area as String
     * @return area as BigDecimal
     */
    public BigDecimal convertToBigDecimal(String area);

    /**
     * Converts given String to LocalDate object
     * @param date date as String
     * @return date as LocalDate object
     * @throws InvalidInputException if given String was formatted wrong
     */
    public LocalDate parseDate(String date) throws InvalidInputException;

    /**
     * Gets Collection of orders for given date
     * @param date LocalDate date to find orders
     * @return Collection of Order objects
     * @throws ItemNotFoundException if orders for given date don't exist
     */
    public Collection<Order> getOrders(LocalDate date) throws ItemNotFoundException;

    /**
     * Searches order for given date and orderNumber
     * @param date LocalDate to search order
     * @param number orderNumber to search order
     * @return Map with all orders for given date as values and orderNumbers as keys
     * @throws ItemNotFoundException if order for given date and number wasn't found
     */
    public Map<Integer, Order> getOrderMap(LocalDate date, int number) throws ItemNotFoundException;

    /**
     * Deletes order for given date and orderNumber.
     * Update orders for given date accordingly
     * @param orderMap Map with orderNumbers as key and Order objects for given date as values
     * @param number int orderNumber to delete order
     * @param date LocalDate to delete order
     * @throws FlooringMasteryPersistenceException if updating orders without deleted one fails
     */
    public void deleteOrder(Map<Integer, Order> orderMap, int number, LocalDate date) throws FlooringMasteryPersistenceException;

    /**
     * Persistently edits order for given date and orderNumber with given Order object.
     * @param orderMap Map with orderNumbers as keys and Order objects as value
     * @param number int orderNumber to edit
     * @param order edited Order object to save
     * @param date LocalDate for edited object
     * @throws FlooringMasteryPersistenceException if saving edited order fails
     */
    public void editOrder(Map<Integer, Order> orderMap, int number, Order order, LocalDate date) throws FlooringMasteryPersistenceException;

    /**
     * Modifies order's attributes without persistent saving
     * @param order Order object to modify
     * @param customerName String with new customerName
     * @param tax Tax object to update order with
     * @param product Product object to update order with
     * @param area BigDecimal with new area
     */
    public void updateOrder(Order order, String customerName, Tax tax, Product product, BigDecimal area);

    /**
     * Exports all existing orders from one persistent storage to another
     * @throws FlooringMasteryPersistenceException when export fails
     */
    public void exportOrders() throws FlooringMasteryPersistenceException;

    /**
     * Saves last orderNumber to persistent storage
     * @throws FlooringMasteryPersistenceException if error during saving occurs
     */
    public void uploadLastOrderNumber() throws FlooringMasteryPersistenceException;
}
