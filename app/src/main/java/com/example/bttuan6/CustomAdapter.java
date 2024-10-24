package com.example.bttuan6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Points> pointsList;
    private LayoutInflater inflater;

    public CustomAdapter(Context context, ArrayList<Points> pointsList) {
        this.context = context;
        this.pointsList = pointsList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return pointsList.size();
    }

    @Override
    public Object getItem(int position) {
        return pointsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_custom_list, parent, false);
        }

        // Get the current point
        Points currentPoint = pointsList.get(position);

        // Bind data to the view
        TextView phoneNumber = convertView.findViewById(R.id.phonenumber);
        TextView timeCreate = convertView.findViewById(R.id.timecreate);
        TextView score = convertView.findViewById(R.id.score);
        TextView timeEdit = convertView.findViewById(R.id.timeedit);
        ImageView deleteIcon = convertView.findViewById(R.id.delete_icon);
        TextView note = convertView.findViewById(R.id.note);

        phoneNumber.setText(currentPoint.getSdt());
        timeCreate.setText(currentPoint.getTime_create()); // assuming cur_date is creation date
        score.setText(currentPoint.getPoint());
        timeEdit.setText(currentPoint.getCur_date()); // You might want to store and use edit date here
        note.setText(currentPoint.getNote());
        // Handle delete icon click (optional)
        deleteIcon.setOnClickListener(view -> {
            // Code to handle delete action
        });
        deleteIcon.setOnClickListener(view -> {
            // Get the phone number to delete
            String phoneNumberToDelete = currentPoint.getSdt();

            // Delete from database
            DataBase db = new DataBase(context);
            db.deletePointByPhoneNumber(phoneNumberToDelete);

            // Remove the item from the list
            pointsList.remove(position);

            // Notify adapter about the change
            notifyDataSetChanged();
        });

        return convertView;
    }

}
