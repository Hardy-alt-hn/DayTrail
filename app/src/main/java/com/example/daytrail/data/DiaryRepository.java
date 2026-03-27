package com.example.daytrail.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class DiaryRepository {
    private final DiaryDao diaryDao;
    private final CategoryDao categoryDao;
    private final LiveData<List<Diary>> allDiaries;

    public DiaryRepository(Application application) {
        diaryDao = new DiaryDao(application);
        categoryDao = new CategoryDao(application);
        allDiaries = diaryDao.getAllDiaries();
    }

    public LiveData<List<Diary>> getAllDiaries() {
        return allDiaries;
    }

    public LiveData<List<Diary>> searchDiaries(String query) {
        return diaryDao.searchDiaries(query);
    }

    public void insert(Diary diary) {
        diaryDao.insert(diary);
    }

    public void update(Diary diary) {
        diaryDao.update(diary);
    }

    public void delete(Diary diary) {
        diaryDao.delete(diary);
    }

    public Diary getDiaryById(long id) {
        return diaryDao.getDiaryById(id);
    }
    
    public void addCategory(Category category) {
        categoryDao.insert(category);
    }
    
    public void deleteCategory(Category category) {
        categoryDao.delete(category);
    }
    
    public LiveData<List<Diary>> getDiariesByCategory(long categoryId) {
        DiaryDao diaryDao = new DiaryDao(categoryDao.getContext());
        return diaryDao.getDiariesByCategory(categoryId);
    }
}
