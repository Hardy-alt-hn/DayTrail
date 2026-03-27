package com.example.daytrail.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DiaryDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "diary.db";
    private static final int DATABASE_VERSION = 2;
    
    private final Context context;

    public static final String TABLE_DIARIES = "diaries";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_WEATHER = "weather";
    public static final String COLUMN_CATEGORY_ID = "category_id";

    public static final String TABLE_CATEGORIES = "categories";
    public static final String CATEGORY_COLUMN_ID = "id";
    public static final String CATEGORY_COLUMN_NAME = "name";

    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_DIARIES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_CONTENT + " TEXT NOT NULL, " +
                    COLUMN_DATE + " INTEGER NOT NULL, " +
                    COLUMN_WEATHER + " TEXT NOT NULL, " +
                    COLUMN_CATEGORY_ID + " INTEGER DEFAULT 1);";

    private static final String CATEGORY_DATABASE_CREATE =
            "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                    CATEGORY_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CATEGORY_COLUMN_NAME + " TEXT NOT NULL UNIQUE);";

    public DiaryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    
    public Context getContext() {
        return context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        db.execSQL(CATEGORY_DATABASE_CREATE);
        // 插入默认分类"Uncategorized"
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" + CATEGORY_COLUMN_NAME + ") VALUES ('Uncategorized')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            try {
                // 升级到版本 2：添加分类功能
                // 检查列是否已存在
                Cursor cursor = db.rawQuery("PRAGMA table_info(" + TABLE_DIARIES + ")", null);
                boolean categoryColumnExists = false;
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                        if (COLUMN_CATEGORY_ID.equals(columnName)) {
                            categoryColumnExists = true;
                            break;
                        }
                    }
                    cursor.close();
                }
                
                if (!categoryColumnExists) {
                    db.execSQL("ALTER TABLE " + TABLE_DIARIES + " ADD COLUMN " + COLUMN_CATEGORY_ID + " INTEGER DEFAULT 1");
                }
                
                // 检查分类表是否存在
                cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_CATEGORIES + "'", null);
                boolean categoryTableExists = cursor != null && cursor.getCount() > 0;
                if (cursor != null) {
                    cursor.close();
                }
                
                if (!categoryTableExists) {
                    db.execSQL(CATEGORY_DATABASE_CREATE);
                    db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" + CATEGORY_COLUMN_NAME + ") VALUES ('Uncategorized')");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
