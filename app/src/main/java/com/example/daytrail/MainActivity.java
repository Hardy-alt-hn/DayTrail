package com.example.daytrail;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.daytrail.adapter.CategoryAdapter;
import com.example.daytrail.data.Diary;
import com.example.daytrail.data.Category;
import com.example.daytrail.viewmodel.DiaryViewModel;

public class MainActivity extends AppCompatActivity implements DiaryAdapter.OnDiaryClickListener {

    private RecyclerView diaryRecyclerView;
    private EditText searchEditText;
    private ImageButton addButton;
    private TextView emptyText;
    private LinearLayout categoryContainer;

    private DiaryViewModel viewModel;
    private DiaryAdapter adapter;
    private CategoryAdapter categoryAdapter;

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
        setupViewModel();
        setupRecyclerView();
        setupCategoryRecyclerView();
        setupListeners();
    }

    private void initViews() {
        diaryRecyclerView = findViewById(R.id.diaryRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        addButton = findViewById(R.id.addButton);
        emptyText = findViewById(R.id.emptyText);
        categoryContainer = findViewById(R.id.categoryContainer);
    }

    private void setupRecyclerView() {
        adapter = new DiaryAdapter();
        adapter.setOnDiaryClickListener(this);
        diaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        diaryRecyclerView.setAdapter(adapter);
    }
    
    private void setupCategoryRecyclerView() {
        categoryAdapter = new CategoryAdapter(this, viewModel.getSelectedCategory());
        categoryAdapter.setOnCategoryClickListener(category -> {
            if (category != null) {
                viewModel.setSelectedCategory(category);
                // 按分类过滤
                viewModel.filterByCategory(category.getId());
            } else {
                showAddCategoryDialog();
            }
        });
        
        categoryAdapter.setOnCategoryLongClickListener(category -> {
            showDeleteCategoryDialog(category);
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        viewModel.getSearchedDiaries().observe(this, diaries -> {
            adapter.submitList(diaries);
            if (diaries.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
                diaryRecyclerView.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                diaryRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        
        viewModel.getAllCategories().observe(this, categories -> {
            categoryAdapter.updateCategories(categories);
        });
    }

    private void setupListeners() {
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditDiaryActivity.class);
            startActivity(intent);
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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
    
    private void showAddCategoryDialog() {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("请输入分类名称");
        input.setMaxLines(1);
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("添加新分类")
                .setMessage("请输入新分类的名称")
                .setView(input)
                .setPositiveButton("添加", (dialog, which) -> {
                    String categoryName = input.getText().toString().trim();
                    if (!categoryName.isEmpty()) {
                        viewModel.addCategory(categoryName);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    public void showDeleteCategoryDialog(com.example.daytrail.data.Category category) {
        if ("Uncategorized".equals(category.getName())) {
            // 不能删除默认分类
            android.widget.Toast.makeText(this, "Cannot delete default category", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete category \"" + category.getName() + "\"? Diaries in this category will be moved to Uncategorized.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteCategory(category);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
