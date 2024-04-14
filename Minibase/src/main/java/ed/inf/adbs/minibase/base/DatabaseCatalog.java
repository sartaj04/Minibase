package ed.inf.adbs.minibase.base;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


/**
 * Database catalog is a singleton instance class which informations such as file location, schema map
 * Has functions to retrieve them
 */
public class DatabaseCatalog {
    private static DatabaseCatalog instance;
    private Map<String, String> relationToFileMap = new HashMap<>();

    private HashMap<String,List<String>> schema = new HashMap<>();

    public DatabaseCatalog(String databaseDir, String relation) {
        this.relationToFileMap = new HashMap<>();
        this.relationToFileMap.put(relation, databaseDir+"/files/"+relation+".csv");
//        this.relationToFileMap.put("S", "/Users/sartajsyed/Desktop/ads/Minibase/data/evaluation/db/files/S.csv");
//        this.relationToFileMap.put("T", "/Users/sartajsyed/Desktop/ads/Minibase/data/evaluation/db/files/T.csv");
        // add more relations here
        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(databaseDir+"/schema.txt").toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ", 2);
                String tableName = parts[0];
                String[] columnNames = parts[1].split(" ");
                List<String> columnList = new ArrayList<>();
                for (String columnName : columnNames) {
                    if (columnName != null && !columnName.isEmpty()) {
                        columnList.add(columnName);
                    }
                }
                this.schema.put(tableName, columnList);
            }
        } catch (IOException e) {
            // Handle exception
        }
    }

    public static DatabaseCatalog getInstance(String databaseDir,String relation) {
        if (instance == null) {
            instance = new DatabaseCatalog(databaseDir, relation);
        }
        return instance;
    }

    public String getFilePath(String databaseDir,String relation) {
        return this.relationToFileMap.get(relation);
    }
    public static List<String> getSchema(String databaseDir, String relation) {
        return getInstance(databaseDir, relation).schema.get(relation);
    }


    // add more catalog methods as needed
}
