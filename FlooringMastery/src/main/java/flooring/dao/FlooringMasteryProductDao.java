package flooring.dao;

import flooring.model.Product;

import java.util.Collection;

public interface FlooringMasteryProductDao {

    /**
     * Loads products from persistent storage to inner memory
     * @throws FlooringMasteryPersistenceException when loading fails
     */
    public void loadProducts() throws FlooringMasteryPersistenceException;

    /**
     * Gets Product for given product type
     * @param productType type of the product
     * @return Product object
     */
    public Product getProduct(String productType);

    /**
     * Gets Collection of all available products
     * @return Collection of Product objects
     */
    public Collection<Product> getAllProducts();
}
