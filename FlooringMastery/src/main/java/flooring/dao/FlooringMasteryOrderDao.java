package flooring.dao;

import flooring.model.Order;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public interface FlooringMasteryOrderDao {

    /**
     * Uploads new order to persistent storage
     * @param order Order object to upload
     * @throws FlooringMasteryPersistenceException when uploading fails
     */
    public void uploadOrder(Order order) throws FlooringMasteryPersistenceException;

    /**
     * Gets all orders for given date
     * @param date LocalDate object
     * @return Map with orderNumbers as Keys and Order objects as values
     * @throws FlooringMasteryPersistenceException when loading of orders fails
     */
    public Map<Integer, Order> getOrdersForDate(LocalDate date) throws FlooringMasteryPersistenceException;

    /**
     * Uploads orders back to persistent storage after modifying one of the orders
     * @param orders Collection of orders to upload
     * @param date LocalDate object associated with orderDate
     * @throws FlooringMasteryPersistenceException when uploading of orders fails
     */
    public void uploadModifiedOrders(Collection<Order> orders, LocalDate date) throws FlooringMasteryPersistenceException;

    /**
     * Exports all existing orders from one persistent storage to another
     * @throws FlooringMasteryPersistenceException when export fails
     */
    public void exportOrders() throws FlooringMasteryPersistenceException;
}
