package flooring.dao;

import flooring.model.Tax;

public interface FlooringMasteryTaxDao {

    /**
     * Loads taxes from persistence storage to inner memory
     * @throws FlooringMasteryPersistenceException when loading process fails
     */
    public void loadTaxes() throws FlooringMasteryPersistenceException;

    /**
     * Gets tax for given state
     * @param state state name
     * @return Tax object
     */
    public Tax getTax(String state);
}
