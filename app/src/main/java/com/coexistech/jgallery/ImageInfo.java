package com.coexistech.jgallery;

import android.graphics.Bitmap;

class ImageInfo {
    private int imageId;
    private String imagePathString;
    /****************************/

    private String imageTitleString;

    public ImageInfo(String imagePath, String imageTitle) {
        imagePathString = imagePath;
        imageTitleString = imageTitle;
    }

    public ImageInfo(String imagePath) {
        imagePathString = imagePath;
    }

    public ImageInfo() {

    }

    public ImageInfo(int id, String imagePath) {
        imageId = id;
        imagePathString = imagePath;
    }

    public String getImagePathString() {
        return imagePathString;
    }

    public void setImagePathString(String imagePathString) {
        this.imagePathString = imagePathString;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImageTitleString() {
        return imageTitleString;
    }

    public void setImageTitleString(String imageTitleString) {
        this.imageTitleString = imageTitleString;
    }

}
