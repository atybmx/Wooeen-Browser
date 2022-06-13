package com.wooeen.model.top;

public class TaskTOP {

    private String q;
    private int page;
    private int qtdPerPage;
    private int idFrom;
    private String checkout;
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

    public TaskTOP withQ(String q){
        setQ(q);
        return this;
    }

    public TaskTOP withPage(int page){
        setPage(page);
        return this;
    }

    public TaskTOP withQtdPerPage(int qtdPerPage){
        setQtdPerPage(qtdPerPage);
        return this;
    }

    public TaskTOP withIdFrom(int idFrom){
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

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }
}
