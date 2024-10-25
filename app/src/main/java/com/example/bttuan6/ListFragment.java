package com.example.bttuan6;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.io.InputStream; // Để sử dụng InputStream
import org.xmlpull.v1.XmlPullParser; // Để sử dụng XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory; // Để sử dụng XmlPullParserFactory

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ListFragment extends Fragment {
    private ListView customListView;
    private CustomAdapter customAdapter;
    private DataBase db;
    private StringBuilder xmlBuilder = new StringBuilder();

    private ArrayList<Points> pointsArrayList;
    private boolean isExporting = false; // Declare isExporting

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
                    exportDataToXml();
                    sendEmailWithXml();
                    isExporting = true;
                } else if (item.getTitle().equals("Import")) {
                    // Mở file explorer để chọn file XML
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("text/xml");
                    isExporting = false;
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_XML_FILE_REQUEST && resultCode == getActivity().RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                importDataFromXml(uri);
            }
        }
    }

    private void importDataFromXml(Uri uri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(inputStream, null);

                Points point = null;
                ArrayList<Points> importedPointsList = new ArrayList<>();
                int eventType = parser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String name = parser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (name.equals("entry")) {
                                point = new Points(); // Khởi tạo đối tượng Points mới
                            } else if (point != null) {
                                if (name.equals("sdt")) {
                                    point.setSdt(parser.nextText());
                                } else if (name.equals("point")) {
                                    point.setPoint(parser.nextText());
                                } else if (name.equals("note")) {
                                    point.setNote(parser.nextText());
                                } else if (name.equals("cur_date")) {
                                    point.setCur_date(parser.nextText());
                                } else if (name.equals("time")) {
                                    point.setTime_create(parser.nextText());
                                }
                            }
                            break;

                        case XmlPullParser.END_TAG:
                            if (name.equals("entry") && point != null) {
                                importedPointsList.add(point); // Thêm điểm vào danh sách
                                db.addPoint(point); // Thêm điểm vào cơ sở dữ liệu
                            }
                            break;
                    }
                    eventType = parser.next();
                }

                Toast.makeText(getContext(), "Nhập dữ liệu thành công!", Toast.LENGTH_SHORT).show();
                // Cập nhật lại danh sách sau khi nhập
                pointsArrayList.clear(); // Xóa danh sách hiện tại
                pointsArrayList.addAll(loadDataFromDatabase()); // Tải lại dữ liệu từ cơ sở dữ liệu
                customAdapter.notifyDataSetChanged(); // Cập nhật adapter

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi khi nhập dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_XML_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                if (isExporting) {
                    // We are in export mode
                    saveXmlToFile(uri); // Export to the selected file
                } else {
                    // We are in import mode
                    importDataFromXml(uri); // Import XML data
                }
            }
        }
    }
    private void saveXmlToFile(Uri uri) {
        try {
            OutputStream outputStream = getActivity().getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(xmlBuilder.toString().getBytes());  // Ensure the content is written
                outputStream.flush(); // Force writing the data
                outputStream.close();
                Toast.makeText(getContext(), "Data exported successfully!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Export Error", "Error exporting data: " + e.getMessage());
            Toast.makeText(getContext(), "Error exporting data", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, proceed with exporting
                exportDataToXml();
            } else {
                // Permission denied, show a message
                Toast.makeText(getContext(), "Storage permission is required to export data", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
