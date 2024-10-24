package com.example.bttuan6;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;

public class ListFragment extends Fragment {
    private ListView customListView;
    private CustomAdapter customAdapter;
    private DataBase db;
    private ArrayList<Points> pointsArrayList;
    private static final int PICK_XML_FILE_REQUEST = 1; // Request code for file picker
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
                    // Handle Export logic
                    Toast.makeText(getContext(), "Export clicked", Toast.LENGTH_SHORT).show();
                } else if (item.getTitle().equals("Import")) {
                    // Open file explorer to select XML file
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("text/xml");
                    startActivityForResult(intent, PICK_XML_FILE_REQUEST);
                }
                return true;
            });
            popupMenu.show();
        });

        // Load data from database
        pointsArrayList = loadDataFromDatabase();
        customAdapter = new CustomAdapter(getContext(), pointsArrayList);
        customListView.setAdapter(customAdapter);
        db = new DataBase(getActivity());
        return view;
    }

    // Method to load data from the database
    private ArrayList<Points> loadDataFromDatabase() {
        db = new DataBase(getContext());
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_XML_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                importDataFromXml(uri);
            }
        }
    }

    private void importDataFromXml(Uri uri) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                importDataFromXML(inputStream);
                inputStream.close(); // Ensure to close the stream
            }
        } catch (Exception e) {
            Log.e("File Import", "Error: " + e.getMessage());
        }
    }

    private void importDataFromXML(InputStream inputStream) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            Points point = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if (tagName.equals("entry")) {
                            point = new Points();
                        } else if (point != null) {
                            switch (tagName) {
                                case "sdt":
                                    point.setSdt(parser.nextText());
                                    break;
                                case "point":
                                    point.setPoint(parser.nextText());
                                    break;
                                case "note":
                                    point.setNote(parser.nextText());
                                    break;
                                case "cur_date":
                                    point.setCur_date(parser.nextText());
                                    break;
                                case "time":
                                    point.setTime_create(parser.nextText());
                                    break;
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("entry") && point != null) {
                            db.addPoint(point);
                        }
                        break;
                }
                eventType = parser.next();

            }
            pointsArrayList.clear();
            pointsArrayList.addAll(loadDataFromDatabase());
            customAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e("XML Parsing", "Error: " + e.getMessage());
        }
    }


}
