package com.devbracket.audiorecorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.devbracket.audiorecorder.listener.OnDatabaseChangedListener;

import java.util.ArrayList;
import java.util.List;

public class DB extends SQLiteOpenHelper {
    private Context mContext;

    private static final String LOG_TAG = "DBHelper";

    private static OnDatabaseChangedListener mOnDatabaseChangedListener;

    public static final String DATABASE_NAME = "saved_recordings.db";
    private static final int DATABASE_VERSION = 1;

    public static abstract class DBHelperItem implements BaseColumns {
        public static final String TABLE_NAME = "saved_recordings";

        public static final String COLUMN_NAME_RECORDING_NAME = "recording_name";
        public static final String COLUMN_NAME_RECORDING_FILE_PATH = "file_path";
        public static final String COLUMN_NAME_RECORDING_LENGTH = "length";
        public static final String COLUMN_NAME_TIME_ADDED = "time_added";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBHelperItem.TABLE_NAME + " (" +
                    DBHelperItem._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_RECORDING_NAME + TEXT_TYPE + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH + TEXT_TYPE + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_RECORDING_LENGTH + " INTEGER " + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_TIME_ADDED + " INTEGER " + ")";

    @SuppressWarnings("unused")
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBHelperItem.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
        mOnDatabaseChangedListener = listener;
    }


    public List<RecordItem> getall() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {DBHelperItem._ID, DBHelperItem.COLUMN_NAME_RECORDING_NAME, DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH, DBHelperItem.COLUMN_NAME_RECORDING_LENGTH, DBHelperItem.COLUMN_NAME_TIME_ADDED};
        Cursor c = db.query(DBHelperItem.TABLE_NAME, projection, null, null, null, null, null);
        List<RecordItem> list = new ArrayList<>();
        while (c.moveToNext()){
            list.add(new RecordItem(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_RECORDING_NAME))
                    ,c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH))
                    ,c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_RECORDING_LENGTH)),
                    c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_TIME_ADDED)))); }
        c.close();
        return list;
    }

    public void removeItemWithId(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = { String.valueOf(id) };
        db.delete(DBHelperItem.TABLE_NAME, "_ID=?", whereArgs);
    }

    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { DBHelperItem._ID };
        Cursor c = db.query(DBHelperItem.TABLE_NAME, projection, null, null, null, null, null);
        int count = c.getCount();
        c.close();
        return count;
    }

    public Context getContext() {
        return mContext;
    }


    public long addRecording(String recordingName, String filePath, long length) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_NAME, recordingName);
        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH, filePath);
        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_LENGTH, length);
        cv.put(DBHelperItem.COLUMN_NAME_TIME_ADDED, System.currentTimeMillis());
        long rowId = db.insert(DBHelperItem.TABLE_NAME, null, cv);

        if (mOnDatabaseChangedListener != null) {
            mOnDatabaseChangedListener.onNewDatabaseEntryAdded();
        }

        return rowId;
    }


    public static class RecordItem{
       public String name,file,length,date;

        public RecordItem(String name,String file, String length, String date) {
            this.name = name;
            this.file = file;
            this.length = length;
            this.date = date;
        }
    }

    public void delete(){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "Delete from "+DBHelperItem.TABLE_NAME;
        db.execSQL(sql);
    }

    public void deleteAt(String str){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "Delete from "+DBHelperItem.TABLE_NAME+" where "+DBHelperItem.COLUMN_NAME_RECORDING_NAME+" = "+str;
        db.execSQL(sql);
    }
}
