package com.example.khachhangthanthiet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonInput = findViewById(R.id.buttonInput);
        Button buttonUse = findViewById(R.id.buttonUse);
        Button buttonList = findViewById(R.id.buttonList);

        // Điều hướng tới Fragment Use khi nhấn nút Use
        buttonUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new UseFragment());
            }
        });

        // Điều hướng tới Fragment Input (trống)
        buttonInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new InputFragment());
            }
        });

        // Điều hướng tới Fragment List (trống)
        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ListFragment());
            }
        });
    }

    // Hàm load Fragment
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null); // Thêm vào backstack để người dùng có thể quay lại
        transaction.commit();
    }
}
