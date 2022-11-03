package flooring.service;

import flooring.dao.FlooringMasteryTaxDao;
import flooring.model.Tax;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FlooringMasteryTaxDaoStubImpl implements FlooringMasteryTaxDao {

    private Map<String, Tax> taxMap;

    @Override
    public void loadTaxes() {
        taxMap = new HashMap<>();
        Tax tax = new Tax(new String[] {"s", "state", "10.0"});
        taxMap.put(tax.getStateName(), tax);
    }

    @Override
    public Tax getTax(String state) {
        return taxMap.get(state);
    }

}
