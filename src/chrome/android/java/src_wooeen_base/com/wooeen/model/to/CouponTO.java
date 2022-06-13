package com.wooeen.model.to;

import java.util.Date;

public class CouponTO {

    private int id;
    private int advertiserId;
    private String advertiserName;
    private String advertiserColor;
    private String title;
    private String description;
    private String url;
    private String voucher;
    private double discount;
    private String discountType;
    private String media;
    private Date dateExpiration;
    private String timezoneExpiration;

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

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
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
