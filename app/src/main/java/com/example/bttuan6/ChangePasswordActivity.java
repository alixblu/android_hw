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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        inputUsername = findViewById(R.id.inputUsername);
        inputOldPassword = findViewById(R.id.inputOldPassword);
        inputNewPassword = findViewById(R.id.inputNewPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputUsername.getText().toString();
                String oldPassword = inputOldPassword.getText().toString();
                String newPassword = inputNewPassword.getText().toString();
                String confirmPassword = inputConfirmPassword.getText().toString();

                // Kiểm tra username có tồn tại không
                if (!username.equals("admin")) { // Thay thế bằng kiểm tra username trong cơ sở dữ liệu
                    Toast.makeText(ChangePasswordActivity.this, "Username does not exist!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra mật khẩu cũ
                if (!oldPassword.equals("admin")) { // Thay thế bằng kiểm tra mật khẩu cũ trong cơ sở dữ liệu
                    Toast.makeText(ChangePasswordActivity.this, "Old password is incorrect!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra mật khẩu mới không được trống
                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "New password and confirmation cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra mật khẩu mới không trùng mật khẩu cũ
                if (newPassword.equals(oldPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "New password cannot be the same as old password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra mật khẩu mới và xác nhận mật khẩu phải giống nhau
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "New password and confirmation do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Nếu tất cả điều kiện đều thỏa mãn, thay đổi mật khẩu (lưu vào cơ sở dữ liệu hoặc SharedPreferences)
                Toast.makeText(ChangePasswordActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Đóng activity
            }
        });
    }
}
