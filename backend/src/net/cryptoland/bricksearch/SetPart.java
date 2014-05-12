package net.cryptoland.bricksearch;

public class SetPart {
    private String partID;
    private int quantity;
    private String colorID;
    private String type;
    private String setID;

    public SetPart(String partID, int quantity, String colorID, String type, String setID) {
        this.partID = partID;
        this.quantity = quantity;
        this.colorID = colorID;
        this.type = type;
        this.setID = setID;
    }

    public String getPartID() {
        return partID;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getColorID() {
        return colorID;
    }

    public String getType() {
        return type;
    }

    public String getSetID() {
        return setID;
    }
}
