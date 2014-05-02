package net.cryptoland.bricksearch;

public class Part {
    private String id;
    private String description;
    private int usageCount = 0;

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Part(String id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public String toString() {
        return id + " - " + usageCount + " - " + description;
    }
}
