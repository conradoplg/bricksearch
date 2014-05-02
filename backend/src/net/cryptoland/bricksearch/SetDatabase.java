package net.cryptoland.bricksearch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class SetDatabase {

    HashMap<String, Set> database = new HashMap<String, Set>();
    private static final Logger log = Logger.getLogger(SetDatabase.class.getName());

    public Collection<Set> getValues() {
        return database.values();
    }

    public Set getSet(String setID) {
        return database.get(setID);
    }

    public void loadCSV(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        br.readLine();
        while ((line = br.readLine()) != null) {
            List<SetPart> setParts;
            String[] arr = line.split(",");
            String setId = arr[0];
            Set set = database.get(setId);
            if (set == null) {
                setParts = new ArrayList<SetPart>();
                set = new Set(setId, setParts);
                database.put(setId, set);
            } else {
                setParts = set.getParts();
            }
            SetPart setPart = new SetPart(arr[1], Integer.valueOf(arr[2]), arr[3], arr[4]);
            setParts.add(setPart);
        }
        br.close();
        log.info("Loaded " + database.size() + " sets");
    }

    public static void main(String[] args) {
        PartDatabase db = new PartDatabase();
        try {
            db.loadCSV("set_pieces.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
