package net.cryptoland.bricksearch;

import java.io.IOException;
import java.util.*;

public class Database {
    private PartDatabase partDB = new PartDatabase();
    private SetDatabase setDB = new SetDatabase();
    private ColorDatabase colorDB = new ColorDatabase();
    private UserPartDatabase userPartDB;
    private HashMap<String, HashSet<String>> partColors = new HashMap<String, HashSet<String>>();
    private String[] sortedColorsID;

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
        final HashMap<String, Integer> colorFreq = new HashMap<String, Integer>();
        for (String id: colorDB.getIDs()) {
            colorFreq.put(id, 0);
        }
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
        for (String partID: partColors.keySet()) {
            for (String colorID: partColors.get(partID)) {
                colorFreq.put(colorID, colorFreq.get(colorID) + 1);
            }
        }
        sortedColorsID = colorFreq.keySet().toArray(new String[0]);
        Arrays.sort(sortedColorsID, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return colorFreq.get(o2) - colorFreq.get(o1);
            }
        });
    }

    public List<Part> searchPart(String query, boolean owned, String colorID) {
        List<Part> r = new ArrayList<Part>();
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

    public String[] getSortedColorIDs() {
        return sortedColorsID;
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
