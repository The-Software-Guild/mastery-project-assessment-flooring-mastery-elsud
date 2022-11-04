package flooring.service;

import flooring.dao.FlooringMasteryOrderDao;
import flooring.dao.FlooringMasteryProductDao;
import flooring.dao.FlooringMasteryTaxDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("testService")
public class FlooringMasteryServiceLayerImplCopy extends FlooringMasterServiceLayerImpl {

    @Autowired
    private FlooringMasteryTaxDao taxDao;
    @Autowired
    private FlooringMasteryProductDao productDao;
    @Autowired
    private FlooringMasteryOrderDao orderDao;

}
