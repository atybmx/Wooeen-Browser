package com.wooeen.model.to;

import java.io.Serializable;

public class CurrencyTO implements Serializable {

    private static final long serialVersionUID = 1154434811783592836L;

    private String id;
    private String name;
    private String symbol;

    public CurrencyTO() {
    }
    public CurrencyTO(String id) {
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
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

}
