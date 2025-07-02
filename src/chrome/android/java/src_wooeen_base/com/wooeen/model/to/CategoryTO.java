package com.wooeen.model.to;

public class CategoryTO {
    private int id;
    private String name;
    private String description;
    private int totalAdvertisers;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalAdvertisers() {
        return totalAdvertisers;
    }

    public void setTotalAdvertisers(int totalAdvertisers) {
        this.totalAdvertisers = totalAdvertisers;
    }
}
