package com.example.btimggallery;
import android.Manifest;  // Thêm dòng này

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btimggallery.adapters.ImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<String> imagePaths = new ArrayList<>();
    private ImageAdapter adapter;

    private static final int REQUEST_PERMISSION = 1; // Mã yêu cầu quyền

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new ImageAdapter(this, imagePaths);
        recyclerView.setAdapter(adapter);

        // Kiểm tra và yêu cầu quyền truy cập bộ nhớ
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Hiển thị lý do cho người dùng trước khi yêu cầu quyền lại
                    Toast.makeText(this, "Ứng dụng cần quyền truy cập bộ nhớ để hiển thị ảnh", Toast.LENGTH_LONG).show();
                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            } else {
                // Nếu quyền đã được cấp, tải ảnh ngay
                loadImages();
            }
        } else {
            // Đối với Android 10 (API 29) và trước đó, yêu cầu quyền thông thường
            loadImages();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Nếu quyền được cấp, tải ảnh từ bộ nhớ ngoài
                loadImages();
            } else {
                // Quyền bị từ chối, tải ảnh từ drawable
                Toast.makeText(this, "Quyền truy cập bộ nhớ bị từ chối, sử dụng ảnh mặc định", Toast.LENGTH_SHORT).show();
                // Thêm ảnh từ drawable vào imagePaths
                loadDefaultImages();
            }
        }
    }

    private void loadDefaultImages() {
        imagePaths.add("android.resource://" + getPackageName() + "/" + R.drawable.a);
        imagePaths.add("android.resource://" + getPackageName() + "/" + R.drawable.b);
        imagePaths.add("android.resource://" + getPackageName() + "/" + R.drawable.c);
        imagePaths.add("android.resource://" + getPackageName() + "/" + R.drawable.d);
        imagePaths.add("android.resource://" + getPackageName() + "/" + R.drawable.e);
        imagePaths.add("android.resource://" + getPackageName() + "/" + R.drawable.f);
        // Tiếp tục thêm các ảnh cần thiết...

        adapter.notifyDataSetChanged(); // Cập nhật lại adapter
    }


    private void loadImages() {
        // Lấy ảnh từ MediaStore (bộ nhớ ngoài) nếu có quyền truy cập
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
                imagePaths.add(path); // Thêm đường dẫn ảnh vào danh sách
            }
            cursor.close();
        }

        adapter.notifyDataSetChanged(); // Cập nhật adapter sau khi đã có ảnh
    }
}
