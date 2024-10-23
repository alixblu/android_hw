package com.example.tuan6;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InputPoint extends AppCompatActivity {

    private Button saveInput;
    private Button saveNextInput;
    private EditText phonenumber;
    private EditText cur_point;
    private EditText new_point;
    private EditText note;
    private DataBase db;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge for immersive UI
        setContentView(R.layout.activity_input_point);

        // Initialize views by finding them in the layout
        saveInput = findViewById(R.id.btnSave);
        saveNextInput = findViewById(R.id.btnSaveNext);
        phonenumber = findViewById(R.id.CustomerPhone); // Correct ID
        cur_point = findViewById(R.id.CurrentPoint);
        new_point = findViewById(R.id.NewPoint);
        note = findViewById(R.id.Note);

        db = new DataBase(InputPoint.this); // Khởi tạo database

        phonenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = s.toString();
                if (phone.length() == 10) { // Kiểm tra nếu số điện thoại đủ 10 ký tự
                    fillCurrentPointsAndNotes(phone);
                }else{
                    cur_point.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        // Set onClickListeners for buttons
        saveInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the phone number to the Check() method
                Check(phonenumber.getText().toString(), new_point.getText().toString());

                // Convert point values
                try {
                    int curPoint = Integer.parseInt(cur_point.getText().toString());
                    int newPoint = Integer.parseInt(new_point.getText().toString());
                    String point_save = String.valueOf(curPoint + newPoint);
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    String phone = phonenumber.getText().toString();
                    if (db.isPhoneNumberExists(phone)) {
                        // Số điện thoại đã tồn tại, có thể thực hiện cập nhật
                        Points updatedPoint = new Points(phone, point_save, note.getText().toString(), date);
                        db.updatePoint(updatedPoint);
                        Toast.makeText(InputPoint.this, "Cập nhật thành công", Toast.LENGTH_LONG).show();
                        phonenumber.setText("");
                        cur_point.setText("");
                        new_point.setText("");
                        note.setText("");
                    } else {
                        // Số điện thoại không tồn tại, có thể thực hiện thêm mới
                        Points addPoint = new Points(phone, point_save, note.getText().toString(), date);
                        db.addPoint(addPoint);
                        Toast.makeText(InputPoint.this, "Thêm thành công", Toast.LENGTH_LONG).show();
                        phonenumber.setText("");
                        cur_point.setText("");
                        new_point.setText("");
                        note.setText("");
                    }

                    showDataFromDb();

                } catch (NumberFormatException e) {
                    showToastAtTop("Vui lòng nhập điểm hợp lệ.");
                }
            }
        });

        saveNextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle save and next button click
                Toast.makeText(InputPoint.this, "Save and Next clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Check(String phone, String new_point) {
        if (phone.length() != 10) {
            showToastAtTop("Số điện thoại phải đủ 10 số");
            return;
        }

        if (new_point.isEmpty()) {
            showToastAtTop("Hãy nhập số điểm cần thêm");
            return;
        }
    }

    private void showToastAtTop(String message) {
        Toast toast = Toast.makeText(InputPoint.this, message, Toast.LENGTH_LONG);
        toast.setGravity(android.view.Gravity.TOP | android.view.Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    private void addInput(Points p) {
        db.addPoint(p);
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
