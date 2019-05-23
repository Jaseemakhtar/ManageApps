package com.jsync.appsdeaddiction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MySQLHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "restricted_apps";
    private static final String COL_ID = "id";
    private static final String COL_APP_NAME = "app_name";
    private static final String COL_P_NAME = "package_name";
    private static final String COL_FROM = "_from";
    private static final String COL_TO = "_to";
    private static final String COL_ICON = "icon";

    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME
            + " ( "
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_APP_NAME + " TEXT, "
            + COL_P_NAME + " TEXT, "
            + COL_ICON + " TEXT, "
            + COL_FROM + " TEXT, "
            + COL_TO
            + ")";

    private final String SELECT_TABLE = "SELECT * FROM " + TABLE_NAME;

    public MySQLHelper(Context context) {
        super(context, "AppsDatabase", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void add(AppsListModel appsListModel){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_APP_NAME, appsListModel.getAppName());
        values.put(COL_P_NAME, appsListModel.getAppPackageName());
        values.put(COL_FROM, appsListModel.getFrom());
        values.put(COL_TO, appsListModel.getTo());
        values.put(COL_ICON, appsListModel.getAppIcon());
        sqLiteDatabase.insert(TABLE_NAME, null, values);
        sqLiteDatabase.close();
    }

    public ArrayList<AppsListModel> getAll(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ArrayList<AppsListModel> list = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_TABLE, null);
        if (cursor.moveToFirst()) {
            do {
                AppsListModel row = new AppsListModel();
                row.setAppName(cursor.getColumnName(cursor.getColumnIndex(COL_APP_NAME)));
                row.setAppPackageName(cursor.getColumnName(cursor.getColumnIndex(COL_P_NAME)));
                row.setAppIcon(cursor.getColumnName(cursor.getColumnIndex(COL_ICON)));
                row.setFrom(cursor.getColumnName(cursor.getColumnIndex(COL_FROM)));
                row.setTo(cursor.getColumnName(cursor.getColumnIndex(COL_TO)));
                list.add(row);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public int update(AppsListModel model){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_FROM, model.getFrom());
        values.put(COL_TO, model.getTo());

        // updating row
        int res =  sqLiteDatabase.update(TABLE_NAME, values, COL_ID + " = ?", new String[] { String.valueOf(model.getRowId()) });
        sqLiteDatabase.close();
        return res;
    }

    public void delete(AppsListModel model){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, COL_ID + " = ?", new String[] { String.valueOf(model.getRowId()) });
        sqLiteDatabase.close();
    }
}
