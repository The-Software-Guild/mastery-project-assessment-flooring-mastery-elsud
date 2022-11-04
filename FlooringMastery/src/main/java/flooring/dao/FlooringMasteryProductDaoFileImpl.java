package flooring.dao;

import flooring.model.Product;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Component
public class FlooringMasteryProductDaoFileImpl implements FlooringMasteryProductDao {

    private final String DELIMITER = "::";
    private final String PRODUCT_FILE;
    private Map<String, Product> productMap = new HashMap<>();

    public FlooringMasteryProductDaoFileImpl() {
        PRODUCT_FILE = "Data/Products.txt";
    }

    public FlooringMasteryProductDaoFileImpl(String productFile) {
        PRODUCT_FILE = productFile;
    }

    /**
     * Reads file line by line and loads each line as a product to productMap
     * @throws FlooringMasteryPersistenceException when file doesn't exist
     */
    @Override
    public void loadProducts() throws FlooringMasteryPersistenceException {
        Scanner in;
        try {
            in = new Scanner(new BufferedReader(new FileReader(PRODUCT_FILE)));
        } catch (FileNotFoundException e) {
            throw new FlooringMasteryPersistenceException("Cannot upload product data");
        }
        String currentLine;
        Product currentProduct;
        // read the header
        in.nextLine();
        while (in.hasNextLine()) {
            currentLine = in.nextLine();
            currentProduct = unmarshallData(currentLine);
            productMap.put(currentProduct.getProductType().toLowerCase(), currentProduct);
        }
    }

    /**
     * Gets Product object for given productType from productMap
     * @param productType type of the product
     * @return Product object
     */
    @Override
    public Product getProduct(String productType) {
        return productMap.get(productType);
    }

    /**
     * Creates new Product object from given String
     * @param productAsString product information as a String
     * @return Product object
     */
    private Product unmarshallData(String productAsString) {
        String[] productArray = productAsString.split(DELIMITER);
        return new Product(productArray);
    }

    /**
     * Gets Collection of all available products from productMap
     * @return Collection of Product objects
     */
    @Override
    public Collection<Product> getAllProducts() {
        return productMap.values();
    }
}
