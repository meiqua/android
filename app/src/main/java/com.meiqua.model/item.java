//this class defined the item to be used
//an item is a book
package com.meiqua.model;


public class item {
    private String author;
    private String thumbnailUrl;
    private String title;
    private String location;
    private boolean collected=false;
    private String id="0";

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private int state=0;

    public item() {
    }

    public item(String id, String location, String title, String thumbnailUrl, String author) {
        this.id = id;
        this.location = location;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
