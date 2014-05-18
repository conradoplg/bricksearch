package net.cryptoland.bricksearch;

import java.io.IOException;
import java.util.*;

public class Database {
    private PartDatabase partDB = new PartDatabase();
    private SetDatabase setDB = new SetDatabase();
    private ColorDatabase colorDB = new ColorDatabase();
    private UserPartDatabase userPartDB;
    private HashMap<String, HashSet<String>> partColors = new HashMap<String, HashSet<String>>();

    public PartDatabase getPartDB() {
        return partDB;
    }

    public SetDatabase getSetDB() {
        return setDB;
    }

    public ColorDatabase getColorDB() {
        return colorDB;
    }

    public UserPartDatabase getUserPartDB() {
        return userPartDB;
    }

    public Database() throws IOException {
        partDB.loadCSV("pieces.csv");
        setDB.loadCSV("set_pieces.csv");
        colorDB.loadCSV("colors.csv");
        partDB.loadSetsStatistics(setDB);
        userPartDB = new UserPartDatabase(setDB);
        userPartDB.loadSetTSV("rebrickable_sets_basebrick.tsv");
        userPartDB.loadPartCSV("rebrickable_parts_myparts.csv");
        for (Set set: setDB.getValues()) {
            for (SetPart setPart: set.getParts()) {
                HashSet<String> colors = partColors.get(setPart.getPartID());
                if (colors == null) {
                    colors = new HashSet<String>();
                    partColors.put(setPart.getPartID(), colors);
                }
                colors.add(setPart.getColorID());
            }
        }
    }

    public List<Part> searchPart(String query, boolean owned, String colorID) {
        System.out.println("Search: " + query + "/" + owned + "/" + colorID);
        List<Part> r = new ArrayList<Part>();
        if (query.length() == 0) {
            return r;
        }
        String[] terms = query.toLowerCase().split("\\s+");
        for (Part part : partDB.values()) {
            String desc = part.getDescription().toLowerCase();
            if (matches(desc, terms) && existsPartInColor(part.getId(), colorID)) {
                if (part.getUsageCount() > 0 && (!owned || isPartOwnedInColor(part.getId(), colorID))) {
                    r.add(part);
                }
            }
        }
        Collections.sort(r, new FreqComparator());
        return r;
    }

    private boolean isPartOwnedInColor(String id, String colorID) {
        Map<String, List<SetPart>> partOwnershipData = userPartDB.getPartOwnershipData(id);
        if (colorID == null) {
            return partOwnershipData.size() > 0;
        }
        return (partOwnershipData.get(colorID) != null);
    }

    public boolean existsPartInColor(String partID, String colorID) {
        HashSet<String> colors = partColors.get(partID);
        if (colors == null) {
            return false;
        }
        return colorID == null || colors.contains(colorID);
    }

    private class FreqComparator implements Comparator<Part> {
        @Override
        public int compare(Part o1, Part o2) {
            return o2.getUsageCount() - o1.getUsageCount();
        }
    }

    private boolean matches(String s, String[] terms) {
        for (String term : terms) {
            if (!s.contains(term)) {
                return false;
            }
        }
        return true;
    }
}
