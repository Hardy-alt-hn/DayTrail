package com.example.daytrail.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class DiaryRepository {
    private final DiaryDao diaryDao;
    private final LiveData<List<Diary>> userDiaries;

    public DiaryRepository(Application application) {
        diaryDao = new DiaryDao(application);
        userDiaries = diaryDao.getUserDiaries();
    }

    public void setUserId(long userId) {
        diaryDao.setUserId(userId);
    }

    public LiveData<List<Diary>> getUserDiaries() {
        return userDiaries;
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

    public LiveData<Diary> getDiaryById(long id) {
        return diaryDao.getDiaryById(id);
    }

    public void refreshUserDiaries() {
        diaryDao.loadUserDiaries();
    }
}
