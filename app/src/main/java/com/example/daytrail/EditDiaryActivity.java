package com.example.daytrail;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.daytrail.data.Diary;
import com.example.daytrail.data.Weather;
import com.example.daytrail.viewmodel.DiaryViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditDiaryActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText contentEditText;
    private RadioGroup weatherRadioGroup;
    private TextView dateTextView;
    private Button saveButton;

    private ImageButton backButton;

    private DiaryViewModel viewModel;
    private Diary currentDiary;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);

        initViews();
        setupViewModel();
        setupListeners();

        long diaryId = getIntent().getLongExtra("DIARY_ID", -1);
        if (diaryId != -1) {
            isEditMode = true;
            loadDiary(diaryId);
        } else {
            setCurrentDate();
            titleEditText.postDelayed(() -> showKeyboard(titleEditText), 300);
        }
    }

    private void initViews() {
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        weatherRadioGroup = findViewById(R.id.weatherRadioGroup);
        dateTextView = findViewById(R.id.dateTextView);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);

        titleEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showKeyboard(titleEditText);
            }
        });

        contentEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showKeyboard(contentEditText);
            }
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DiaryViewModel.class);
    }

    private void setupListeners() {

        saveButton.setOnClickListener(v -> saveDiary());
        backButton.setOnClickListener(v -> finish());

    }

    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日", Locale.getDefault());
        dateTextView.setText("日期：" + sdf.format(new Date()));
    }

    private void loadDiary(long id) {
        viewModel.getDiaryById(id).observe(this, diary -> {
            if (diary != null) {
                currentDiary = diary;
                titleEditText.setText(diary.getTitle());
                contentEditText.setText(diary.getContent());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日", Locale.getDefault());
                dateTextView.setText("日期：" + sdf.format(new Date(diary.getDate())));

                Weather weather = diary.getWeather();
                if (weather == Weather.SUNNY) {
                    weatherRadioGroup.check(R.id.sunnyRadio);
                } else if (weather == Weather.CLOUDY) {
                    weatherRadioGroup.check(R.id.cloudyRadio);
                } else if (weather == Weather.RAINY) {
                    weatherRadioGroup.check(R.id.rainyRadio);
                } else if (weather == Weather.SNOWY) {
                    weatherRadioGroup.check(R.id.snowyRadio);
                }
            }

            titleEditText.postDelayed(() -> showKeyboard(titleEditText), 300);
        });
    }

    private void saveDiary() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

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

        int selectedWeatherId = weatherRadioGroup.getCheckedRadioButtonId();
        Weather weather = Weather.SUNNY;
        if (selectedWeatherId == R.id.cloudyRadio) {
            weather = Weather.CLOUDY;
        } else if (selectedWeatherId == R.id.rainyRadio) {
            weather = Weather.RAINY;
        } else if (selectedWeatherId == R.id.snowyRadio) {
            weather = Weather.SNOWY;
        }

        if (isEditMode && currentDiary != null) {
            currentDiary.setTitle(title);
            currentDiary.setContent(content);
            currentDiary.setWeather(weather);
            viewModel.update(currentDiary);
            Toast.makeText(this, "日记已更新", Toast.LENGTH_SHORT).show();
        } else {
            Diary diary = new Diary(title, content, weather);
            viewModel.insert(diary);
            Toast.makeText(this, "日记已保存", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void showKeyboard(EditText editText) {
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
