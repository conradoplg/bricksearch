package net.cryptoland.bricksearch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class PartDatabase {
    HashMap<String, Part> database = new HashMap<String, Part>();
    private static final Logger log = Logger.getLogger(PartDatabase.class.getName());

    public void loadCSV(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        br.readLine();
        while ((line = br.readLine()) != null) {
            String[] arr = line.split(",", 2);
            String id = arr[0];
            String desc = arr[1];
            desc = desc.replaceAll("\"", "");
            Part part = new Part(id, desc);
            database.put(id, part);
        }
        br.close();
        log.info("Loaded " + database.size() + " parts");
    }

    public void loadSetsStatistics(SetDatabase setDB) {
        for (Set set: setDB.getValues()) {
            for (SetPart setPart: set.getParts()) {
                Part part = database.get(setPart.getPartID());
                if (part == null) {
                    //System.err.println("Not in database: " + setPart.getPartID() + " from set " + set.getId());
                } else {
                    part.setUsageCount(part.getUsageCount() + setPart.getQuantity());
                }
            }
        }
    }

    public static void main(String[] args) {
        PartDatabase db = new PartDatabase();
        try {
            db.loadCSV("pieces.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Part get(String id) {
        return database.get(id);
    }

    public Collection<Part> values() {
        return database.values();
    }
}
