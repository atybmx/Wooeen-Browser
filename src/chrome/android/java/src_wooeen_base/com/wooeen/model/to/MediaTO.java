package com.wooeen.model.to;

public class MediaTO {

    private int id;
    private boolean dash;
    private String objectKey;
    private String name;
    private int width;
    private int height;
    private long size;
    private String url;

    public MediaTO() {
    }
    public MediaTO(int id) {
        this.id = id;
    }

    public MediaTO(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getObjectKey() {
        return objectKey;
    }
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public boolean isDash() {
        return dash;
    }
    public void setDash(boolean dash) {
        this.dash = dash;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
