package flooring.dao;

import flooring.model.Tax;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Component
public class FlooringMasteryTaxDaoFileImpl implements FlooringMasteryTaxDao {

    private final String DELIMITER = "::";
    private final String TAX_FILE;
    private Map<String, Tax> taxMap = new HashMap<>();

    public FlooringMasteryTaxDaoFileImpl() {
        TAX_FILE = "Data/Taxes.txt";
    }

    public FlooringMasteryTaxDaoFileImpl(String taxFile) {
        TAX_FILE = taxFile;
    }

    /**
     * Reads file line by line and loads each line as a tax to taxMap
     * @throws FlooringMasteryPersistenceException when file doesn't exist
     */
    @Override
    public void loadTaxes() throws FlooringMasteryPersistenceException {
        Scanner in;
        try {
            in = new Scanner(new BufferedReader(new FileReader(TAX_FILE)));
        } catch (FileNotFoundException e) {
            throw new FlooringMasteryPersistenceException("Cannot upload tax data");
        }
        String currentLine;
        Tax currentTax;
        // read the header
        in.nextLine();
        while (in.hasNextLine()) {
            currentLine = in.nextLine();
            currentTax = unmarshallData(currentLine);
            taxMap.put(currentTax.getStateName().toLowerCase(), currentTax);
        }
    }

    /**
     * Gets Tax object for given state from taxMap
     * @param state state name
     * @return Tax object
     */
    @Override
    public Tax getTax(String state) {
        return taxMap.get(state);
    }

    /**
     * Creates new Tax object from given String
     * @param taxAsString tax information as a String
     * @return Tax object
     */
    private Tax unmarshallData(String taxAsString) {
        String[] taxArray = taxAsString.split(DELIMITER);
        return new Tax(taxArray);
    }

}
