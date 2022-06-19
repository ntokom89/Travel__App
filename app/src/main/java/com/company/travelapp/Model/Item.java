package com.company.travelapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
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

    protected Item(Parcel in) {
        itemId = in.readString();
        categoryID = in.readString();
        nameItem = in.readString();
        descriptionItem = in.readString();
        dateAquiredItem = in.readString();
        imageItem = in.readInt();
        imageUri = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemId);
        dest.writeString(categoryID);
        dest.writeString(nameItem);
        dest.writeString(descriptionItem);
        dest.writeString(dateAquiredItem);
        dest.writeInt(imageItem);
        dest.writeString(imageUri);
    }
}
