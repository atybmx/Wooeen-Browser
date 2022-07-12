package com.wooeen.model.to;

public class WoeTrkClickTO {

    private int user;
    private int source;
    private int link;
    private String dateClick;

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

    }
}
