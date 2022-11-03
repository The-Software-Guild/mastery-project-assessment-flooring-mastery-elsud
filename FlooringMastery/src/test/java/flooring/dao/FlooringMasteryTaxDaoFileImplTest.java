package flooring.dao;

import flooring.model.Tax;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FlooringMasteryTaxDaoFileImplTest {

    @Test
    void testLoadTaxesFromNotExistingFile() {
        FlooringMasteryTaxDao testTaxDao = new FlooringMasteryTaxDaoFileImpl("notExisting.txt");
        assertThrows(FlooringMasteryPersistenceException.class, () -> testTaxDao.loadTaxes());
    }

    @Test
    void testLoadTaxesFromDefaultFileDoesNotThrowException() {
        FlooringMasteryTaxDao testTaxDao = new FlooringMasteryTaxDaoFileImpl();
        try{
            testTaxDao.loadTaxes();
        } catch (FlooringMasteryPersistenceException e) {
            fail("Not expected exception");
        }
    }

    @Test
    void testGetTaxFromTestFile() {
        FlooringMasteryTaxDao testTaxDao = new FlooringMasteryTaxDaoFileImpl(
                "src/test/resources/taxTest.txt"
        );
        try{
            testTaxDao.loadTaxes();
        } catch (FlooringMasteryPersistenceException e) {
            fail("Not expected exception");
        }
        Tax testTax = testTaxDao.getTax("alabama");
        assertEquals(new BigDecimal("4.00"), testTax.getTaxRate());
        assertEquals("Alabama", testTax.getStateName());

    }

    @Test
    void testGetTaxForNotExistingStateFromTestFile() {
       FlooringMasteryTaxDao testTaxDao = new FlooringMasteryTaxDaoFileImpl(
                "src/test/resources/taxTest.txt"
        );
        try{
            testTaxDao.loadTaxes();
        } catch (FlooringMasteryPersistenceException e) {
            fail("Not expected exception");
        }
        Tax testTax = testTaxDao.getTax("notExisting");
        assertNull(testTax);
    }
}