package com.wooeen.model.to;

import java.util.Date;

public class NavigationTO {

    private int id;
    private NavAdvertiserTOA advertiser;
    private boolean favorite;
    private int status;
    private String prodid;
    private String gtin;
    private String title;
    private String url;
    private String category;
    private String image;
    private double amount;
    private double amountOld;
    private double rating;
    private int totalReviews;
    private Date dateRegister;

    public double getAmountOld() {
        return amountOld;
    }

    public void setAmountOld(double amountOld) {
        this.amountOld = amountOld;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public static class NavAdvertiserTOA{
        private int id;
        private String name;
        private String color;
        private String logo;
        private String deeplink;
        private String params;
        public NavAdvertiserTOA() {
        }
        public NavAdvertiserTOA(int id) {
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
        public String getDeeplink() {
            return deeplink;
        }
        public void setDeeplink(String deeplink) {
            this.deeplink = deeplink;
        }
        public String getParams() {
            return params;
        }
        public void setParams(String params) {
            this.params = params;
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
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NavAdvertiserTOA getAdvertiser() {
        return advertiser;
    }

    public void setAdvertiser(NavAdvertiserTOA advertiser) {
        this.advertiser = advertiser;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getProdid() {
        return prodid;
    }

    public void setProdid(String prodid) {
        this.prodid = prodid;
    }

    public String getGtin() {
        return gtin;
    }

    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDateRegister() {
        return dateRegister;
    }

    public void setDateRegister(Date dateRegister) {
        this.dateRegister = dateRegister;
    }

}
