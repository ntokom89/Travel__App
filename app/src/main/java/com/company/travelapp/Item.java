package com.company.travelapp;

public class Item {
    private String itemId;
    private String categoryID;
    private String nameItem;
    private String descriptionItem;
    private String dateAquiredItem;
    private int imageItem;
    private String imageUri;

    public Item(String categoryID, String nameItem, String descriptionItem, String dateAquiredItem, int imageItem) {
        this.categoryID = categoryID;
        this.nameItem = nameItem;
        this.descriptionItem = descriptionItem;
        this.dateAquiredItem = dateAquiredItem;
        this.imageItem = imageItem;
    }

    public Item() {
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
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

    public int getImageItem() {
        return imageItem;
    }

    public void setImageItem(int imageItem) {
        this.imageItem = imageItem;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
