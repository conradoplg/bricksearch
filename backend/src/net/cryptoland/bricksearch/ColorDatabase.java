package net.cryptoland.bricksearch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

public class ColorDatabase {
    private static final Logger log = Logger.getLogger(ColorDatabase.class.getName());
    HashMap<String, String> database = new HashMap<String, String>();

    public String getDescription(String id) {
        return database.get(id);
    }

    public void loadCSV(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        br.readLine();
        while ((line = br.readLine()) != null) {
            String[] arr = line.split(",");
            String colorID = arr[0];
            String colorName = arr[1];
            database.put(colorID, colorName);
        }
        br.close();
        log.info("Loaded " + database.size() + " colors");
    }

    public String[] getIDs() {
        return database.keySet().toArray(new String[0]);
    }
}
