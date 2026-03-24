package com.example.daytrail.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class DiaryRepository {
    private final DiaryDao diaryDao;
    private final LiveData<List<Diary>> allDiaries;

    public DiaryRepository(Application application) {
        diaryDao = new DiaryDao(application);
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
}
