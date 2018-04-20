package cf.vozhuo.app.broswser.favorites;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class FavoritesSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "favorites.db";
    public static final String TABLE_NAME = "favorites";
    private Context mContext;
    public FavoritesSQLiteOpenHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
//        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, url VARCHAR UNIQUE, title VARCHAR, time VARCHAR)";
        db.execSQL(sql);
        Log.e(TAG, "onCreate: SUCCESS");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
