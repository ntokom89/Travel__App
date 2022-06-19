package com.company.travelapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Collection implements Parcelable {

    private String categoryID;
    private String categoryName;
    private String imageUri;
    private String userID;

    private ArrayList<Item> items;


    public Collection(String categoryID, String categoryName, String imageUri, String userID) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.imageUri = imageUri;
        this.userID = userID;
    }

    public Collection() {
    }

    protected Collection(Parcel in) {
        categoryID = in.readString();
        categoryName = in.readString();
        imageUri = in.readString();
        userID = in.readString();
        items = in.readArrayList(null);
    }

    public static final Creator<Collection> CREATOR = new Creator<Collection>() {
        @Override
        public Collection createFromParcel(Parcel in) {
            return new Collection(in);
        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };

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

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(categoryID);
        dest.writeString(categoryName);
        dest.writeString(imageUri);
        dest.writeString(userID);
        dest.writeList(items);
    }
}
