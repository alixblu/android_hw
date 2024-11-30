package com.example.bttuan7;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import java.util.List;

public class ImageAdapter extends ArrayAdapter<PhotoItem> {
    private final List<PhotoItem> photoItems;
    private final LayoutInflater inflater;

    public ImageAdapter(Context context, List<PhotoItem> photoItems) {
        super(context, R.layout.activity_main, photoItems);
        this.photoItems = photoItems;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_list_view, parent, false); // Use list_item.xml
        }

        CheckBox checkBox = convertView.findViewById(R.id.checkBox);
        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textView = convertView.findViewById(R.id.textView);

        PhotoItem item = photoItems.get(position);
        imageView.setImageBitmap(item.getBitmap());
        textView.setText(item.getTimestamp()); // Show the timestamp here
        checkBox.setChecked(item.isSelected());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> item.setSelected(isChecked));

        return convertView;
    }

}
