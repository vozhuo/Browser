package cf.vozhuo.app.broswser.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cf.vozhuo.app.broswser.favorites.FavHisEntity;
import cf.vozhuo.app.broswser.favorites.SQLiteHelper;

import static android.content.ContentValues.TAG;

public class DownloadDao {
    private SQLiteHelper openHelper;
    private static String TABLE = "download";
    public DownloadDao(Context context) {
        openHelper = new SQLiteHelper(context);
    }
    //增
    public void insert(String _id, String url, String name, String size, String path) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.e(TAG, "insert: " + name + " " + size);
        values.put("_id",_id);
        values.put("url",url);
        values.put("name",name);
        values.put("size",size);
        values.put("path",path);
        db.insert(TABLE, null, values);
    }
    //删
    public void delete(String path) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete(TABLE, "path = ?", new String[]{path});
    }
    public void deleteAll() {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        String sql = "DELETE FROM " + TABLE;
        db.execSQL(sql);
    }

    //改
    public void update(String title, String path) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title",title);
        values.put("path",path);
        db.update(TABLE, values, "path = ?", new String[]{path});
    }

    //查询所有
    public List<DownloadEntity> queryAll() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] columns = {"_id","url","name","size","path"};
        Cursor cursor = db.query(TABLE, columns, null, null, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            List<DownloadEntity> list = new ArrayList<>();
            while(cursor.moveToNext()) {
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String size = cursor.getString(cursor.getColumnIndex("size"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                list.add(new DownloadEntity(_id, url, name, size, path));
            }
            cursor.close();
            return list;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
}
