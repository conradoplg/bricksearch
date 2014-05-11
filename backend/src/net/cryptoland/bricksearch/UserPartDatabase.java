package net.cryptoland.bricksearch;

import javax.jws.soap.SOAPBinding;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class UserPartDatabase {
    private static final Logger log = Logger.getLogger(UserPartDatabase.class.getName());
    List<SetPart> looseParts = new ArrayList<SetPart>();
    HashMap<String, Set> sets = new HashMap<String, Set>();
    HashMap<String, Integer> setCounts = new HashMap<String, Integer>();
    private SetDatabase setDB;

    public UserPartDatabase(SetDatabase setDB) {
        this.setDB = setDB;
    }

    public void loadSetTSV(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            String[] arr = line.split("\t");
            int count = Integer.parseInt(arr[0]);
            String setID = arr[1];
            Set set = setDB.getSet(setID);
            if (set != null) {
                sets.put(setID, set);
                setCounts.put(setID, setCounts.containsKey(setID) ? setCounts.get(setID) + 1 : 1);
            }
        }
        br.close();
        log.info("Loaded " + sets.size() + " distinct sets");
    }

    public void loadPartCSV(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        br.readLine();
        while ((line = br.readLine()) != null) {
            String[] arr = line.split(",");
            String partID = arr[0];
            String colorID = arr[1];
            int count = Integer.parseInt(arr[2]);
            SetPart setPart = new SetPart(partID, count, colorID, "0");
            looseParts.add(setPart);
        }
        br.close();
        log.info("Loaded " + sets.size() + " distinct sets, " + setCounts.size() + " total");
    }
}
