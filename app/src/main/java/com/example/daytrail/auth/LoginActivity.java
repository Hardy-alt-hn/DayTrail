package com.example.daytrail.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.daytrail.R;
import com.example.daytrail.MainActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initViews();
        setupListeners();
    }
    
    private void initViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
    }
    
    private void setupListeners() {
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 简单的登录验证（实际项目中应该连接后端 API）
            AuthManager.getInstance(getApplication()).login(username, password);
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            
            // 跳转到主页
            navigateToMain();
        });
    }
    
    private void navigateToMain() {
        android.content.Intent intent = new android.content.Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
