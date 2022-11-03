package flooring.ui;

import flooring.model.Order;
import flooring.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;

@Component
public class FlooringMasteryView {

    @Autowired
    private UserIO io;

    /**
     * Displays menu and reads user's input
     * @return int associated with menu button
     */
    public int displayMenu() {
        io.print("****************");
        io.print("* <<Flooring Program>>");
        io.print("* 1. Display Orders");
        io.print("* 2. Add an Order");
        io.print("* 3. Edit an Order");
        io.print("* 4. Remove an Order");
        io.print("* 5. Export All Data");
        io.print("* 6. Quit");
        io.print("****************");
        return io.readInt("Please enter the number of your choice", 1, 6);
    }

    /**
     * Reads input that supposed to be order's date
     * @return entered String
     */
    public String askDate() {
        return io.readString("Please enter order date. It should be in format MM-dd-yyyy");
    }

    /**
     * Reads input that supposed to be customer's name
     * @return entered String
     */
    public String askName(){
        return io.readString("Please enter customer name");
    }

    /**
     * Reads input that supposed to be customer's name and displays previous name
     * @return entered String
     */
    public String askName(String previousName){
        return io.readString("Please enter customer name (" + previousName + ")");
    }

    /**
     * Reads input that supposed to be state name
     * @return entered String
     */
    public String askStateName() {
        return io.readString("Please enter state");
    }

     /**
     * Reads input that supposed to be state name and displays previous state
     * @return entered String
     */
    public String askStateName(String previousState) {
        return io.readString("Please enter state (" + previousState + ")");
    }

    /**
     *  Displays available products and reads input that supposed to be product type
     * @return entered String
     */
    public String askProductType(Collection<Product> products) {
        displayProducts(products);
        return io.readString("Please choose product type");
    }

    /**
     * Displays available products and previous product type.
     * Reads input that supposed to be product type
     * @return entered String
     */
    public String askProductType(Collection<Product> products, String previousType) {
        displayProducts(products);
        return io.readString("Please choose product type (" + previousType + ")");
    }

    /**
     * Reads input that supposed to be double not less than 100.0
     * @return entered double
     */
    public double askArea() {
        return io.readDouble(
                "Please enter the area (minimum order size is 100 sq ft)",
                100.0, Double.MAX_VALUE
        );
    }

    /**
     * Reads input that supposed to be convertible to double not less than 100.0
     * and displays previous area
     * @return entered data or previous data as a String
     */
    public String askArea(BigDecimal previousArea) {
        String input;
        double inputAsDouble;
        while (true) {
            input = io.readString(
                "Please enter the area (minimum order size is 100 sq ft) (" + previousArea + ")");
            if ( input == null || input.isEmpty()) {
                return previousArea.toString();
            }
            try {
                inputAsDouble = Double.parseDouble(input);
                if (inputAsDouble >= 100) {
                    return input;
                }
            } catch (NumberFormatException e) {
                io.print("Input error. Please try again");
            }
        }
    }

    /**
     * Reads input that supposed to be order's number
     * @return entered integer
     */
    public int askNumber() {
        return io.readInt("Enter order number");
    }

    /**
     * Displays attributes of the order
     * @param order Order object
     */
    public void displayOrder(Order order) {
        io.print("Order date - " + order.getDate());
        io.print("Customer name - " + order.getCustomerName());
        io.print("State - " + order.getState());
        io.print("Tax rate - " + order.getTaxRate());
        io.print("Product type _ " + order.getProductType());
        io.print("Cost per square foot - " + order.getCostPerSquareFoot());
        io.print("Labor cost per square foot - " + order.getLaborCostPerSquareFoot());
        io.print("Area - " + order.getArea());
        io.print("Material cost without taxes - " + order.getMaterialCost());
        io.print("Labor cost without taxes - " + order.getLaborCost());
        io.print("Taxes - " + order.getTax());
        io.print("Total cost - " +order.getTotal());
    }

    /**
     * Reads input to confirm or cancel some action
     * @param action action to confirm (delete, save etc.)
     * @return entered String
     */
    public String confirm(String action) {
        return io.readString("Do you want to " + action + " this order? (y/n)");
    }

    /**
     * Displays available products with their costs
     * @param products Collection of Product objects
     */
    public void displayProducts(Collection<Product> products){
        io.print("Product type - Cost per square foot - Labor cost per square foot");
        products.stream()
                .forEach((product) -> io.print(
                        product.getProductType() + " - " +
                        product.getLaborCostPerSquareFoot() + " - "
                        + product.getLaborCostPerSquareFoot()
                ));
    }

    /**
     * Displays information about orders from given Collection.
     * Waits input to continue
     * @param orders Collection of Order objects
     */
    public void displayOrders(Collection<Order> orders) {
        orders.stream()
              .forEach((order) -> io.print(
                      "Order #" + order.getOrderNumber() + " - Customer "
                       + order.getCustomerName() + " - " + order.getState()
                       + " - " + order.getProductType() + " - Area "
                       + order.getArea() + "sq.f. - Cost $" + order.getTotal()
              ));
        io.readString("Please hit enter to continue");
    }

    /**
     * Displays error message and waits input to continue
     * @param message text of the message to display
     */
    public void displayErrorMessage(String message) {
        io.print(message);
        io.readString("Please hit enter to continue");
    }

    /**
     * Displays a banner for successful export of the data and waits input to continue
     */
    public void displaySuccessfulExportBanner() {
        io.readString("Orders were exported. Please, hit enter to continue");
    }

    /**
     * Displays a banner for successful order saving and waits input to continue
     */
    public void displayOrderSavedBanner() {
        io.readString("Order was saved. Please hit enter to continue");
    }

    /**
     * Displays a banner for successful order deleting and waits input to continue
     */
    public void displayOrderDeletedBanner() {
        io.readString("Order was deleted. Please hit enter to continue");
    }

    /**
     * Displays a banner for successful order editing and waits input to continue
     */
    public void displayOrderEditedBanner() {
        io.readString("Order was edited. Please hit enter to continue");
    }

    /**
     * Displays Good-bye message
     */
    public  void displayGoodByeMessage() {
        io.print("Good bye!");
    }
}
