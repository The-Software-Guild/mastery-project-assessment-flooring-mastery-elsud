package flooring.dao;

import flooring.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FlooringMasteryProductDaoFileImplTest {

    @Test
    void testLoadProductsFromDefaultFileDoesNotThrowException() {
        FlooringMasteryProductDao testProductDao = new FlooringMasteryProductDaoFileImpl();
        try {
            testProductDao.loadProducts();
        } catch (FlooringMasteryPersistenceException e) {
            fail("Not expected exception");
        }
    }

    @Test
    void testLoadProductsFromNotExistingFileThrowsException() {
        FlooringMasteryProductDao testProductDao = new FlooringMasteryProductDaoFileImpl("notExist");
        try {
            testProductDao.loadProducts();
            fail("Exception should be thrown");
        } catch (FlooringMasteryPersistenceException e) {
        }
    }

    @Test
    void testGetProductFromTestFile() {
        FlooringMasteryProductDao testProductDao = new FlooringMasteryProductDaoFileImpl(
                "src/test/resources/productTest.txt"
        );
        try {
            testProductDao.loadProducts();
        } catch (FlooringMasteryPersistenceException e) {
            fail("Not expected exception");
        }
        Product product2 = testProductDao.getProduct("product2");
        assertEquals(new BigDecimal("2.00"), product2.getLaborCostPerSquareFoot());
        assertEquals(new BigDecimal("2.00"), product2.getCostPerSquareFoot());
        assertEquals("product2", product2.getProductType());
    }

    @Test
    void testGetNotExistingProductFromTestFile() {
        FlooringMasteryProductDao testProductDao = new FlooringMasteryProductDaoFileImpl(
                "src/test/resources/productTest.txt"
        );
        try {
            testProductDao.loadProducts();
        } catch (FlooringMasteryPersistenceException e) {
            fail("Not expected exception");
        }
        Product product = testProductDao.getProduct("notExist");
        assertNull(product);
    }

    @Test
    void testGetAllProducts() {
        FlooringMasteryProductDao testProductDao = new FlooringMasteryProductDaoFileImpl(
                "src/test/resources/productTest.txt"
        );
        Collection<Product> products = testProductDao.getAllProducts();
        assertEquals(0, products.size());
        try {
            testProductDao.loadProducts();
        } catch (FlooringMasteryPersistenceException e) {
            fail("Not expected exception");
        }
        Product product1 = new Product(new String[]{"product1", "1.00", "1.00"});
        Product product2 = new Product(new String[]{"product2", "2.00", "2.00"});
        products = testProductDao.getAllProducts();
        assertTrue(products.contains(product1));
        assertTrue(products.contains(product2));
        assertEquals(2, products.size());
    }
}