package com.yolov3android;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Arrays;
import java.util.List;

public class TestOpenHelper extends SQLiteOpenHelper {



    List<String> cocoNames = Arrays.asList("jagaimo","beniharuka","shadowqueen","satoimo","ninjin","daikon","koushindaikon","mixcarrot","tamanegi","mushroom","fruitkabu","stickbroccoli","negi","haninniku","serori","hourensou","dill","wasabina","nabana","babyleaf","radish","pakuchi","akakarashina","leaflettuce_red","leaflettuce_green","komatsuma","bekana","shungiku","saladmizuna","akasaladhourensou","lemon","rukkora","oaklettuce_red","oaklettuce_green","ringo","ourin","kikuimo","saladset","gorugo","shitakke","kokabu","hakusai","ninniku","akadaikon","renkon");


    // データーベースのバージョン
    private static final int DATABASE_VERSION = 3;

    // データーベース情報を変数に格納
    private static final String DATABASE_NAME = "TestDB.db";
    private static final String TABLE_NAME = "testdb";
    private static final String _ID = "_id";
    private static final String COLUMN_NAME_NAME = "name";
    private static final String COLUMN_NAME_QUANTITY = "quantity";
    private static final String COLUMM_NAME_ALARM = "alarm";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_NAME + " TEXT," +
                    COLUMN_NAME_QUANTITY + " INTEGER," +
                    COLUMM_NAME_ALARM + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    TestOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                SQL_CREATE_ENTRIES
        );

        for(int i=0;i<cocoNames.size();i++) {
            String vag_name = cocoNames.get(i);
            saveData(db, vag_name, 1,1);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(
                SQL_DELETE_ENTRIES
        );
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void saveData(SQLiteDatabase db, String name, int quantity,int alarm){
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("alarm",alarm);

        db.insert("testdb", null, values);
    }
}

