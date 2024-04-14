package ed.inf.adbs.minibase.base;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ed.inf.adbs.minibase.base.*;

import static ed.inf.adbs.minibase.base.DatabaseCatalog.*;

public class ScanOperator extends Operator {
    private String relation;
    private String databaseDir;
    private BufferedReader reader;
    private Tuple nextTuple;
    private List<String> schema;

    private DatabaseCatalog catalog;

    /**
     * Scan Operator class which extends Operator class which scans through the db to retrieve tuples of variables available based on schema
     * Populates functions of Operator class
     */

    public ScanOperator(String databaseDir, String relation) {
        this.relation = relation;
        this.databaseDir = databaseDir;
        try {
            catalog = new DatabaseCatalog(databaseDir,relation);
            // Initialize the reader with the file path of the relation
            this.reader = new BufferedReader(new FileReader(catalog.getFilePath(databaseDir, relation)));
            this.nextTuple = getNextTupleFromFile();
            // Get the schema of the relation from the catalog
            this.schema = catalog.getSchema(databaseDir, relation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gives schema of particular Relation
     */
    public List<String> getSchema(){
        return this.schema;
    }

    /**
     * Gives Next Tuple of values i.e. row present in db
     */
    @Override
    public Tuple getNextTuple() {
        Tuple returnTuple = nextTuple;
        nextTuple = getNextTupleFromFile();
        // Return the next tuple
        return returnTuple;
    }

    /**
     * Resets closes reader and starts the new reader
     */
    @Override
    public void reset() {
        try {
            // Close the reader and initialize it again to read the relation from the beginning
            this.reader.close();
            this.reader = new BufferedReader(new FileReader(catalog.getFilePath(databaseDir, relation)));
            this.nextTuple = getNextTupleFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves next tuple from the file
     */
    private Tuple getNextTupleFromFile() {
        try {
            // Read the next line from the relation file
            String line = reader.readLine();
            if (line == null) {
                return null;
            }
            // Split the line into individual values and add them to a list of literals
            String[] values = line.split(",");
            List<String> literals = new ArrayList<>();
            for (String value : values) {
                literals.add(value);
            }
            // Create a new tuple with the list of literals as its attributes
            return new Tuple(literals);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
