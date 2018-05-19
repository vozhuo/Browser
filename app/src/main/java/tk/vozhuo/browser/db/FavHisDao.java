package tk.vozhuo.browser.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import tk.vozhuo.browser.entity.FavHisEntity;
import tk.vozhuo.browser.db.SQLiteHelper;

import static android.content.ContentValues.TAG;

public class FavHisDao {
    private SQLiteHelper openHelper;
    private String TABLE;
    public FavHisDao(Context context, String table) {
        TABLE = table;
        openHelper = new SQLiteHelper(context);
    }
    //增
    public void insert(String _id, String title, String url, String time, byte[] favicon) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.e(TAG, "insert: " + title + " " + url);
        values.put("_id",_id);
        values.put("title",title);
        values.put("url",url);
        values.put("time",time);
        values.put("favicon", favicon);
        db.insert(TABLE, null, values);
    }
    //删
    public void delete(String url) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete(TABLE, "url = ?", new String[]{url});
    }
    public void deleteAll() {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        String sql = "DELETE FROM " + TABLE;
        db.execSQL(sql);
    }

    //改
    public void update(String title, String url) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title",title);
        values.put("url",url);
        db.update(TABLE, values, "url = ?", new String[]{url});
    }
    public void updateTime(String url, String time) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("time",time);
        db.update(TABLE, values, "url = ?", new String[]{url});
    }
    //查询所有
    public List<FavHisEntity> queryAll() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] columns = {"_id","title","url","time", "favicon"};
        Cursor cursor = db.query(TABLE, columns, null, null, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            List<FavHisEntity> list = new ArrayList<>();
            while(cursor.moveToNext()) {
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                byte[] favicon = cursor.getBlob(cursor.getColumnIndex("favicon"));
                list.add(new FavHisEntity(_id,title,url,time, favicon));
            }
            cursor.close();
            return list;
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
