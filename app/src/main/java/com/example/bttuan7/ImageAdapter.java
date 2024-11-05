package com.example.bttuan7;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<PhotoItem> photoItems; // Sử dụng PhotoItem

    public ImageAdapter(Context context, List<PhotoItem> photoItems) {
        this.context = context;
        this.photoItems = photoItems;
    }

    @Override
    public int getCount() {
        return photoItems.size();
    }

    @Override
    public Object getItem(int position) {
        return photoItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200)); // Kích thước hình ảnh
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }


        return imageView;
    }
}
