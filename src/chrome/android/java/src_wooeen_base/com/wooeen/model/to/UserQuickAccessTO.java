package com.wooeen.model.to;

import java.util.Date;

public class UserQuickAccessTO {

    private int id;
    private UserTO user;
    private String validator;
    private String redirect;
    private Date expiration;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public UserTO getUser() {
        return user;
    }
    public void setUser(UserTO user) {
        this.user = user;
    }
    public String getValidator() {
        return validator;
    }
    public void setValidator(String validator) {
        this.validator = validator;
    }
    public String getRedirect() {
        return redirect;
    }
    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }
    public Date getExpiration() {
        return expiration;
    }
    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

}
