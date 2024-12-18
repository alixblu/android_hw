package com.example.bttuan6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBase extends SQLiteOpenHelper {

    private static final String db_name = "quanlidiem";
    private static final String table_input = "Points";
    private static final int db_version = 1;  // db_version phải là kiểu int

    private static final String key_phonenumber = "sdt";
    private static final String key_point = "point";
    private static final String key_note = "note";
    private static final String key_cur_date = "cur_date";
    private static final String key_time_create = "time";

    private Points Points;


    public DataBase(Context context) {
        super(context, db_name, null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Tạo bảng Points
        String createPointsTable = "CREATE TABLE " + table_input + " (" +
                key_phonenumber + " TEXT PRIMARY KEY, " +
                key_point + " TEXT, " +
                key_note + " TEXT, " +
                key_cur_date + " TEXT," +
                key_time_create + " TEXT)";
        sqLiteDatabase.execSQL(createPointsTable);

        // Tạo bảng account
        String createAccountTable = "CREATE TABLE account (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT)";
        sqLiteDatabase.execSQL(createAccountTable);

//         Chèn tài khoản mặc định admin/admin
        String insertDefaultAdmin = "INSERT INTO account (username, password) VALUES ('admin', 'admin')";
        sqLiteDatabase.execSQL(insertDefaultAdmin);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop table cũ nếu cần nâng cấp
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table_input);
        onCreate(sqLiteDatabase);
    }


    public void addPoint(Points points) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(key_phonenumber, points.getSdt());
        values.put(key_point, points.getPoint());
        values.put(key_note, points.getNote());
        values.put(key_cur_date, points.getCur_date());
        values.put(key_time_create, points.getTime_create());

        long result = db.insert(table_input, null, values);
        Log.d("DB_INSERT", "Insert result: " + result); // Kiểm tra kết quả insert
        db.close();
    }


    public Cursor getAllPoints() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + table_input, null);
    }

    public void updatePoint(Points points) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(key_point, points.getPoint());
        values.put(key_note, points.getNote());
        values.put(key_cur_date, points.getCur_date());

        // Cập nhật bản ghi dựa trên số điện thoại
        int result = db.update(table_input, values, key_phonenumber + "=?", new String[]{points.getSdt()});
        Log.d("DB_UPDATE", "Update result: " + result); // Kiểm tra kết quả update
        db.close();
    }

    public boolean isPhoneNumberExists(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(table_input,
                new String[]{key_phonenumber},
                key_phonenumber + "=?",
                new String[]{phoneNumber},
                null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public Points getPointByPhoneNumber(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(table_input,
                new String[]{key_point, key_note},
                key_phonenumber + "=?",
                new String[]{phoneNumber},
                null, null, null);
        Points point = null;
        if (cursor != null && cursor.moveToFirst()) {
            String currentPoint = cursor.getString(cursor.getColumnIndexOrThrow(key_point));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(key_note));
            point = new Points(phoneNumber, currentPoint, note, ""); // "cur_date" not needed here
        }
        if (cursor != null) {
            cursor.close();
        }
        return point;
    }

    public void deletePointByPhoneNumber(String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table_input, key_phonenumber + "=?", new String[]{phoneNumber});
        db.close();
    }

    public void addAccount(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);

        long result = db.insert("account", null, values);
        Log.d("DB_INSERT_ACCOUNT", "Insert result: " + result);
        db.close();
    }

    public boolean isAccountExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("account",
                new String[]{"username"},
                "username=?",
                new String[]{username},
                null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public boolean validateAccount(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("account",
                new String[]{"username"},
                "username=? AND password=?",
                new String[]{username, password},
                null, null, null);

        boolean valid = (cursor.getCount() > 0);
        cursor.close();
        return valid;
    }

    public void deleteAccount(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("account", "username=?", new String[]{username});
        db.close();
    }


    public boolean updateAccount(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);

        int result = db.update("account", values, "username=?", new String[]{username});
        db.close();
        // Trả về true nếu cập nhật thành công, ngược lại false
        return result > 0;
    }

}