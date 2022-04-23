package com.company.travelapp;

public class Collection {

    private String nameCollection;
    private int imageCollection;

    public Collection(String nameCollection, int imageCollection) {
        this.nameCollection = nameCollection;
        this.imageCollection = imageCollection;
    }

    public Collection() {
    }

    public String getNameCollection() {
        return nameCollection;
    }

    public void setNameCollection(String nameCollection) {
        this.nameCollection = nameCollection;
    }

    public int getImageCollection() {
        return imageCollection;
    }

    public void setImageCollection(int imageCollection) {
        this.imageCollection = imageCollection;
    }
}
