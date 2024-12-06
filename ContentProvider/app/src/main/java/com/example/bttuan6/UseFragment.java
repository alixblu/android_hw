package com.example.bttuan6;

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

public class UseFragment extends Fragment {
    private Button saveInputUse;
    private Button saveNextInputUse;
    private EditText phonenumber;
    private EditText cur_point;
    private EditText use_point;
    private EditText note;
    private DataBase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_use, container, false);

        // Initialize views
        saveInputUse = view.findViewById(R.id.buttonSave);
        saveNextInputUse = view.findViewById(R.id.buttonSaveNext);
        phonenumber = view.findViewById(R.id.editTextPhone);
        cur_point = view.findViewById(R.id.editTextCurrentPoints);
        use_point = view.findViewById(R.id.editTextUsedPoints);
        note = view.findViewById(R.id.editTextNote);
        db = new DataBase(getActivity());

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

        saveInputUse.setOnClickListener(v -> {
            String phone = phonenumber.getText().toString();
            if (!db.isPhoneNumberExists(phone)) {
                showToastAtTop("Số điện thoại không tồn tại. Vui lòng nhập lại.");
//                phonenumber.setText(""); // Clear phone number input
                return;
            }

            Check(phone, use_point.getText().toString());
            try {
                int curPoint = Integer.parseInt(cur_point.getText().toString());
                int usePoint = Integer.parseInt(use_point.getText().toString());
                if (curPoint < usePoint) {
                    showToast("Điểm sử dụng phải nhỏ hơn hoặc bằng điểm hiện tại.");
                    return;
                }
                String point_save = String.valueOf(curPoint - usePoint);
                String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.getDefault()).format(new Date());

                Points updatedPoint = new Points(phone, point_save, note.getText().toString(), date);
                db.updatePoint(updatedPoint);
                showToast("Sử dụng điểm thành công");
                clearInputs();
                showDataFromDb();
            } catch (NumberFormatException e) {
                showToastAtTop("Vui lòng nhập điểm hợp lệ.");
            }
        });

        saveNextInputUse.setOnClickListener(v -> {
            showToast("Save and Next clicked");
        });

        return view;
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
        use_point.setText("");
        note.setText("");
    }

    private void showDataFromDb() {
        Cursor cursor = db.getAllPoints();
        try {
            if (cursor.moveToFirst()) {
                do {
                    String sdt = cursor.getString(cursor.getColumnIndexOrThrow("sdt"));
                    String point = cursor.getString(cursor.getColumnIndexOrThrow("point"));
                    String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                    String cur_date = cursor.getString(cursor.getColumnIndexOrThrow("cur_date"));
                    Log.d("DB_DATA", "Phone: " + sdt + ", Points: " + point + ", Note: " + note + ", Date: " + cur_date);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
    }

    private void fillCurrentPointsAndNotes(String phone) {
        Points point = db.getPointByPhoneNumber(phone);
        if (point != null) {
            cur_point.setText(point.getPoint());
            note.setText(point.getNote());
        } else {
            cur_point.setText("0");
            note.setText("");
            showToastAtTop("Số điện thoại không tồn tại. Vui lòng kiểm tra lại.");
//            phonenumber.setText(""); // Clear input field
        }
    }
}
