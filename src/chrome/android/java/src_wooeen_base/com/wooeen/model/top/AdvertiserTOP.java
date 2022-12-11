package com.wooeen.model.top;

public class AdvertiserTOP {

    private int id;
    private String domain;
    private String q;
    private int page;
    private int qtdPerPage;
    private String orderBy;


    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getQtdPerPage() {
        return qtdPerPage;
    }

    public void setQtdPerPage(int qtdPerPage) {
        this.qtdPerPage = qtdPerPage;
    }

    public AdvertiserTOP withQ(String q){
        setQ(q);
        return this;
    }

    public AdvertiserTOP withPage(int page){
        setPage(page);
        return this;
    }

    public AdvertiserTOP withQtdPerPage(int qtdPerPage){
        setQtdPerPage(qtdPerPage);
        return this;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
