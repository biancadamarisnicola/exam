package com.example.nicolab.exam.entity.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.nicolab.exam.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bianca on 19.11.2016.
 */

public class DatabaseSettings extends SQLiteOpenHelper {
    private static final String TAG = DatabaseSettings.class.getSimpleName();

    public static final String TABLE_NAME = "entity";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_V1 = "value1";
    public static final String COLUMN_V2 = "value2";
    public static final String COLUMN_V3 = "value3";
    public static final String COLUMN_V4 = "value4";
    public static final String COLUMN_V5 = "value6";

    private static final String DATABASE_NAME = "caloriescounter.db";
    private static final int DATABASE_VERSION = 2;

    private static final String COLUMN_ID = "id";
    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME + "( " + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_V1 + " text not null, "
            + COLUMN_V2 + " text not null, "
            + COLUMN_V4 + " text not null, "
            + COLUMN_V3 + " text not null);";

    public DatabaseSettings(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "DatabaseSettings");

    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d(TAG, "onCreate Database");
        database.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpdate Database");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void save(Entity e) {
        Log.d(TAG, "saveAliment Database");
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, e.getName());
        cv.put(COLUMN_V1, e.getValue1());
        cv.put(COLUMN_V2, e.getValue2());
        cv.put(COLUMN_V4, e.getValue3());
        cv.put(COLUMN_V3, e.getValue4());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public List<Entity> getAll() {
        Log.d(TAG, "get objects Database");
        List<Entity> aliments = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("select * from " + TABLE_NAME, null, null);
        int size = c.getCount();
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            aliments.add(new Entity(c.getString(0), c.getString(1), c.getString(2), c.getString(3),c.getString(4), c.getString(5)));
            c.moveToNext();
        }
        ;
        return aliments;
    }

    public void deletByName(Entity entity) {
        Log.d(TAG, "deleteAliment Database");
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, entity.getName());
        String whereClause = " name =? ";
        String[] args = new String[1];
        args[0] = entity.getName();
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, whereClause, args);
        db.close();
    }

    public Entity getOneByName(String alimentName) {
        Log.d(TAG, "get aliment with name " + alimentName);
        Cursor c = getReadableDatabase().rawQuery("select * from " + TABLE_NAME +  " WHERE name = ?", new String[] { alimentName });
        c.moveToFirst();
        if (c.moveToFirst()) {
            Log.d(TAG, c.getString(0) + c.getString(1));
            return new Entity(c.getString(0), c.getString(1), c.getString(2), c.getString(3),c.getString(4), c.getString(5));
        } else {
            return null;
        }
    }

    public void deleteAll() {
        getWritableDatabase().execSQL("DELETE FROM " + TABLE_NAME + ";");
    }
}
