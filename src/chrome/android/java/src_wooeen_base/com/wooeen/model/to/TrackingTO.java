package com.wooeen.model.to;

public class TrackingTO {

    private int id;
    private int platformId;
    private int advertiserId;
    private int advertiserType;
    private String deeplink;
    private String params;
    private String domain;
    private int priority;

    private double payout;
    private String commissionType;
    private double commissionAvg1;
    private double commissionMin1;
    private double commissionMax1;
    private double commissionAvg2;
    private double commissionMin2;
    private double commissionMax2;
    private int approvalDays;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeeplink() {
        return deeplink;
    }

    public void setDeeplink(String deeplink) {
        this.deeplink = deeplink;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    public int getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(int advertiserId) {
        this.advertiserId = advertiserId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public double getPayout() {
        return payout;
    }

    public void setPayout(double payout) {
        this.payout = payout;
    }

    public String getCommissionType() {
        return commissionType;
    }

    public void setCommissionType(String commissionType) {
        this.commissionType = commissionType;
    }

    public double getCommissionAvg1() {
        return commissionAvg1;
    }

    public void setCommissionAvg1(double commissionAvg1) {
        this.commissionAvg1 = commissionAvg1;
    }

    public double getCommissionMin1() {
        return commissionMin1;
    }

    public void setCommissionMin1(double commissionMin1) {
        this.commissionMin1 = commissionMin1;
    }

    public double getCommissionMax1() {
        return commissionMax1;
    }

    public void setCommissionMax1(double commissionMax1) {
        this.commissionMax1 = commissionMax1;
    }

    public double getCommissionAvg2() {
        return commissionAvg2;
    }

    public void setCommissionAvg2(double commissionAvg2) {
        this.commissionAvg2 = commissionAvg2;
    }

    public double getCommissionMin2() {
        return commissionMin2;
    }

    public void setCommissionMin2(double commissionMin2) {
        this.commissionMin2 = commissionMin2;
    }

    public double getCommissionMax2() {
        return commissionMax2;
    }

    public void setCommissionMax2(double commissionMax2) {
        this.commissionMax2 = commissionMax2;
    }

    public int getApprovalDays() {
        return approvalDays;
    }

    public void setApprovalDays(int approvalDays) {
        this.approvalDays = approvalDays;
    }

    public int getAdvertiserType() {
        return advertiserType;
    }

    public void setAdvertiserType(int advertiserType) {
        this.advertiserType = advertiserType;
    }
}
