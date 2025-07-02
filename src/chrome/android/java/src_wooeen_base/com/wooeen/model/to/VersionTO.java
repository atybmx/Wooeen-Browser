package com.wooeen.model.to;

public class VersionTO {

    private int checkout;
    private int product;
    private int query;
    private int pub;
    private String android;

    public int getQuery() {
        return query;
    }

    public void setQuery(int query) {
        this.query = query;
    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public int getCheckout() {
        return checkout;
    }

    public void setCheckout(int checkout) {
        this.checkout = checkout;
    }

    public int getPub() {
        return pub;
    }

    public void setPub(int pub) {
        this.pub = pub;
    }

    public String getAndroid(){
        return android;
    }

    public void setAndroid(String android){
        this.android = android;
    }
}
