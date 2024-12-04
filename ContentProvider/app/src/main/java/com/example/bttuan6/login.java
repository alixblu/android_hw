package com.example.bttuan6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    private DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo cơ sở dữ liệu
        db = new DataBase(this);

        EditText inputUsername = findViewById(R.id.inputUsername);
        EditText inputPassword = findViewById(R.id.inputPass);
        Button loginButton = findViewById(R.id.button);
        Button changePasswordButton = findViewById(R.id.btnChangePassword);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputUsername.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Kiểm tra nếu username hoặc password bị rỗng
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(login.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra thông tin đăng nhập trong cơ sở dữ liệu
                if (db.validateAccount(username, password)) {
                    // Điều hướng đến MainActivity nếu thông tin đúng
                    Intent intent = new Intent(login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Hiển thị thông báo nếu thông tin sai
                    Toast.makeText(login.this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
//                if (username.equals("admin") && password.equals("admin")) {
//                    // Điều hướng đến MainActivity nếu thông tin đúng
//                    Intent intent = new Intent(login.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    // Hiển thị thông báo nếu thông tin sai
//                    Toast.makeText(login.this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        // Chuyển đến ChangePasswordActivity khi nhấn nút đổi mật khẩu
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}