package com.example.bttuan6;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InputFragment extends Fragment {
    private Button saveInput;
    private Button saveNextInput;
    private EditText phonenumber;
    private EditText cur_point;
    private EditText new_point;
    private EditText note;
    private DataBase db;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_input, container, false);

        // Initialize views
        saveInput = view.findViewById(R.id.btnSave);
        saveNextInput = view.findViewById(R.id.btnSaveNext);
        phonenumber = view.findViewById(R.id.CustomerPhone);
        cur_point = view.findViewById(R.id.CurrentPoint);
        new_point = view.findViewById(R.id.NewPoint);
        note = view.findViewById(R.id.Note);

        db = new DataBase(getActivity()); // Initialize database connection

        // Add TextWatcher for phone number field
        phonenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10) {
                    fillCurrentPointsAndNotes(s.toString());
                } else {
                    cur_point.setText("0");
                    note.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set onClickListener for "Save" button
        saveInput.setOnClickListener(v -> handleSave(false));

        // Set onClickListener for "Save and Next" button
        saveNextInput.setOnClickListener(v -> handleSave(true));

        return view;
    }

    /**
     * Handle the save logic for both "Save" and "Save and Next" buttons.
     *
     * @param isSaveAndNext if true, switch to UseFragment after saving.
     */
    private void handleSave(boolean isSaveAndNext) {
        String phone = phonenumber.getText().toString();
        String newPoint = new_point.getText().toString();

        if (!validateInput(phone, newPoint)) return;

        try {
            int curPoint = Integer.parseInt(cur_point.getText().toString());
            int newPointValue = Integer.parseInt(newPoint);
            String pointSave = String.valueOf(curPoint + newPointValue);
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            if (db.isPhoneNumberExists(phone)) {
                // Update existing record
                Points updatedPoint = new Points(phone, pointSave, note.getText().toString(), date, "");
                db.updatePoint(updatedPoint);
                showToast("Cập nhật thành công");
            } else {
                // Add new record
                Points newRecord = new Points(phone, pointSave, note.getText().toString(), date, date);
                db.addPoint(newRecord);
                showToast("Thêm thành công");
            }

            clearInputs();

            if (isSaveAndNext && getActivity() != null) {
                Fragment useFragment = new UseFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, useFragment)
                        .addToBackStack(null) // Enable going back
                        .commit();
            }
        } catch (NumberFormatException e) {
            showToastAtTop("Vui lòng nhập điểm hợp lệ.");
        }
    }

    /**
     * Validate input fields.
     *
     * @param phone    Phone number input
     * @param newPoint New point input
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInput(String phone, String newPoint) {
        if (phone.length() != 10) {
            showToastAtTop("Số điện thoại phải đủ 10 số");
            return false;
        }
        if (newPoint.isEmpty()) {
            showToastAtTop("Hãy nhập số điểm cần thêm");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    private void showToastAtTop(String message) {
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        toast.setGravity(android.view.Gravity.TOP | android.view.Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    private void clearInputs() {
        phonenumber.setText("");
        cur_point.setText("0");
        new_point.setText("");
        note.setText("");
    }

    private void fillCurrentPointsAndNotes(String phone) {
        Points point = db.getPointByPhoneNumber(phone);
        if (point != null) {
            cur_point.setText(point.getPoint());
            note.setText(point.getNote());
        } else {
            cur_point.setText("0");
            note.setText("");
        }
    }
}
