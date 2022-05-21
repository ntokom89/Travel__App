package com.company.travelapp;

public class Collection {

    private String categoryID;
    private String categoryName;
   // private int imageCollection;
    private String imageUri;
    private String userID;

    //public Collection(String categoryName, int imageCollection) {
  //      this.categoryName = categoryName;
   //     this.imageCollection = imageCollection;
  //  }

    public Collection(String categoryID, String categoryName, String imageUri, String userID) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.imageUri = imageUri;
        this.userID = userID;
    }

    public Collection() {
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    //public int getImageCollection() {
   //     return imageCollection;
   // }

    //public void setImageCollection(int imageCollection) {
    //    this.imageCollection = imageCollection;
   // }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
