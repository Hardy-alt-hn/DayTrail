package com.example.daytrail.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiaryDao {
    private final DiaryDbHelper dbHelper;
    private final ExecutorService executorService;
    private long currentUserId = -1;
    private final MutableLiveData<List<Diary>> userDiaries = new MutableLiveData<>();

    public DiaryDao(Context context) {
        dbHelper = new DiaryDbHelper(context);
        executorService = Executors.newFixedThreadPool(4);
    }

    public void setUserId(long userId) {
        this.currentUserId = userId;
        loadUserDiaries();
    }

    public void insert(Diary diary) {
        if (currentUserId == -1) return;

        executorService.execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DiaryDbHelper.COLUMN_USER_ID, currentUserId);
            values.put(DiaryDbHelper.COLUMN_TITLE, diary.getTitle());
            values.put(DiaryDbHelper.COLUMN_CONTENT, diary.getContent());
            values.put(DiaryDbHelper.COLUMN_DATE, diary.getDate());
            values.put(DiaryDbHelper.COLUMN_WEATHER, diary.getWeather().getValue());

            db.insert(DiaryDbHelper.TABLE_DIARIES, null, values);

            loadUserDiaries();
        });
    }

    public void update(Diary diary) {
        executorService.execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DiaryDbHelper.COLUMN_TITLE, diary.getTitle());
            values.put(DiaryDbHelper.COLUMN_CONTENT, diary.getContent());
            values.put(DiaryDbHelper.COLUMN_DATE, diary.getDate());
            values.put(DiaryDbHelper.COLUMN_WEATHER, diary.getWeather().getValue());

            db.update(DiaryDbHelper.TABLE_DIARIES, values,
                    DiaryDbHelper.COLUMN_ID + " = ? AND " + DiaryDbHelper.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(diary.getId()), String.valueOf(currentUserId)});

            loadUserDiaries();
        });
    }

    public void delete(Diary diary) {
        executorService.execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete(DiaryDbHelper.TABLE_DIARIES,
                    DiaryDbHelper.COLUMN_ID + " = ? AND " + DiaryDbHelper.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(diary.getId()), String.valueOf(currentUserId)});

            loadUserDiaries();
        });
    }

    public LiveData<List<Diary>> getUserDiaries() {
        return userDiaries;
    }

    public LiveData<List<Diary>> searchDiaries(String query) {
        MutableLiveData<List<Diary>> searchResult = new MutableLiveData<>();

        executorService.execute(() -> {
            List<Diary> diaries = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String selection = DiaryDbHelper.COLUMN_USER_ID + " = ? AND (" +
                    DiaryDbHelper.COLUMN_TITLE + " LIKE ? OR " +
                    DiaryDbHelper.COLUMN_CONTENT + " LIKE ?)";

            Cursor cursor = db.query(
                    DiaryDbHelper.TABLE_DIARIES,
                    null,
                    selection,
                    new String[]{String.valueOf(currentUserId), "%" + query + "%", "%" + query + "%"},
                    null,
                    null,
                    DiaryDbHelper.COLUMN_DATE + " DESC"
            );

            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_ID));
                    long userId = cursor.getLong(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_USER_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_TITLE));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_CONTENT));
                    long date = cursor.getLong(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_DATE));
                    String weather = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_WEATHER));

                    diaries.add(new Diary(id, userId, title, content, date, Weather.fromValue(weather)));
                } while (cursor.moveToNext());
            }

            cursor.close();
            searchResult.postValue(diaries);
        });

        return searchResult;
    }

    public LiveData<Diary> getDiaryById(long id) {
        MutableLiveData<Diary> diaryLiveData = new MutableLiveData<>();

        executorService.execute(() -> {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Diary diary = null;

            Cursor cursor = db.query(
                    DiaryDbHelper.TABLE_DIARIES,
                    null,
                    DiaryDbHelper.COLUMN_ID + " = ? AND " + DiaryDbHelper.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(id), String.valueOf(currentUserId)},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                long userId = cursor.getLong(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_USER_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_CONTENT));
                long date = cursor.getLong(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_DATE));
                String weather = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_WEATHER));

                diary = new Diary(id, userId, title, content, date, Weather.fromValue(weather));
            }

            cursor.close();
            diaryLiveData.postValue(diary);
        });

        return diaryLiveData;
    }

    public void loadUserDiaries() {
        if (currentUserId == -1) return;

        executorService.execute(() -> {
            List<Diary> diaries = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.query(
                    DiaryDbHelper.TABLE_DIARIES,
                    null,
                    DiaryDbHelper.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(currentUserId)},
                    null,
                    null,
                    DiaryDbHelper.COLUMN_DATE + " DESC"
            );

            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_ID));
                    long userId = cursor.getLong(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_USER_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_TITLE));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_CONTENT));
                    long date = cursor.getLong(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_DATE));
                    String weather = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_WEATHER));

                    diaries.add(new Diary(id, userId, title, content, date, Weather.fromValue(weather)));
                } while (cursor.moveToNext());
            }

            cursor.close();

            Collections.sort(diaries, (d1, d2) -> Long.compare(d2.getDate(), d1.getDate()));

            userDiaries.postValue(diaries);
        });
    }
}
