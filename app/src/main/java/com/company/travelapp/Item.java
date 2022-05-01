package com.company.travelapp;

public class Item {
    private String nameCollection;
    private String nameItem;
    private String descriptionItem;
    private String dateAquiredItem;
    private int imageCollection;

    public Item(String nameCollection, String nameItem, String descriptionItem, String dateAquiredItem, int imageCollection) {
        this.nameCollection = nameCollection;
        this.nameItem = nameItem;
        this.descriptionItem = descriptionItem;
        this.dateAquiredItem = dateAquiredItem;
        this.imageCollection = imageCollection;
    }

    public String getNameCollection() {
        return nameCollection;
    }

    public void setNameCollection(String nameCollection) {
        this.nameCollection = nameCollection;
    }

    public String getNameItem() {
        return nameItem;
    }

    public void setNameItem(String nameItem) {
        this.nameItem = nameItem;
    }

    public String getDescriptionItem() {
        return descriptionItem;
    }

    public void setDescriptionItem(String descriptionItem) {
        this.descriptionItem = descriptionItem;
    }

    public String getDateAquiredItem() {
        return dateAquiredItem;
    }

    public void setDateAquiredItem(String dateAquiredItem) {
        this.dateAquiredItem = dateAquiredItem;
    }

    public int getImageCollection() {
        return imageCollection;
    }

    public void setImageCollection(int imageCollection) {
        this.imageCollection = imageCollection;
    }
}
