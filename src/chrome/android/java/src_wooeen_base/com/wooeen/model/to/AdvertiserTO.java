package com.wooeen.model.to;

public class AdvertiserTO {
    private int id;
    private int type;
    private String name;
    private String color;
    private String logo;
    private String url;
    private String domain;
    private CheckoutTO checkout;
    private CheckoutTO product;
    private CheckoutTO query;
    private String omniboxTitle;
    private String omniboxDescription;

    public AdvertiserTO(){
    }
    public AdvertiserTO(int id){
        this.id = id;
    }

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public CheckoutTO getCheckout() {
        return checkout;
    }

    public void setCheckout(CheckoutTO checkout) {
        this.checkout = checkout;
    }

    public String getOmniboxTitle() {
        return omniboxTitle;
    }

    public void setOmniboxTitle(String omniboxTitle) {
        this.omniboxTitle = omniboxTitle;
    }

    public String getOmniboxDescription() {
        return omniboxDescription;
    }

    public void setOmniboxDescription(String omniboxDescription) {
        this.omniboxDescription = omniboxDescription;
    }

    public CheckoutTO getQuery() {
        return query;
    }

    public void setQuery(CheckoutTO query) {
        this.query = query;
    }

    public CheckoutTO getProduct() {
        return product;
    }

    public void setProduct(CheckoutTO product) {
        this.product = product;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
