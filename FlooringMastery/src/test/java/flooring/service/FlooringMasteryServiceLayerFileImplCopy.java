package flooring.service;

import flooring.dao.FlooringMasteryOrderDao;
import flooring.dao.FlooringMasteryPersistenceException;
import flooring.dao.FlooringMasteryProductDao;
import flooring.dao.FlooringMasteryTaxDao;
import flooring.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("testService")
public class FlooringMasteryServiceLayerFileImplCopy extends FlooringMasterServiceLayerFileImpl{

    private int lastOrderNumber;

    @Autowired
    private FlooringMasteryTaxDao taxDao;
    @Autowired
    private FlooringMasteryProductDao productDao;
    @Autowired
    private FlooringMasteryOrderDao orderDao;

    // Copied to call right version of loadLastOrderNumber
    @Override
    public void loadData() throws FlooringMasteryPersistenceException {
        taxDao.loadTaxes();
        productDao.loadProducts();
        lastOrderNumber = loadLastOrderNumber();
    }

    // Modify it to return 10 instead of reading from the file
    private int loadLastOrderNumber() {
        return 10;
    }

    // Copied to call copied generateOrderNumber
    @Override
    public void saveOrder(Order order) throws FlooringMasteryPersistenceException {
        int orderNumber = generateOrderNumber();
        order.setOrderNumber(orderNumber);
        orderDao.uploadOrder(order);
    }

    // Copied to interact with right lastOrderNumber attribute
    private int generateOrderNumber() {
         return lastOrderNumber++;
     }

    // Modified in order to not write to the file
    public void uploadLastOrderNumber() throws FlooringMasteryPersistenceException {
        // do nothing
    }

}

