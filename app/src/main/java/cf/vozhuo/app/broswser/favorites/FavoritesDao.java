package cf.vozhuo.app.broswser.favorites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class FavoritesDao {
    private static final String TABLE = "favorites";
    public int DB_VERSION = 1;
    private FavoritesSQLiteOpenHelper openHelper;

    public FavoritesDao(Context context) {
        openHelper = new FavoritesSQLiteOpenHelper(context);
    }
    //增
    public void insert(String _id, String title, String url, String time) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("_id",_id);
        values.put("title",title);
        values.put("url",url);
        values.put("time",time);
        db.insert(TABLE, null, values);
    }
    //删
    public void delete(String url) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete(TABLE, "url = ?", new String[]{url});
        Log.e(TAG, "delete: SUCCESS");
    }
    //改
    public void update(String _id, String title, String url) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title",title);
        values.put("url",url);
        db.update(TABLE, values, "_id = ?", new String[]{_id});
    }
    //查询所有
    public List<FavoritesEntity> queryAllFavorites() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] columns = {"_id","title","url","time"};
        Cursor cursor = db.query(TABLE, columns, null, null, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            List<FavoritesEntity> listFavorites = new ArrayList<>();
            while(cursor.moveToNext()) {
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                listFavorites.add(new FavoritesEntity(_id,title,url,time));
            }
            cursor.close();
            return listFavorites;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public boolean queryURL(String url) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] column = {"url"};
        String selection = "url = ?";
        String[] selectionArgs = {url};
        Cursor cursor = db.query(TABLE, column, selection, selectionArgs, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            return false;
        }
    }
}
