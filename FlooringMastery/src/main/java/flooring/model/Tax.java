package flooring.model;

import java.math.BigDecimal;

public class Tax {

    private String stateAbbreviation;
    private String stateName;
    private BigDecimal taxRate;

    public Tax(String[] taxArray) {
        stateAbbreviation = taxArray[0];
        stateName = taxArray[1];
        taxRate = new BigDecimal(taxArray[2]);
    }

    public String getStateName() {
        return stateName;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }
}
