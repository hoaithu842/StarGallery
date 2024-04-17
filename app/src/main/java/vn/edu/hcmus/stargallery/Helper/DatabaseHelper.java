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
    private static final String TABLE_TRASH = "Trash";
    private static final String ALBUM_NAMES = "AlbumNames";
    private static final String ALBUMS = "Albums";

    public DatabaseHelper(Application application) {
        File storagePath = application.getFilesDir();
        String myDbPath = storagePath + "/" + DATABASE_NAME;
        Log.d("DBpath", myDbPath);
        try {
            db = SQLiteDatabase.openDatabase(myDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            db.beginTransaction();
            try {
                createFavoriteTable(TABLE_FAVORITE);
                createAlbumNamesTable(ALBUM_NAMES);
                createAlbumsTable(ALBUMS);
                createFavoriteTable(TABLE_TRASH);
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

    public void createFavoriteTable(String TABLE_NAME) {
//        db.execSQL("drop table if exists " + TABLE_NAME);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + " ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " PATH TEXT UNIQUE);");
    }

    public void createAlbumNamesTable(String ALBUM_NAMES) {
        //        db.execSQL("drop table if exists " + ALBUM_NAMES);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ALBUM_NAMES + " ("
                + " ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " NAME TEXT UNIQUE);");
    }

    public void createAlbumsTable(String ALBUMS) {
        //        db.execSQL("drop table if exists " + ALBUMS);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ALBUMS + " ("
                + " ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " PATH TEXT UNIQUE, "
                + " FOREIGN KEY(ID) REFERENCES "+ALBUM_NAMES+"(ID));");
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

    public void insertTrash(String path) {
        try {
            db.beginTransaction();
            try {
                db.execSQL("INSERT INTO " + TABLE_TRASH + "(PATH) values ('" + path + "');");
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
                c1.close();
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

    public boolean albumExists(String albName) {
        boolean exists = false;
        try {
            db.beginTransaction();
            try {
                String sql = "SELECT * FROM " + ALBUM_NAMES + " WHERE NAME=" + albName;
                Cursor cursor = db.rawQuery(sql, new String[]{albName});
                exists = cursor.getCount() > 0;
                cursor.close();
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
        return exists;
    }

//    public ArrayList<String> getAlbumsName() {
//        ArrayList<String> albNames = new ArrayList<>();
//        try {
//            db.beginTransaction();
//            try {
//                String sql = "SELECT * FROM " + TABLE_FAVORITE;
//                Cursor c1 = db.rawQuery(sql, null);
//                c1.moveToPosition(-1);
//                while (c1.moveToNext()) {
//                    int ID = c1.getInt(0);
//                    String PATH = c1.getString(1);
//                    albNames.add(PATH);
//                }
//            } catch (SQLiteException e) {
//                //
//            } finally {
//                db.endTransaction();
//            }
////            db.close();
//        } catch (SQLiteException e) {
//            //
//        }
//        return favoriteImages;
//    }

    public void createAlbum(String ALBUM_NAME) {
        try {
            db.beginTransaction();
            try {
                db.execSQL("INSERT INTO " + ALBUM_NAMES + "(NAME) values ('" + ALBUM_NAME + "');");
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

    public ArrayList<String> getAlbumNames() {
        ArrayList<String> albumNames = new ArrayList<>();
        try {
            db.beginTransaction();
            try {
                String sql = "SELECT * FROM " + ALBUM_NAMES;
                Cursor c1 = db.rawQuery(sql, null);
                c1.moveToPosition(-1);
                while (c1.moveToNext()) {
                    int ID = c1.getInt(0);
                    String NAME = c1.getString(1);
                    albumNames.add(NAME);
                }
                c1.close();
            } catch (SQLiteException e) {
                //
            } finally {
                db.endTransaction();
            }
//            db.close();
        } catch (SQLiteException e) {
            //
        }
        return albumNames;
    }

    public ArrayList<String> getAlbumImages(String ALBUM_NAME) {
        ArrayList<String> favoriteImages = new ArrayList<>();
        try {
            db.beginTransaction();
            try {
//                String sql = "SELECT * FROM " + ALBUMS;
                String sql = "SELECT * FROM " + ALBUMS + " JOIN " + ALBUM_NAMES +
                        " WHERE " + ALBUM_NAMES + ".NAME=" + ALBUM_NAME;
                        //HOCSINH.MaLop=LOP.MaLop";
                Cursor c1 = db.rawQuery(sql, null);
                c1.moveToPosition(-1);
                while (c1.moveToNext()) {
                    int ID = c1.getInt(0);
                    String PATH = c1.getString(1);
                    favoriteImages.add(PATH);
                }
                c1.close();
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
}