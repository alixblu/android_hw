package com.example.bttuan7;

import android.graphics.Bitmap;

public class PhotoItem {
    private int id;
    private Bitmap bitmap;
    private String timestamp;
    private boolean isSelected;

    public PhotoItem(int id, Bitmap bitmap, String timestamp) {
        this.id = id;
        this.bitmap = bitmap;
        this.timestamp = timestamp;
        this.isSelected = false;
    }

    public int getId() {
        return id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

