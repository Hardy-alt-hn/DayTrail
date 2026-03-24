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
    private final MutableLiveData<List<Diary>> allDiaries = new MutableLiveData<>();

    public DiaryDao(Context context) {
        dbHelper = new DiaryDbHelper(context);
        executorService = Executors.newFixedThreadPool(4);
    }

    public void insert(Diary diary) {
        executorService.execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DiaryDbHelper.COLUMN_TITLE, diary.getTitle());
            values.put(DiaryDbHelper.COLUMN_CONTENT, diary.getContent());
            values.put(DiaryDbHelper.COLUMN_DATE, diary.getDate());
            values.put(DiaryDbHelper.COLUMN_WEATHER, diary.getWeather());

            db.insert(DiaryDbHelper.TABLE_DIARIES, null, values);

            loadAllDiaries();
        });
    }

    public void update(Diary diary) {
        executorService.execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DiaryDbHelper.COLUMN_TITLE, diary.getTitle());
            values.put(DiaryDbHelper.COLUMN_CONTENT, diary.getContent());
            values.put(DiaryDbHelper.COLUMN_DATE, diary.getDate());
            values.put(DiaryDbHelper.COLUMN_WEATHER, diary.getWeather());

            db.update(DiaryDbHelper.TABLE_DIARIES, values,
                    DiaryDbHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(diary.getId())});

            loadAllDiaries();
        });
    }

    public void delete(Diary diary) {
        executorService.execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete(DiaryDbHelper.TABLE_DIARIES,
                    DiaryDbHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(diary.getId())});

            loadAllDiaries();
        });
    }

    public LiveData<List<Diary>> getAllDiaries() {
        loadAllDiaries();
        return allDiaries;
    }

    public LiveData<List<Diary>> searchDiaries(String query) {
        executorService.execute(() -> {
            List<Diary> diaries = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.query(
                    DiaryDbHelper.TABLE_DIARIES,
                    null,
                    DiaryDbHelper.COLUMN_TITLE + " LIKE ?",
                    new String[]{"%" + query + "%"},
                    null,
                    null,
                    DiaryDbHelper.COLUMN_DATE + " DESC"
            );

            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_TITLE));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_CONTENT));
                    long date = cursor.getLong(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_DATE));
                    String weather = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_WEATHER));

                    diaries.add(new Diary(id, title, content, date, weather));
                } while (cursor.moveToNext());
            }

            cursor.close();
            allDiaries.postValue(diaries);
        });

        return allDiaries;
    }

    public Diary getDiaryById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Diary diary = null;

        Cursor cursor = db.query(
                DiaryDbHelper.TABLE_DIARIES,
                null,
                DiaryDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_CONTENT));
            long date = cursor.getLong(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_DATE));
            String weather = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_WEATHER));

            diary = new Diary(id, title, content, date, weather);
        }

        cursor.close();

        return diary;
    }

    private void loadAllDiaries() {
        executorService.execute(() -> {
            List<Diary> diaries = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.query(
                    DiaryDbHelper.TABLE_DIARIES,
                    null,
                    null,
                    null,
                    null,
                    null,
                    DiaryDbHelper.COLUMN_DATE + " DESC"
            );

            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_TITLE));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_CONTENT));
                    long date = cursor.getLong(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_DATE));
                    String weather = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.COLUMN_WEATHER));

                    diaries.add(new Diary(id, title, content, date, weather));
                } while (cursor.moveToNext());
            }

            cursor.close();

            Collections.sort(diaries, (d1, d2) -> Long.compare(d2.getDate(), d1.getDate()));

            allDiaries.postValue(diaries);
        });
    }
}