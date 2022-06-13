package com.wooeen.model.to;

import java.util.Date;

public class TaskTO {

    private int id;
    private int advertiserId;
    private int platformId;
    private String title;
    private String description;
    private String url;
    private double payout;
    private String media;
    private Date dateExpiration;
    private String timezoneExpiration;
    private CheckoutTO checkout;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public String getTimezoneExpiration() {
        return timezoneExpiration;
    }

    public void setTimezoneExpiration(String timezoneExpiration) {
        this.timezoneExpiration = timezoneExpiration;
    }

    public int getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(int advertiserId) {
        this.advertiserId = advertiserId;
    }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
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

    public double getPayout() {
        return payout;
    }

    public void setPayout(double payout) {
        this.payout = payout;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public CheckoutTO getCheckout() {
        return checkout;
    }

    public void setCheckout(CheckoutTO checkout) {
        this.checkout = checkout;
    }
}
