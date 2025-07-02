package com.wooeen.model.to;

import java.util.Date;

public class PostTO {

    private int id;
    private String title;
    private String excerpt;
    private String image;
    private String link;
    private Date date;
    private String authorId;
    private String authorName;
    private String authorPhoto;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExcerpt() {
        return excerpt;
    }
    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }
    public String getAuthorId() {
        return authorId;
    }
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    public String getAuthorPhoto() {
        return authorPhoto;
    }
    public void setAuthorPhoto(String authorPhoto) {
        this.authorPhoto = authorPhoto;
    }
}
