package flooring.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

public class Order {

    private LocalDate date;
    private int orderNumber;
    private String customerName;
    private String state;
    private BigDecimal taxRate;
    private String productType;
    private BigDecimal area;
    private BigDecimal costPerSquareFoot;
    private BigDecimal laborCostPerSquareFoot;
    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal tax;
    private BigDecimal total;

    public Order() {
    }

    public Order(
            LocalDate date, String customerName, String stateName,
            BigDecimal taxRate, String productType, BigDecimal area,
            BigDecimal costPerSquareFoot, BigDecimal laborCostPerSquareFoot
            ) {
        this.date = date;
        this.customerName = customerName;
        this.state = stateName;
        this.taxRate = taxRate;
        this.productType = productType;
        this.area = area;
        this.costPerSquareFoot = costPerSquareFoot;
        this.laborCostPerSquareFoot = laborCostPerSquareFoot;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getCostPerSquareFoot() {
        return costPerSquareFoot;
    }

    public void setCostPerSquareFoot(BigDecimal costPerSquareFoot) {
        this.costPerSquareFoot = costPerSquareFoot;
    }

    public BigDecimal getLaborCostPerSquareFoot() {
        return laborCostPerSquareFoot;
    }

    public void setLaborCostPerSquareFoot(BigDecimal laborCostPerSquareFoot) {
        this.laborCostPerSquareFoot = laborCostPerSquareFoot;
    }

    /**
     * Multiplies area and costPerSquareFoot
     * @return materialCost as BigDecimal with scale 2 and RoundingMode HALF_UP
     */
    public BigDecimal getMaterialCost() {
        return costPerSquareFoot.multiply(area).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Multiplies area and laborCostPerSquareFoot
     * @return laborCost as BigDecimal with scale 2 and RoundingMode HALF_UP
     */
    public BigDecimal getLaborCost() {
        return laborCostPerSquareFoot.multiply(area).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates tax for both material and labor costs
     * @return tax as BigDecimal with scale 2 and RoundingMode HALF_UP
     */
    public BigDecimal getTax() {
        BigDecimal costWithoutTax = getLaborCost().add(getMaterialCost());
        return costWithoutTax.multiply(getTaxRate())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /**
     * Adds materialCost, laborCost and tax
     * @return total cost as BigDecimal
     */
    public BigDecimal getTotal() {
        BigDecimal costWithoutTax = getLaborCost().add(getMaterialCost());
        return costWithoutTax.add(getTax());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return getOrderNumber() == order.getOrderNumber() && getDate().equals(order.getDate()) && getCustomerName().equals(order.getCustomerName()) && getState().equals(order.getState()) && getProductType().equals(order.getProductType()) && getArea().equals(order.getArea());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getOrderNumber(), getCustomerName(), getState(), getProductType(), getArea());
    }
}
