package com.example.daytrail;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daytrail.adapter.DiaryAdapter;
import com.example.daytrail.auth.AuthManager;
import com.example.daytrail.auth.LoginActivity;
import com.example.daytrail.data.Diary;
import com.example.daytrail.viewmodel.DiaryViewModel;

public class MainActivity extends AppCompatActivity implements DiaryAdapter.OnDiaryClickListener {

    private RecyclerView diaryRecyclerView;
    private EditText searchEditText;
    private ImageButton addButton;
    private ImageButton logoutButton;
    private TextView emptyText;

    private DiaryViewModel viewModel;
    private DiaryAdapter adapter;
    private AuthManager authManager;
    private boolean isSearchActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isSearchActive) {
            searchEditText.setText("");
            viewModel.refreshData();
        }
    }

    private void initViews() {
        diaryRecyclerView = findViewById(R.id.diaryRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        addButton = findViewById(R.id.addButton);
        logoutButton = findViewById(R.id.logoutButton);
        emptyText = findViewById(R.id.emptyText);

        authManager = AuthManager.getInstance(getApplication());
    }

    private void setupRecyclerView() {
        adapter = new DiaryAdapter();
        adapter.setOnDiaryClickListener(this);
        diaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        diaryRecyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        long userId = authManager.getUserId();
        viewModel.setUserId(userId);

        viewModel.getDisplayedDiaries().observe(this, diaries -> {
            if (diaries != null) {
                adapter.submitList(diaries);
                if (diaries.isEmpty()) {
                    emptyText.setVisibility(View.VISIBLE);
                    diaryRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.GONE);
                    diaryRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupListeners() {
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditDiaryActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> showLogoutDialog());

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                isSearchActive = !query.isEmpty();
                viewModel.setSearchQuery(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    authManager.logout();
                    Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onEdit(Diary diary) {
        Intent intent = new Intent(MainActivity.this, EditDiaryActivity.class);
        intent.putExtra("DIARY_ID", diary.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(Diary diary) {
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除这篇日记吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    viewModel.delete(diary);
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
