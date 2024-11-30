package com.example.btimggallery;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.btimggallery.adapters.DetailAdapter;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private DetailAdapter adapter;
    private List<String> imagePaths;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        viewPager = findViewById(R.id.viewPager);

        // Lấy dữ liệu từ Intent
        imagePaths = getIntent().getStringArrayListExtra("imagePaths");
        position = getIntent().getIntExtra("position", 0);

        // Set adapter
        adapter = new DetailAdapter(this, imagePaths);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        // Hiệu ứng chuyển động
        viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                page.setAlpha(1 - Math.abs(position));
                page.setTranslationX(-position * page.getWidth());
            }
        });

        // Gesture để xử lý 3 ngón tay
        setupThreeFingerSwipe();
    }

    private void setupThreeFingerSwipe() {
        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (e2.getPointerCount() == 3) { // Kiểm tra 3 ngón tay
                    if (distanceX > 0) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true); // Chuyển qua phải
                    } else {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true); // Chuyển qua trái
                    }
                    return true;
                }
                return false;
            }
        });

        viewPager.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }
}
