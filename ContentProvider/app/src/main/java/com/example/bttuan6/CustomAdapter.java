package com.example.bttuan6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
        timeCreate.setText(currentPoint.getTime_create());
        score.setText(currentPoint.getPoint());
        timeEdit.setText(currentPoint.getCur_date());
        note.setText(currentPoint.getNote());

        // Handle delete icon click
        deleteIcon.setOnClickListener(view -> {
            // Create an AlertDialog to confirm deletion
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa khách hàng này không?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Get the phone number to delete
                            String phoneNumberToDelete = currentPoint.getSdt();

                            // Delete from database
                            DataBase db = new DataBase(context);
                            db.deletePointByPhoneNumber(phoneNumberToDelete);

                            // Remove the item from the list
                            pointsList.remove(position);

                            // Notify adapter about the change
                            notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); // Close dialog on "No"
                        }
                    })
                    .show();
        });

        return convertView;
    }
}