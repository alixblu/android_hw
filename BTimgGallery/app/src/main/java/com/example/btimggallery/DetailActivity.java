package com.example.btimggallery;

import android.os.Bundle;

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
        imagePaths = getIntent().getStringArrayListExtra("imagePaths");
        position = getIntent().getIntExtra("position", 0);

        adapter = new DetailAdapter(this, imagePaths);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }
}
