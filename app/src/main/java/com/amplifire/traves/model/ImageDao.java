package com.amplifire.traves.model;

import android.graphics.Bitmap;


public class ImageDao {

    private boolean isButton; //String json
    private Bitmap image;
    private String imageUri;

    public ImageDao() {

    }

    public ImageDao(boolean isButton, Bitmap image, String imageUri) {
        this.isButton = isButton;
        this.image = image;
        this.imageUri = imageUri;
    }

    public boolean isButton() {
        return isButton;
    }

    public void setButton(boolean button) {
        isButton = button;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
