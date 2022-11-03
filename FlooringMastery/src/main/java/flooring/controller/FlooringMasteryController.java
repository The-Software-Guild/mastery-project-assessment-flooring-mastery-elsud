package flooring.controller;

import flooring.dao.FlooringMasteryPersistenceException;
import flooring.model.Order;
import flooring.model.Product;
import flooring.model.Tax;
import flooring.service.FlooringMasteryServiceLayer;
import flooring.service.InvalidInputException;
import flooring.service.ItemNotFoundException;
import flooring.ui.FlooringMasteryView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

@Component("controller")
public class FlooringMasteryController {

    private final FlooringMasteryServiceLayer service;
    private final FlooringMasteryView view;

    @Autowired
    public FlooringMasteryController(FlooringMasteryServiceLayer service, FlooringMasteryView view) {
        this.service = service;
        this.view = view;
    }

    /**
     * Main function to process user's requests
     */
    public void run() {
        boolean ifQuit = false;
        // load necessary data, exit if loading fails
        try {
            service.loadData();
        } catch (FlooringMasteryPersistenceException e) {
            view.displayErrorMessage(e.getMessage());
            ifQuit = true;
        }
        // loop for processing requests
        while (!ifQuit) {
            int choice = view.displayMenu();
            switch (choice) {
                case 1:
                    displayOrders();
                    break;
                case 2:
                    addOrder();
                    break;
                case 3:
                    editOrder();
                    break;
                case 4:
                    deleteOrder();
                    break;
                case 5:
                    exportOrders();
                    break;
                case 6:
                    ifQuit = true;
                    break;
                default:
                    view.displayErrorMessage("Unknown command");
            }
        }
        // upload data before exiting
        try {
            service.uploadLastOrderNumber();
        } catch (FlooringMasteryPersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }
        view.displayGoodByeMessage();
    }

