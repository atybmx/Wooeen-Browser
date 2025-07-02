package com.wooeen.model.to;

import java.util.ArrayList;
import java.util.List;

public class AdvertiserDetailTO {
    private int id;
    private String name;
    private String color;
    private MediaTO logo;
    private String url;
    private String domain;
    private String omniboxTitle;
    private String omniboxDescription;
    private String seoTitle;
    private String seoDescription;
    private String urlQuery;
    private AdvertiserStatsTO stats;

    public AdvertiserDetailTO(){
    }
    public AdvertiserDetailTO(int id){
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

    public MediaTO getLogo() {
        return logo;
    }

    public void setLogo(MediaTO logo) {
        this.logo = logo;
    }

    public String getUrlQuery() {
        return urlQuery;
    }

    public void setUrlQuery(String urlQuery) {
        this.urlQuery = urlQuery;
    }

    public AdvertiserStatsTO getStats() {
        return stats;
    }

    public void setStats(AdvertiserStatsTO stats) {
        this.stats = stats;
    }

    public String getSeoTitle() {
        return seoTitle;
    }

    public void setSeoTitle(String seoTitle) {
        this.seoTitle = seoTitle;
    }

    public String getSeoDescription() {
        return seoDescription;
    }

    public void setSeoDescription(String seoDescription) {
        this.seoDescription = seoDescription;
    }
}
