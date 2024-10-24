package com.example.bttuan6;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
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
                    exportDataToXml();
                    sendEmailWithXml();
                } else if (item.getTitle().equals("Import")) {
                    // Handle Import logic here
                    Toast.makeText(getContext(), "Import clicked", Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            popupMenu.show();
        });

        pointsArrayList = loadDataFromDatabase();
        customAdapter = new CustomAdapter(getContext(), pointsArrayList);
        customListView.setAdapter(customAdapter);

        return view;
    }

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
                String time_create = cursor.getString(cursor.getColumnIndexOrThrow("time"));

                pointsList.add(new Points(phoneNumber, point, note, curDate, time_create));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return pointsList;
    }

    private void exportDataToXml() {
        pointsArrayList = loadDataFromDatabase();
        if (pointsArrayList.isEmpty()) {
            Toast.makeText(getContext(), "Danh sách rỗng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo nội dung XML
        StringBuilder xmlContent = new StringBuilder();
        xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlContent.append("<PointsList>\n");

        for (Points point : pointsArrayList) {
            xmlContent.append("  <Point>\n");
            xmlContent.append("    <PhoneNumber>").append(point.getSdt()).append("</PhoneNumber>\n");
            xmlContent.append("    <PointValue>").append(point.getPoint()).append("</PointValue>\n");
            xmlContent.append("    <Note>").append(point.getNote()).append("</Note>\n");
            xmlContent.append("    <CurDate>").append(point.getCur_date()).append("</CurDate>\n");
            xmlContent.append("  </Point>\n");
        }

        xmlContent.append("</PointsList>");

        File exportDir = new File(getContext().getExternalFilesDir(null), "EXPORT");
        if (!exportDir.exists() && !exportDir.mkdirs()) {
            Toast.makeText(getContext(), "Không thể tạo thư mục EXPORT", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(exportDir, "pointkhachhang.xml");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream)) {
            writer.write(xmlContent.toString());
            writer.flush();
            Toast.makeText(getContext(), "Export thành công! File tại: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi khi export: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmailWithXml() {
        File file = new File(getContext().getExternalFilesDir(null) + "/EXPORT/pointkhachhang.xml");

        if (!file.exists() || !file.canRead()) {
            Toast.makeText(getContext(), "File không tồn tại hoặc không thể đọc", Toast.LENGTH_SHORT).show();
            return;
        }

        // URI của file, sử dụng FileProvider để tránh lỗi khi chia sẻ file
        Uri uri = FileProvider.getUriForFile(getContext(), "com.example.bttuan6.fileprovider", file);

        // Tạo intent để gửi email
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("application/xml");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Danh sách điểm");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Đính kèm file XML chứa danh sách quản lí điểm.");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);  // Đính kèm file XML

        startActivity(Intent.createChooser(emailIntent, "Chọn ứng dụng email:"));
    }

}
