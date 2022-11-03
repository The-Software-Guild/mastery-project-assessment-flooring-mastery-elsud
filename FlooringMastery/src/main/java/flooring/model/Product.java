package flooring.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {
    private String productType;
    private BigDecimal costPerSquareFoot;
    private BigDecimal laborCostPerSquareFoot;

    public Product(String[] productArray) {
        productType = productArray[0];
        costPerSquareFoot = new BigDecimal(productArray[1]);
        laborCostPerSquareFoot = new BigDecimal(productArray[2]);
    }

    public String getProductType() {
        return productType;
    }

    public BigDecimal getCostPerSquareFoot() {
        return costPerSquareFoot;
    }

    public BigDecimal getLaborCostPerSquareFoot() {
        return laborCostPerSquareFoot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return getProductType().equals(product.getProductType()) && getCostPerSquareFoot().equals(product.getCostPerSquareFoot()) && getLaborCostPerSquareFoot().equals(product.getLaborCostPerSquareFoot());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductType(), getCostPerSquareFoot(), getLaborCostPerSquareFoot());
    }
}
