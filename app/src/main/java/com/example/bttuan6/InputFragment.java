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
    private Date createDate;
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

        db = new DataBase(getActivity()); // Correct context initialization

        phonenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = s.toString();
                if (phone.length() == 10) {
                    fillCurrentPointsAndNotes(phone);
                } else {
                    cur_point.setText("0");
                    note.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set onClickListeners for buttons
        saveInput.setOnClickListener(v -> {
            String phone = phonenumber.getText().toString();
            String newPoint = new_point.getText().toString();

            // Kiểm tra đầu vào
            if (!Check(phone, newPoint)) {  // Bây giờ Check trả về boolean
                return; // Dừng lại nếu kiểm tra không thành công
            }

            try {
                int curPoint = Integer.parseInt(cur_point.getText().toString());
                int newPointValue = Integer.parseInt(newPoint); // Chuyển đổi điểm mới

                String point_save = String.valueOf(curPoint + newPointValue);
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                if (db.isPhoneNumberExists(phone)) {
                    // Cập nhật bản ghi
                    Points updatedPoint = new Points(phone, point_save, note.getText().toString(), date, "");
                    db.updatePoint(updatedPoint);
                    showToast("Cập nhật thành công");
                } else {
                    // Thêm bản ghi mới
                    Points addPoint = new Points(phone, point_save, note.getText().toString(), date, date);
                    db.addPoint(addPoint);
                    showToast("Thêm thành công");
                }

                clearInputs();
                showDataFromDb();
            } catch (NumberFormatException e) {
                showToastAtTop("Vui lòng nhập điểm hợp lệ.");
            }
        });




        saveNextInput.setOnClickListener(v -> {
            // First, perform the same save operation as in saveInput
            Check(phonenumber.getText().toString(), new_point.getText().toString());
            try {
                int curPoint = Integer.parseInt(cur_point.getText().toString());
                int newPoint = Integer.parseInt(new_point.getText().toString());
                String point_save = String.valueOf(curPoint + newPoint);
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                String phone = phonenumber.getText().toString();
                if (db.isPhoneNumberExists(phone)) {
                    // Update existing record
                    Points updatedPoint = new Points(phone, point_save, note.getText().toString(), date, "");
                    db.updatePoint(updatedPoint);
                    showToast("Cập nhật thành công");
                } else {
                    // Add new record
                    Points addPoint = new Points(phone, point_save, note.getText().toString(), date, date);
                    db.addPoint(addPoint);
                    showToast("Thêm thành công");
                }
                clearInputs();
                showDataFromDb();

                // Now switch to the UseFragment after saving
                if (getActivity() != null) {
                    Fragment useFragment = new UseFragment(); // Replace with your actual fragment
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, useFragment) // Ensure this ID matches your main layout
                            .addToBackStack(null) // Optional: to allow going back
                            .commit();
                }

            } catch (NumberFormatException e) {

            }
        });

        return view; // Return the inflated view
    }

    private boolean Check(String phone, String new_point) {
        if (phone.length() != 10) {
            showToastAtTop("Số điện thoại phải đủ 10 số");
            return false; // Trả về false nếu kiểm tra không thành công
        }
        if (new_point.isEmpty()) {
            showToastAtTop("Hãy nhập số điểm cần thêm");
            return false; // Trả về false nếu kiểm tra không thành công
        }
        return true; // Trả về true nếu tất cả kiểm tra thành công
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
        cur_point.setText("");
        new_point.setText("");
        note.setText("");
    }

    private void showDataFromDb() {
        Cursor cursor = db.getAllPoints();
        if (cursor.moveToFirst()) {
            do {
                String sdt = cursor.getString(cursor.getColumnIndexOrThrow("sdt"));
                String point = cursor.getString(cursor.getColumnIndexOrThrow("point"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                String cur_date = cursor.getString(cursor.getColumnIndexOrThrow("cur_date"));
                // Log the data
                Log.d("DB_DATA", "Phone: " + sdt + ", Points: " + point + ", Note: " + note + ", Date: " + cur_date);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void fillCurrentPointsAndNotes(String phone) {
        Points point = db.getPointByPhoneNumber(phone);
        if (point != null) {
            cur_point.setText(point.getPoint());
            note.setText(point.getNote());
        } else {
            note.setText("");
            cur_point.setText("0");
        }
    }
}
