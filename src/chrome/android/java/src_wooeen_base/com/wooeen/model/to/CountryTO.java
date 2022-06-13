package com.wooeen.model.to;

public class CountryTO {

    private String id;
    private String name;
    private String language;
    private String imageUrl;
    private String currency;
    private boolean loadPosts;
    private boolean loadOffers;
    private boolean loadCoupons;
    private boolean loadTasks;

    public CountryTO(){

    }

    public CountryTO(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public boolean getLoadPosts() {
        return loadPosts;
    }
    public void setLoadPosts(boolean loadPosts) {
        this.loadPosts = loadPosts;
    }
    public boolean getLoadOffers() {
        return loadOffers;
    }
    public void setLoadOffers(boolean loadOffers) {
        this.loadOffers = loadOffers;
    }
    public boolean getLoadCoupons() {
        return loadCoupons;
    }
    public void setLoadCoupons(boolean loadCoupons) {
        this.loadCoupons = loadCoupons;
    }
    public boolean getLoadTasks() {
        return loadTasks;
    }
    public void setLoadTasks(boolean loadTasks) {
        this.loadTasks = loadTasks;
    }

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
