package net.cryptoland.bricksearch;

import java.util.List;

public class Set {
    String id;
    List<SetPart> parts;

    public String getId() {
        return id;
    }

    public List<SetPart> getParts() {
        return parts;
    }

    public Set(String id, List<SetPart> parts) {
        this.id = id;
        this.parts = parts;
    }
}
