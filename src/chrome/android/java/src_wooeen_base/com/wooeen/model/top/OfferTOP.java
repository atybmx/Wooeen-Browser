package com.wooeen.model.top;

public class OfferTOP {

    private String q;
    private int page;
    private int qtdPerPage;
    private int idFrom;
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

    public OfferTOP withQ(String q){
        setQ(q);
        return this;
    }

    public OfferTOP withPage(int page){
        setPage(page);
        return this;
    }

    public OfferTOP withQtdPerPage(int qtdPerPage){
        setQtdPerPage(qtdPerPage);
        return this;
    }

    public OfferTOP withIdFrom(int idFrom){
        setIdFrom(idFrom);
        return this;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public int getIdFrom() {
        return idFrom;
    }

    public void setIdFrom(int idFrom) {
        this.idFrom = idFrom;
    }

}
