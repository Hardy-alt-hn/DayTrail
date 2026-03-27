package com.example.daytrail.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class CategoryDao {
    private final DiaryDbHelper dbHelper;
    private final Context context;

    public CategoryDao(Context context) {
        this.dbHelper = new DiaryDbHelper(context);
        this.context = context;
    }
    
    public Context getContext() {
        return context;
    }

    public void insert(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DiaryDbHelper.CATEGORY_COLUMN_NAME, category.getName());
        db.insert(DiaryDbHelper.TABLE_CATEGORIES, null, values);
    }

    public LiveData<List<com.example.daytrail.data.Category>> getAllCategories() {
        MutableLiveData<List<com.example.daytrail.data.Category>> allCategories = new MutableLiveData<>();
        List<com.example.daytrail.data.Category> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DiaryDbHelper.TABLE_CATEGORIES,
                null,
                null,
                null,
                null,
                null,
                DiaryDbHelper.CATEGORY_COLUMN_NAME + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DiaryDbHelper.CATEGORY_COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.CATEGORY_COLUMN_NAME));
                categories.add(new com.example.daytrail.data.Category(id, name));
            } while (cursor.moveToNext());
        }

        cursor.close();
        allCategories.postValue(categories);
        return allCategories;
    }

    public Category getCategoryById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Category category = null;

        Cursor cursor = db.query(
                DiaryDbHelper.TABLE_CATEGORIES,
                null,
                DiaryDbHelper.CATEGORY_COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.CATEGORY_COLUMN_NAME));
            category = new Category(id, name);
        }

        cursor.close();
        return category;
    }

    public Category getUncategorizedCategory() {
        return getCategoryByName("Uncategorized");
    }

    public Category getCategoryByName(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Category category = null;

        Cursor cursor = db.query(
                DiaryDbHelper.TABLE_CATEGORIES,
                null,
                DiaryDbHelper.CATEGORY_COLUMN_NAME + " = ?",
                new String[]{name},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DiaryDbHelper.CATEGORY_COLUMN_ID));
            String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DiaryDbHelper.CATEGORY_COLUMN_NAME));
            category = new Category(id, categoryName);
        }

        cursor.close();
        return category;
    }

    public void delete(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 先将该分类下的日记改为"Uncategorized"
        Category uncategorized = getUncategorizedCategory();
        if (uncategorized != null) {
            ContentValues values = new ContentValues();
            values.put(DiaryDbHelper.COLUMN_CATEGORY_ID, uncategorized.getId());
            db.update(DiaryDbHelper.TABLE_DIARIES, values,
                    DiaryDbHelper.COLUMN_CATEGORY_ID + " = ?",
                    new String[]{String.valueOf(category.getId())});
        }
        
        // 然后删除分类
        db.delete(DiaryDbHelper.TABLE_CATEGORIES,
                DiaryDbHelper.CATEGORY_COLUMN_ID + " = ?",
                new String[]{String.valueOf(category.getId())});
    }
}
