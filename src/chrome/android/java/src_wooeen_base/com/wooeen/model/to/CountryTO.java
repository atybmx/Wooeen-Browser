package com.wooeen.model.to;

import java.io.Serializable;

public class CountryTO implements Serializable {

    private static final long serialVersionUID = -7421241539173001135L;

    private String id;
    private String name;
    private String language;
    private String imageUrl;
    private CurrencyTO currency;
    private boolean loadPosts;
    private boolean loadOffers;
    private boolean loadCoupons;
    private boolean loadTasks;
    private boolean loadGames;
    private CategoryTO categoryB2b;
    private String searchHint;

    public CountryTO(){

    }

    public CountryTO(String id){
        this.id = id;
    }

    public CountryTO(String id, String image){
        this.id = id; this.imageUrl = image;
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

    public CurrencyTO getCurrency() {
        return currency;
    }
    public void setCurrency(CurrencyTO currency) {
        this.currency = currency;
    }

    public boolean getLoadGames() {
        return loadGames;
    }

    public void setLoadGames(boolean loadGames) {
        this.loadGames = loadGames;
    }

    public CategoryTO getCategoryB2b() {
        return categoryB2b;
    }

    public void setCategoryB2b(CategoryTO categoryB2b) {
        this.categoryB2b = categoryB2b;
    }

    public String getSearchHint() {
        return searchHint;
    }

    public void setSearchHint(String searchHint) {
        this.searchHint = searchHint;
    }
}
