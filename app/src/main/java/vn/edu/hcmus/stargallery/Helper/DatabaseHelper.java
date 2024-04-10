package vn.edu.hcmus.stargallery.Helper;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class DatabaseHelper {
    SQLiteDatabase db;
    private static final String DATABASE_NAME = "StarGalleryDB";
    private static final String TABLE_FAVORITE = "Favorite";

    // Table Columns
    private static final String KEY_ID = "ID";
    private static final String KEY_PATH = "PATH";
    public DatabaseHelper(Application application) {
        File storagePath = application.getFilesDir();
        String myDbPath = storagePath + "/" + DATABASE_NAME;
        Log.d("DBpath", myDbPath);
        try {
            db = SQLiteDatabase.openDatabase(myDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            db.beginTransaction();
            try {
                createFavoriteTable(TABLE_FAVORITE);
                db.setTransactionSuccessful(); //commit your changes
            } catch (SQLiteException e) {
                //
            } finally {
                db.endTransaction();
            }
//            db.close();
        } catch (SQLiteException e) {
            //
        }
    }

    void createFavoriteTable(String TABLE_NAME) {
//        db.execSQL("drop table if exists " + TABLE_NAME);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + " ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " PATH TEXT UNIQUE);");
    }

    public void insertFavoriteImage(String path) {
        try {
            db.beginTransaction();
            try {
                db.execSQL("INSERT INTO " + TABLE_FAVORITE + "(PATH) values ('" + path + "');");
                db.setTransactionSuccessful(); //commit your changes
            } catch (SQLiteException e) {
                //
            } finally {
                db.endTransaction();
            }
//            db.close();
        } catch (SQLiteException e) {
            //
        }

    }

    public ArrayList<String> getFavoriteImages() {
        ArrayList<String> favoriteImages = new ArrayList<>();
        try {
            db.beginTransaction();
            try {
                String sql = "SELECT * FROM " + TABLE_FAVORITE;
                Cursor c1 = db.rawQuery(sql, null);
                c1.moveToPosition(-1);
                while (c1.moveToNext()) {
                    int ID = c1.getInt(0);
                    String PATH = c1.getString(1);
                    favoriteImages.add(PATH);
                }
            } catch (SQLiteException e) {
                //
            } finally {
                db.endTransaction();
            }
//            db.close();
        } catch (SQLiteException e) {
            //
        }
        return favoriteImages;
    }

    public boolean favoriteContainsImage(String path) {
        try {
            db.beginTransaction();
            try {
                String sql = "SELECT * FROM " + TABLE_FAVORITE + " WHERE PATH='" + path + "'";
                Cursor c1 = db.rawQuery(sql, null);
                c1.moveToPosition(-1);
                while (c1.moveToNext()) {
                    return true;
                }
            } catch (SQLiteException e) {
                //
            } finally {
                db.endTransaction();
            }
//            db.close();
        } catch (SQLiteException e) {
            //
        }
        return false;
    }

    public void removeImageFromFavorite(String path) {
        try {
            db.beginTransaction();
            try {
                String sql = "DELETE FROM " + TABLE_FAVORITE + " WHERE PATH='" + path + "'";
                db.execSQL(sql);
                db.setTransactionSuccessful();
            } catch (SQLiteException e) {
                //
            } finally {
                db.endTransaction();
            }
//            db.close();
        } catch (SQLiteException e) {
            //
        }
    }
}