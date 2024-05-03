package vn.edu.hcmus.stargallery.Helper;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
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
                createTable(TABLE_FAVORITE);
                createAlbumNamesTable(ALBUM_NAMES);
                createAlbumsTable(ALBUMS);
                createTable(TABLE_TRASH);
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
//________________CREATE TABLE________________

    public void createTable(String TABLE_NAME) {
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
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + ALBUMS + " ("
//                + " ID INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + " PATH TEXT UNIQUE, "
//                + " FOREIGN KEY(ID) REFERENCES "+ALBUM_NAMES+"(ID));");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ALBUMS +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "PATH TEXT, " +
                    "ALBUM_ID INTEGER, " +
                    "UNIQUE(PATH, ALBUM_ID), " +
                    "FOREIGN KEY(ALBUM_ID) REFERENCES " + ALBUM_NAMES + "(ID));");
    }
//________________FAVORITE________________
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
    public void insertMultiFavorite(ArrayList<String> paths) {
        if(paths.isEmpty())
            return;
        try {
            db.beginTransaction();
            try {
                // Loop through each path and insert it into the table
                for (String path : paths) {
                    db.execSQL("INSERT INTO " + TABLE_FAVORITE + "(PATH) VALUES ('" + path + "');");
                }
                db.setTransactionSuccessful(); // commit your changes
            } catch (SQLiteException e) {
                // Handle exception
            } finally {
                db.endTransaction();
            }
//        db.close(); // No need to close the database here
        } catch (SQLiteException e) {
            // Handle exception
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
                String sql = "SELECT * FROM " + TABLE_FAVORITE + " WHERE PATH='" + path + "');";
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
                String sql = "DELETE FROM " + TABLE_FAVORITE + " WHERE PATH='" + path + "');";
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
//________________TRASH________________
    public void AddToTrashImage(String path) {
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
    public void AddToTrashMultiImage(ArrayList<String> paths) {
        if(paths.isEmpty())
            return;
        try {
            db.beginTransaction();
            try {
                // Loop through each path and insert it into the table
                for (String path : paths) {
                    db.execSQL("INSERT INTO " + TABLE_TRASH + "(PATH) VALUES ('" + path + "');");
                }
                db.setTransactionSuccessful(); // commit your changes
            } catch (SQLiteException e) {
                // Handle exception
            } finally {
                db.endTransaction();
            }
//        db.close(); // No need to close the database here
        } catch (SQLiteException e) {
            // Handle exception
        }
    }
    public ArrayList<String> getTrashImages() {
        ArrayList<String> TrashImages = new ArrayList<>();
        try {
            db.beginTransaction();
            try {
                String sql = "SELECT * FROM " + TABLE_TRASH;
                Cursor c1 = db.rawQuery(sql, null);
                c1.moveToPosition(-1);
                while (c1.moveToNext()) {
                    int ID = c1.getInt(0);
                    String PATH = c1.getString(1);
                    TrashImages.add(PATH);
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
        return TrashImages;
    }
    public boolean TrashContainsImage(String path) {
        try {
            db.beginTransaction();
            try {
                String sql = "SELECT * FROM " + TABLE_TRASH + " WHERE PATH='" + path + "'";
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
    public void removeImageFromTrash(String path) {
        try {
            db.beginTransaction();
            try {
                String sql = "DELETE FROM " + TABLE_TRASH + " WHERE PATH='" + path + "');";
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
//________________OTHER ALBUM________________
    public ArrayList<String> getAlbums(){
        ArrayList<String> albums_name = new ArrayList<>();
        try {
            db.beginTransaction();
            try {
                String sql = "SELECT * FROM " + ALBUM_NAMES;
                Cursor c1 = db.rawQuery(sql, null);
                c1.moveToPosition(-1);
                while (c1.moveToNext()) {
                    int ID = c1.getInt(0);
                    String PATH = c1.getString(1);
                    albums_name.add(PATH);
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
        return albums_name;
    }
    public boolean createNewAlbum(String name){
        ArrayList<String> albums = getAlbums();
        if(albums.contains(name))
            return false;
        try {
            db.beginTransaction();
            try {
                db.execSQL("INSERT INTO " + ALBUM_NAMES + "(NAME) values ('" + name + "');");
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
        return true;
    }
    public void insertMultiToAlbum(String album_name, ArrayList<String> image_list) {
        if (image_list.isEmpty())
            return;

        try {
            db.beginTransaction();
            try {
                // Retrieve the ID of the album from AlbumNames table based on album_name
                String query = "SELECT ID FROM " + ALBUM_NAMES + " WHERE NAME=?";
                Cursor cursor = db.rawQuery(query, new String[]{album_name});
                int albumID = -1;
                if (cursor.moveToFirst()) {
                    albumID = cursor.getInt(cursor.getColumnIndex("ID"));
                }
                cursor.close();

                // If albumID is valid, insert the images into Albums table
                if (albumID != -1) {
                    // Prepare the SQL statement for insertion


                    // Loop through each image path and insert it into the table
                    for (String imagePath : image_list) {
                        String insertStatement = "INSERT INTO " + ALBUMS + " (PATH, ALBUM_ID) VALUES ('"
                              + imagePath + "', '" + albumID + "');";
                        // Ensure the path is properly escaped to prevent SQL injection
                        db.execSQL(insertStatement);
                    }

                    // Explicitly end the transaction and commit changes
                    db.setTransactionSuccessful();
                }
            } catch (SQLiteException e) {
                // Handle exception
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteException e) {
            // Handle exception
        }
    }


    public boolean albumExists(String albName) {
        boolean exists = false;
        try {
            db.beginTransaction();
            try {
                String sql = "SELECT * FROM " + ALBUM_NAMES + " WHERE NAME =?";
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
        ArrayList<String> album_names = new ArrayList<>();
        try {
            db.beginTransaction();
            try {
                String sql = "SELECT * FROM " + ALBUM_NAMES;
                Cursor c1 = db.rawQuery(sql, null);
                c1.moveToPosition(-1);
                while (c1.moveToNext()) {
                    int ID = c1.getInt(0);
                    String NAME = c1.getString(1);
                    album_names.add(NAME);
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
        return album_names;
    }
    public ArrayList<String> getAlbumImages(String ALBUM_NAME) {
        ArrayList<String> favoriteImages = new ArrayList<>();
        try {
            db.beginTransaction();
            try {
//                String sql = "SELECT * FROM " + ALBUMS;


                String sql = "SELECT * FROM " + ALBUMS + " JOIN " + ALBUM_NAMES
                        + " ON " + ALBUMS + ".ALBUM_ID = " + ALBUM_NAMES + ".ID WHERE "
                        + ALBUM_NAMES + ".NAME = '" + ALBUM_NAME + "'";

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