    /**
     * Gets date, orders for that date and displays them
     */
    private void displayOrders() {
        LocalDate date = getDate();
        try {
            Collection<Order> orders = service.getOrders(date);
            view.displayOrders(orders);
        } catch (ItemNotFoundException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    /**
     * Gets necessary data to create order.
     * If user confirms creation, saves this order
     */
    private void addOrder() {
        LocalDate date = askAndValidateDate();
        String customerName = askAndValidateName();
        Tax tax = askAndValidateState();
        Product product = askAndValidateProductType();
        BigDecimal area = service.convertToBigDecimal(view.askArea());
        Order order = service.createOrder(date, customerName, tax, product, area);;
        view.displayOrder(order);
        // ask for confirmation
        boolean isValid = false;
        String choice;
        while (!isValid) {
            choice = view.confirm("place");
            switch (choice) {
                case "y":
                case "Y":
                    try {
                        service.saveOrder(order);
                        view.displayOrderSavedBanner();
                    } catch (FlooringMasteryPersistenceException e) {
                        view.displayErrorMessage(e.getMessage());
                    }
                    isValid = true;
                    break;
                case "n":
                case "N":
                    isValid = true;
                    break;
                default:
                    view.displayErrorMessage("Unknown command");
            }
        }

    }

    /**
     * Gets date and orderNumber and searches for appropriate order.
     * If order exists and user confirms deletion, deletes it
     */
    private void deleteOrder() {
        LocalDate date = getDate();
        int number = view.askNumber();
        Map<Integer, Order> orderMap = null;
        try {
            orderMap = service.getOrderMap(date, number);
        } catch (ItemNotFoundException e) {
            view.displayErrorMessage(e.getMessage());
            return;
        }
        view.displayOrder(orderMap.get(number));
        // ask for confirmation
        boolean isValid = false;
        String choice;
        while (!isValid) {
            choice = view.confirm("delete");
            switch (choice) {
                case "y":
                case "Y":
                    try {
                        service.deleteOrder(orderMap, number, date);
                        view.displayOrderDeletedBanner();
                    } catch (FlooringMasteryPersistenceException e) {
                        view.displayErrorMessage(e.getMessage());
                    }
                    isValid = true;
                    break;
                case "n":
                case "N":
                    isValid = true;
                    break;
                default:
                    view.displayErrorMessage("Unknown command");
            }
        }
    }

    /**
     * Gets date and orderNumber and searches for appropriate order.
     * If order exists, modifies this order. If user confirms editing, saves it
     */
    private void editOrder() {
        LocalDate date = getDate();
        int number = view.askNumber();
        Map<Integer, Order> orderMap = null;
        try {
            orderMap = service.getOrderMap(date, number);
        } catch (ItemNotFoundException e) {
            view.displayErrorMessage(e.getMessage());
            return;
        }
        Order order = orderMap.get(number);
        Order newOrder = getModifiedOrder(order);
        view.displayOrder(newOrder);
        // ask for confirmation
        boolean isValid = false;
        String choice;
        while (!isValid) {
            choice = view.confirm("save");
            switch (choice) {
                case "y":
                case "Y":
                    try {
                        service.editOrder(orderMap, number, newOrder, date);
                        view.displayOrderEditedBanner();
                    } catch (FlooringMasteryPersistenceException e) {
                        view.displayErrorMessage(e.getMessage());
                    }
                    isValid = true;
                    break;
                case "n":
                case "N":
                    isValid = true;
                    break;
                default:
                    view.displayErrorMessage("Unknown command");
            }
        }
    }

    /**
     * Runs code to export all orders from one persistent storage to another
     */
    private void exportOrders() {
        try {
            service.exportOrders();
            view.displaySuccessfulExportBanner();
        } catch (FlooringMasteryPersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    /**
     * Runs code to get date until it wouldn't be valid
     * @return LocalDate object
     */
    private LocalDate askAndValidateDate() {
        boolean isValid = false;
        String dateAsString;
        LocalDate date = null;
        while (!isValid) {
            dateAsString = view.askDate();
            try {
                date = service.validateDate(dateAsString);
                isValid = true;
            } catch (InvalidInputException e) {
                view.displayErrorMessage(e.getMessage());
            }
        }
        return date;
    }

    /**
     * Runs code to get customerName until it wouldn't be valid
     * @return String customerName
     */
    private String askAndValidateName() {
         boolean isValid = false;
         String name = null;
         while (!isValid) {
             name = view.askName();
             try {
                 service.validateName(name);
                 isValid = true;
             } catch (InvalidInputException e) {
                 view.displayErrorMessage(e.getMessage());
             }
         }
         return name;
     }

    /**
     * Runs code to get customerName until it wouldn't be valid.
     * Displays previous name to give an option not to modify it.
     * @param previousName String with previous name
     * @return String customerName String with new name
     */
    private String askAndValidateName(String previousName) {
        String newName = null;
        boolean isValid = false;
        while (!isValid) {
            newName = view.askName(previousName);
            if (newName == null || newName.isEmpty()) {
                // leave previous value
                return previousName;
            }
            try {
                service.validateName(newName);
                isValid = true;
            } catch (InvalidInputException e) {
                view.displayErrorMessage(e.getMessage());
            }
        }
        return newName;
    }

     /**
     * Runs code to get state until it wouldn't be valid
     * @return Tax object for that state
     */
     private Tax askAndValidateState() {
        boolean isValid = false;
        String state;
        Tax tax = null;
        while (!isValid) {
            state = view.askStateName();
            try {
                tax = service.validateState(state);
                isValid = true;
            } catch (InvalidInputException e) {
                view.displayErrorMessage(e.getMessage());
            }
        }
        return tax;
    }

     /**
     * Runs code to get state until it wouldn't be valid.
     * Displays previous state to give an option not to modify it.
     * @param previousState String with previous state
     * @return Tax object for new state
     */
    private Tax askAndValidateState(String previousState) {
        boolean isValid = false;
        String newState;
        Tax tax = null;
        while (!isValid) {
            newState = view.askStateName(previousState);
            if (newState == null || newState.isEmpty()) {
                // leave previous value
                newState = previousState;
            }
            try {
                tax = service.validateState(newState);
                isValid = true;
            } catch (InvalidInputException e) {
                view.displayErrorMessage(e.getMessage());
            }
        }
        return tax;
    }

    /**
     * Runs code to get productType until it wouldn't be valid
     * @return Product object for that type
     */
    private Product askAndValidateProductType() {
        boolean isValid = false;
        String productType;
        Product product = null;
        Collection<Product> products = service.getAllProducts();
        while (!isValid) {
            productType = view.askProductType(products);
            try {
                product = service.validateType(productType);
                isValid = true;
            } catch (InvalidInputException e) {
                view.displayErrorMessage(e.getMessage());
            }
        }
        return product;
    }

    /**
     * Runs code to get productType until it wouldn't be valid.
     * Displays previous productType to give an option not to modify it.
     * @param previousType Sting with previous type
     * @return Product object for new productType
     */
    private Product askAndValidateProductType(String previousType) {
        boolean isValid = false;
        String productType;
        Product product = null;
        Collection<Product> products = service.getAllProducts();
        while (!isValid) {
            productType = view.askProductType(products, previousType);
            if (productType == null || productType.isEmpty()) {
                // leave previous value
                productType = previousType;
            }
            try {
                product = service.validateType(productType);
                isValid = true;
            } catch (InvalidInputException e) {
                view.displayErrorMessage(e.getMessage());
            }
        }
        return product;
    }


    /**
     * Gets date for searching orders until entered date wouldn't be valid
     * @return LocalDate object
     */
    private LocalDate getDate() {
        boolean isDateValid = false;
        LocalDate date = null;
        String dateAsString;
        while (!isDateValid) {
            dateAsString = view.askDate();
            try {
                date = service.parseDate(dateAsString);
                isDateValid = true;
            } catch (InvalidInputException e) {
                view.displayErrorMessage(e.getMessage());
            }
        }
        return date;
    }

    /**
     * Runs code to get new fields' values for given order and updates
     * order with these values
     * @param order Order object to edit
     * @return modified object
     */
    private Order getModifiedOrder(Order order) {
        String newName = askAndValidateName(order.getCustomerName());
        Tax newTax = askAndValidateState(order.getState());
        Product newProduct = askAndValidateProductType(order.getProductType());
        BigDecimal newArea = service.convertToBigDecimal(view.askArea(order.getArea()));
        service.updateOrder(order, newName, newTax, newProduct, newArea);
        return order;
    }

}
