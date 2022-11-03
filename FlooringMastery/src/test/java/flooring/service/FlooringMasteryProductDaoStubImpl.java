package flooring.service;

import flooring.dao.FlooringMasteryPersistenceException;
import flooring.dao.FlooringMasteryProductDao;
import flooring.model.Product;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class FlooringMasteryProductDaoStubImpl implements FlooringMasteryProductDao {

    private Map<String, Product> productMap = new HashMap<>();

    @Override
    public void loadProducts() throws FlooringMasteryPersistenceException {
        Product product = new Product(new String[] {"product", "5.00", "15.00"});
        productMap.put(product.getProductType(), product);
    }

    @Override
    public Product getProduct(String productType) {
        return productMap.get(productType);
    }

    @Override
    public Collection<Product> getAllProducts() {
        return productMap.values();
    }

}
