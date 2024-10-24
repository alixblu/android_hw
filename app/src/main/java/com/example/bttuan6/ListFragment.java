package com.example.bttuan6;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ListFragment extends Fragment {
    private ListView customListView;
    private CustomAdapter customAdapter;
    private ArrayList<Points> pointsArrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list, container, false);
        customListView = view.findViewById(R.id.customListview);

        // Find the ImageView and set the PopupMenu
        ImageView detailIcon = view.findViewById(R.id.imageView);
        detailIcon.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            popupMenu.getMenu().add("Export");
            popupMenu.getMenu().add("Import");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Export")) {
                    // Handle Export logic here
                    Toast.makeText(getContext(), "Export clicked", Toast.LENGTH_SHORT).show();
                } else if (item.getTitle().equals("Import")) {
                    // Handle Import logic here
                    Toast.makeText(getContext(), "Import clicked", Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            popupMenu.show();
        });

        // Load data from database
        pointsArrayList = loadDataFromDatabase();
        customAdapter = new CustomAdapter(getContext(), pointsArrayList);
        customListView.setAdapter(customAdapter);
        return view;
    }

    // Method to load data from the database
    private ArrayList<Points> loadDataFromDatabase() {
        DataBase db = new DataBase(getContext());
        ArrayList<Points> pointsList = new ArrayList<>();

        Cursor cursor = db.getAllPoints();
        if (cursor.moveToFirst()) {
            do {
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("sdt"));
                String point = cursor.getString(cursor.getColumnIndexOrThrow("point"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                String curDate = cursor.getString(cursor.getColumnIndexOrThrow("cur_date"));
                String createDate = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                // Add the point object to the list
                pointsList.add(new Points(phoneNumber, point, note, curDate, createDate));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return pointsList;
    }
}
