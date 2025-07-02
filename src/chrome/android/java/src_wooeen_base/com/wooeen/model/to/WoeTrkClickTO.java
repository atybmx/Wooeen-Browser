package com.wooeen.model.to;

public class WoeTrkClickTO {

    private int user;
    private int source;
    private int link;
    private String dateClick;
    private String country;
    private int advertiser;
    private int task;

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getLink() {
        return link;
    }

    public void setLink(int link) {
        this.link = link;
    }

    public String getDateClick() {
        return dateClick;
    }

    public void setDateClick(String dateClick) {
        this.dateClick = dateClick;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    
    public int getAdvertiser() {
        return advertiser;
    }

    public void setAdvertiser(int advertiser) {
        this.advertiser = advertiser;
    }
    
    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }

    public static enum Event{
        IMPRESSIONS(-2),
        CLICKS(-1),
        INSTALLS(-3),
        UNINSTALLS(-4);

        private int value;

        private Event(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public boolean equals(int value) {
            return this.value == value;
        }
    }
}
