package net.cryptoland.bricksearch;

import java.util.List;

public class Set {
    String id;
    List<SetPart> parts;
    String name;

    public String getId() {
        return id;
    }

    public List<SetPart> getParts() {
        return parts;
    }

    public Set(String id, List<SetPart> parts, String name) {
        this.id = id;
        this.parts = parts;
        this.name = name;
    }
}
