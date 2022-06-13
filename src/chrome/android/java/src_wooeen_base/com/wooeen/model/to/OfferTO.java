package com.wooeen.model.to;

import java.util.Date;

public class OfferTO {

    private int id;
    private int advertiserId;
    private String advertiserName;
    private String advertiserColor;
    private String title;
    private String description;
    private String url;
    private double price;
    private String media;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(int advertiserId) {
        this.advertiserId = advertiserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAdvertiserName() {
        return advertiserName;
    }

    public void setAdvertiserName(String advertiserName) {
        this.advertiserName = advertiserName;
    }

    public String getAdvertiserColor() {
        return advertiserColor;
    }

    public void setAdvertiserColor(String advertiserColor) {
        this.advertiserColor = advertiserColor;
    }
}
