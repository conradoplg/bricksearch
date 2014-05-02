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
            String[] arr = line.split(",");
            Part part = new Part(arr[0], arr[1]);
            database.put(arr[0], part);
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

    public List<Part> textSearch(String query) {
        List<Part> r = new ArrayList<Part>();
        String[] terms = query.toLowerCase().split("\\s+");
        for (Part part : database.values()) {
            String desc = part.getDescription().toLowerCase();
            if (matches(desc, terms)) {
                if (part.getUsageCount() > 0) {
                    r.add(part);
                }
            }
        }
        Collections.sort(r, new FreqComparator());
        return r;
    }

    public static void main(String[] args) {
        PartDatabase db = new PartDatabase();
        try {
            db.loadCSV("pieces.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class FreqComparator implements Comparator<Part> {
        @Override
        public int compare(Part o1, Part o2) {
            return o2.getUsageCount() - o1.getUsageCount();
        }
    }

    private boolean matches(String s, String[] terms) {
        for (String term : terms) {
            if (s.contains(term)) {
                return false;
            }
        }
        return true;
    }
}
