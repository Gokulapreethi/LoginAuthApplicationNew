package com.example.jaish.loginauthapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

/**
 * Created by Jaish on 14-12-2018.
 */

public class SqHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    /**
     * DB Inside COMMedia Folder //
     */
    public static final String DATABASE_NAME = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/MyDatabase.db";
    public static SQLiteDatabase db = null;
    private static SqHelper dbHelper = null;
    Context context;
   public static final String CREATE_TABLE_LOGIN_DETAILS = "create table if not exists logindetails (id integer primary key autoincrement,name varchar(100),emailid varchar(10),password varchar(100),mobile varchar(13))";


    public SqHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    public static SqHelper getDB(Context cntxt) {
        if (dbHelper == null) {
            dbHelper = new SqHelper(cntxt);
            dbHelper.openDatabase();
        }
        return dbHelper;
    }

    /**
     * Open writable database when database is in null.
     */
    public void openDatabase() {
        if (db == null) {
            try {
                db = dbHelper.getWritableDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void openReadableDatabase() {
        try {
            if (db == null)
                db = getReadableDatabase();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_LOGIN_DETAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public synchronized void close() {
        super.close();
        db.close();
        db = null;
    }

    public String getLoginDetails(String mailId) {
        Cursor cur;
        String resultString=null;
        try {
            if (db == null)
                db = getReadableDatabase();
            try {
                if (db != null) {
                    if (!db.isOpen())
                        openDatabase();
                    cur = db.rawQuery("select * from logindetails where emailid = '" + mailId + "'", null);
                    cur.moveToFirst();
                    while (!cur.isAfterLast()) {
                        resultString=cur.getString(cur.getColumnIndex("password"));
                        cur.moveToNext();
                    }
                    cur.close();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return resultString;
        }
    }

    public boolean insertorupdateLoginDetails(LoginDetailsBean loginDetailsBean) {
        boolean result = false;

        try {
            int row_id = 0;

            if (!db.isOpen())
                openDatabase();
            if (!CheckFieldsExist(loginDetailsBean.getEmailid())) {
                ContentValues cv = new ContentValues();
                result=true;
                cv.put("name", loginDetailsBean.getName());
                cv.put("emailid", loginDetailsBean.getEmailid());
                cv.put("password", loginDetailsBean.getPassword());
                cv.put("mobile", loginDetailsBean.getMobileno());
                row_id = (int) db.insert("logindetails", null, cv);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return result;
    }


    public boolean CheckFieldsExist(String field) {
        boolean isExist = false;
        Cursor cur;
        try {
            if (db == null)
                db = getReadableDatabase();
            try {
                if (db != null) {
                    if (!db.isOpen())
                        openDatabase();

                    cur = db.rawQuery("select * from logindetails where emailid = '" + field + "'", null);
                    cur.moveToFirst();
                    while (!cur.isAfterLast()) {
                        isExist = true;
                        cur.moveToNext();
                    }
                    cur.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return isExist;
        }
    }
}

