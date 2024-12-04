package com.example.bttuan6;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText inputUsername;
    private EditText inputOldPassword;
    private EditText inputNewPassword;
    private EditText inputConfirmPassword;
    private Button btnChangePassword;
    private DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        db = new DataBase(this); // Khởi tạo cơ sở dữ liệu

        inputUsername = findViewById(R.id.inputUsername);
        inputOldPassword = findViewById(R.id.inputOldPassword);
        inputNewPassword = findViewById(R.id.inputNewPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputUsername.getText().toString().trim();
                String oldPassword = inputOldPassword.getText().toString().trim();
                String newPassword = inputNewPassword.getText().toString().trim();
                String confirmPassword = inputConfirmPassword.getText().toString().trim();

                // Kiểm tra username có tồn tại
                if (!db.isAccountExists(username)) {
                    Toast.makeText(ChangePasswordActivity.this, "Username does not exist!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra mật khẩu cũ
                if (!db.validateAccount(username, oldPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "Old password is incorrect!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra mật khẩu mới
                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "New password and confirmation cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isPasswordStrong(newPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "New password must be at least 8 characters long and contain at least one special character!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPassword.equals(oldPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "New password cannot be the same as old password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "New password and confirmation do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cập nhật mật khẩu
                if (db.updateAccount(username, newPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng activity
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Failed to change password. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isPasswordStrong(String password) {
        return password.length() >= 8 && password.matches(".*[!@#$%^&*].*");
    }
}
