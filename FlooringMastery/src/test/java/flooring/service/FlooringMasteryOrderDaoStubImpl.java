package flooring.service;

import flooring.dao.FlooringMasteryOrderDao;
import flooring.dao.FlooringMasteryPersistenceException;
import flooring.model.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class FlooringMasteryOrderDaoStubImpl implements FlooringMasteryOrderDao {

    private Map<LocalDate, Map<Integer, Order>> ordersMap = new HashMap<>();
    @Override
    public void uploadOrder(Order order) throws FlooringMasteryPersistenceException {
        Map<Integer, Order> orderForDate = ordersMap.get(order.getDate());
        if (orderForDate == null) {
            orderForDate = new HashMap<>();
        }
        orderForDate.put(order.getOrderNumber(), order);
        ordersMap.put(order.getDate(), orderForDate);
    }

    @Override
    public Map<Integer, Order> getOrdersForDate(LocalDate date) throws FlooringMasteryPersistenceException {
        Map<Integer, Order> ordersForDate = ordersMap.get(date);
        if (ordersForDate == null) {
            throw new FlooringMasteryPersistenceException("Cannot load data");
        }
        return ordersForDate;
    }

    @Override
    public void uploadModifiedOrders(Collection<Order> orders, LocalDate date) throws FlooringMasteryPersistenceException {
        Map<Integer, Order> orderForDate = new HashMap<>();
        orders.stream()
              .forEach(order -> orderForDate.put(order.getOrderNumber(), order));
        ordersMap.put(date, orderForDate);
    }

    @Override
    public void exportOrders() throws FlooringMasteryPersistenceException {
        // do nothing
    }
}
