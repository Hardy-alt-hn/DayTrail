package com.example.daytrail;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.daytrail.data.Diary;
import com.example.daytrail.data.Category;
import com.example.daytrail.viewmodel.DiaryViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditDiaryActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText contentEditText;
    private EditText weatherEditText;
    private TextView dateTextView;
    private Button saveButton;
    private ImageButton backButton;
    private Spinner categorySpinner;

    private DiaryViewModel viewModel;
    private Diary currentDiary;
    private boolean isEditMode = false;
    private long selectedDate = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置窗口支持正常的输入法
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_edit_diary);

        initViews();
        setupViewModel();
        setupListeners();
        setupCategorySpinner();

        long diaryId = getIntent().getLongExtra("DIARY_ID", -1);
        if (diaryId != -1) {
            isEditMode = true;
            loadDiary(diaryId);
        } else {
            setCurrentDate();
            // 自动聚焦到标题输入框并显示输入法
            titleEditText.postDelayed(() -> {
                titleEditText.requestFocus();
                showKeyboard(titleEditText);
            }, 300);
        }
    }

    private void setupCategorySpinner() {
        viewModel.getAllCategories().observe(this, categories -> {
            ArrayAdapter<com.example.daytrail.data.Category> adapter = new ArrayAdapter<com.example.daytrail.data.Category>(this,
                    android.R.layout.simple_spinner_item, categories) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView view = (TextView) super.getView(position, convertView, parent);
                    com.example.daytrail.data.Category category = getItem(position);
                    if (category != null) {
                        view.setText(category.getName());
                    }
                    return view;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                    com.example.daytrail.data.Category category = getItem(position);
                    if (category != null) {
                        view.setText(category.getName());
                    }
                    return view;
                }
            };
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
            
            // 如果是编辑模式，设置当前分类
            if (isEditMode && currentDiary != null) {
                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).getId() == currentDiary.getCategoryId()) {
                        categorySpinner.setSelection(i);
                        break;
                    }
                }
            } else {
                // 新建日记时默认选择最新分类
                Category latestCategory = viewModel.getLatestCategory();
                if (latestCategory != null) {
                    for (int i = 0; i < categories.size(); i++) {
                        if (categories.get(i).getId() == latestCategory.getId()) {
                            categorySpinner.setSelection(i);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void initViews() {
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        weatherEditText = findViewById(R.id.weatherEditText);
        dateTextView = findViewById(R.id.dateTextView);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
        categorySpinner = findViewById(R.id.categorySpinner);

        dateTextView.setOnClickListener(v -> showDatePicker());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DiaryViewModel.class);
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> saveDiary());
        backButton.setOnClickListener(v -> finish());
    }

    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日", Locale.getDefault());
        selectedDate = System.currentTimeMillis();
        dateTextView.setText("日期：" + sdf.format(new Date(selectedDate)));
    }

    private void loadDiary(long id) {
        currentDiary = viewModel.getDiaryById(id);
        if (currentDiary != null) {
            titleEditText.setText(currentDiary.getTitle());
            contentEditText.setText(currentDiary.getContent());
            weatherEditText.setText(currentDiary.getWeather());

            selectedDate = currentDiary.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日", Locale.getDefault());
            dateTextView.setText("日期：" + sdf.format(new Date(selectedDate)));
        }
    }

    private void saveDiary() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        String weather = weatherEditText.getText().toString().trim();
        
        Category selectedCategory = (Category) categorySpinner.getSelectedItem();
        long categoryId = selectedCategory != null ? selectedCategory.getId() : 1;

        if (title.isEmpty()) {
            titleEditText.setError("标题不能为空");
            titleEditText.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            contentEditText.setError("内容不能为空");
            contentEditText.requestFocus();
            return;
        }

        if (weather.isEmpty()) {
            weatherEditText.setError("天气不能为空");
            weatherEditText.requestFocus();
            return;
        }

        if (isEditMode && currentDiary != null) {
            currentDiary.setTitle(title);
            currentDiary.setContent(content);
            currentDiary.setWeather(weather);
            currentDiary.setDate(selectedDate);
            currentDiary.setCategoryId(categoryId);
            viewModel.update(currentDiary);
            Toast.makeText(this, "日记已更新", Toast.LENGTH_SHORT).show();
        } else {
            Diary diary = new Diary(title, content, weather);
            diary.setDate(selectedDate);
            diary.setCategoryId(categoryId);
            viewModel.insert(diary);
            Toast.makeText(this, "日记已保存", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void showDatePicker() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(new Date(selectedDate));
        
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH);
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                java.util.Calendar newCalendar = java.util.Calendar.getInstance();
                newCalendar.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0);
                newCalendar.set(java.util.Calendar.MILLISECOND, 0);
                selectedDate = newCalendar.getTimeInMillis();
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日", Locale.getDefault());
                dateTextView.setText("日期：" + sdf.format(new Date(selectedDate)));
            },
            year,
            month,
            day
        );
        datePickerDialog.show();
    }
}
