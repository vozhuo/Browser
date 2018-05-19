package tk.vozhuo.browser.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "data.db";
    public static final String TABLE_FAV = "favorites";
    public static final String TABLE_HIS = "histories";
    public static final String TABLE_QA = "quickAccess";
    public SQLiteHelper(Context context) {
        super(context, DB_NAME,null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String fav = "CREATE TABLE " + TABLE_FAV + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, url VARCHAR UNIQUE, title VARCHAR, time VARCHAR, favicon BLOB)";
        String his = "CREATE TABLE " + TABLE_HIS + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, url VARCHAR UNIQUE, title VARCHAR, time VARCHAR, favicon BLOB)";
        String qa = "CREATE TABLE " + TABLE_QA + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, url VARCHAR UNIQUE, title VARCHAR, time VARCHAR, favicon BLOB)";
        db.execSQL(fav);
        db.execSQL(his);
        db.execSQL(qa);
        Log.e(TAG, "CREATE DATABASE");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QA);
        onCreate(db);
    }
}
