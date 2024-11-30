package com.example.btimggallery;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btimggallery.adapters.ImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<String> imagePaths = new ArrayList<>();
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new ImageAdapter(this, imagePaths);
        recyclerView.setAdapter(adapter);

        loadImages();
    }

    private void loadImages() {
        // Thêm ảnh từ thư mục drawable
        imagePaths.add("android.resource://" + getPackageName() + "/" + R.drawable.a);
        imagePaths.add("android.resource://" + getPackageName() + "/" + R.drawable.b);
        imagePaths.add("android.resource://" + getPackageName() + "/" + R.drawable.c);
        imagePaths.add("android.resource://" + getPackageName() + "/" + R.drawable.d);
        imagePaths.add("android.resource://" + getPackageName() + "/" + R.drawable.e);
        imagePaths.add("android.resource://" + getPackageName() + "/" + R.drawable.f);
        // Tiếp tục thêm các ảnh cần thiết...

        // Lấy ảnh từ MediaStore (bộ nhớ ngoài)
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                imagePaths.add(path);
            }
            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }

}
