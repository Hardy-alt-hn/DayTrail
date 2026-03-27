package com.example.daytrail.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.daytrail.MainActivity;
import com.example.daytrail.R;
import com.example.daytrail.data.DiaryDbHelper;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerText;
    private ProgressBar progressBar;

    private DiaryDbHelper dbHelper;
    private AuthManager authManager;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (isAlreadyLoggedIn()) {
            navigateToMain();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);
        progressBar = findViewById(R.id.progressBar);

        dbHelper = new DiaryDbHelper(this);
        authManager = AuthManager.getInstance(getApplication());
        mainHandler = new Handler(Looper.getMainLooper());
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> performLogin());

        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean isAlreadyLoggedIn() {
        authManager = AuthManager.getInstance(getApplication());
        return authManager.isLoggedIn();
    }

    private void performLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty()) {
            usernameEditText.setError("用户名不能为空");
            usernameEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("密码不能为空");
            passwordEditText.requestFocus();
            return;
        }

        showLoading(true);

        new Thread(() -> {
            boolean loginSuccess = checkCredentials(username, password);

            mainHandler.post(() -> {
                showLoading(false);
                if (loginSuccess) {
                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else {
                    Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private boolean checkCredentials(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("daytrail_users", MODE_PRIVATE);
        String storedPassword = sharedPreferences.getString(username, null);

        if (storedPassword != null && storedPassword.equals(password)) {
            long userId = sharedPreferences.getLong(username + "_id", -1);
            authManager.login(userId, username);
            return true;
        }
        return false;
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            registerText.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
            registerText.setEnabled(true);
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
