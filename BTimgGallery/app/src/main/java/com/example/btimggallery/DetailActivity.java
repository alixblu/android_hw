package com.example.btimggallery;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.btimggallery.adapters.DetailAdapter;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.Arrays;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    // Mảng lưu trữ hệ số phóng to cho từng ảnh
    private float[] scaleFactors;
    private boolean isZooming = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        viewPager = findViewById(R.id.viewPager);
        List<String> imagePaths = getIntent().getStringArrayListExtra("imagePaths");
        int position = getIntent().getIntExtra("position", 0);

        // Kiểm tra imagePaths không phải null
        if (imagePaths != null) {
            // Set adapter
            DetailAdapter adapter = new DetailAdapter(this, imagePaths);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(position);
            //không trượt ảnh khi click từ thư viện
            viewPager.setCurrentItem(position, false);

            // Khởi tạo mảng lưu trữ hệ số phóng to
            scaleFactors = new float[imagePaths.size()];
            Arrays.fill(scaleFactors, 1.0f); // Mặc định không phóng to

            // Khởi tạo ScaleGestureDetector để xử lý phóng to/thu nhỏ
            scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(@NonNull ScaleGestureDetector detector) {
                    int currentItem = viewPager.getCurrentItem();
                    float scaleFactor = detector.getScaleFactor();

                    // Cập nhật hệ số phóng cho ảnh hiện tại
                    scaleFactors[currentItem] *= scaleFactor;
                    scaleFactors[currentItem] = Math.max(0.1f, Math.min(scaleFactors[currentItem], 5.0f)); // Giới hạn hệ số phóng

                    // Lấy ảnh hiện tại để áp dụng thu phóng
                    View currentImageView = viewPager.findViewWithTag("image" + currentItem);
                    if (currentImageView != null) {
                        currentImageView.setScaleX(scaleFactors[currentItem]); // Áp dụng kích thước
                        currentImageView.setScaleY(scaleFactors[currentItem]); // Áp dụng kích thước
                    }
                    return true;
                }

                @Override
                public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
                    isZooming = true; // Đánh dấu đang thu phóng
                    return true;
                }

                @Override
                public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
                    isZooming = false; // Kết thúc thu phóng
                }
            });

            // Gesture để xử lý vuốt 3 ngón tay
            gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
                    if (e2.getPointerCount() == 3 && !isZooming) {
                        if (distanceX > 0) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true); // Vuốt phải
                        } else {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true); // Vuốt trái
                        }
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean onSingleTapUp(@NonNull MotionEvent e) {
                    viewPager.performClick(); // Gọi performClick khi click được phát hiện
                    return super.onSingleTapUp(e);
                }
            });
            viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
                @Override
                public void transformPage(@NonNull View page, float position) {
                    if (position < -1) {
                        // Trang nằm ngoài bên trái
                        page.setScaleX(0.8f);
                        page.setScaleY(0.8f);
                        page.setAlpha(0.5f);
                    } else if (position <= 1) {
                        // Trang nằm trong màn hình (giữa -1 và 1)
                        float scaleFactor = Math.max(0.8f, 1 - Math.abs(position));
                        page.setScaleX(scaleFactor);
                        page.setScaleY(scaleFactor);

                        // Đặt độ mờ dần
                        page.setAlpha(0.5f + (scaleFactor - 0.8f) / 0.2f * (1 - 0.5f));
                    } else {
                        // Trang nằm ngoài bên phải
                        page.setScaleX(0.8f);
                        page.setScaleY(0.8f);
                        page.setAlpha(0.5f);
                    }
                }
            });

            // Lắng nghe sự kiện chạm
            viewPager.setOnTouchListener((v, event) -> {
                boolean scaleHandled = scaleGestureDetector.onTouchEvent(event);
                boolean gestureHandled = gestureDetector.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick(); // Gọi performClick khi click được phát hiện
                }
                return scaleHandled || gestureHandled; // Trả về true nếu đã xử lý
            });

            // Đăng ký listener khi thay đổi trang, reset tất cả ảnh về kích thước mặc định
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int newPosition) {
                    super.onPageSelected(newPosition);
                    resetAllZooms(); // Reset tất cả các ảnh về kích thước mặc định
                }
            });
        }
    }

    // Hàm khôi phục lại tỉ lệ thu phóng cho tất cả ảnh
    private void resetAllZooms() {
        for (int i = 0; i < scaleFactors.length; i++) {
            scaleFactors[i] = 1.0f; // Đặt tỷ lệ về 1 cho tất cả ảnh
            View imageView = viewPager.findViewWithTag("image" + i);
            if (imageView instanceof PhotoView) {
                ((PhotoView) imageView).setScale(1.0f, true); // Đặt tỉ lệ về bình thường cho PhotoView
            }
        }
    }
}