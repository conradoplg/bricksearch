package net.cryptoland.bricksearch;

public class SetPart {
    private String partID;
    private int quantity;
    private String colorID;
    private String type;

    public SetPart(String partID, int quantity, String colorID, String type) {
        this.partID = partID;
        this.quantity = quantity;
        this.colorID = colorID;
        this.type = type;
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
}
