package com.example.bttuan6;

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

        // Load InputFragment by default
        loadFragment(new InputFragment());

        // Navigate to UseFragment when Use button is clicked
        buttonUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new UseFragment());
            }
        });

        // Navigate to InputFragment when Input button is clicked
        buttonInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new InputFragment());
            }
        });

        // Navigate to ListFragment when List button is clicked
        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ListFragment());
            }
        });
    }

    // Method to load Fragment
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null); // Add to back stack for user to navigate back
        transaction.commit();
    }
}
