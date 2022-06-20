package com.ankit.music;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "tasks";
    private static final String SONG_ID = "id";
    private static final String SONG_ICON = "song_icon";
    private static final String SONG = "song";
    private static final String ARTIST = "artist";
    private static final String ALBUM = "album";
    private static final String FILE_NAME = "filename";
    private static final String FAVOURITE = "favourite";

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY autoincrement, %s string, %s string, %s string, %s string, %s string, %s string DEFAULT 'n')", TABLE_NAME, SONG_ID, SONG_ICON, SONG, ARTIST, ALBUM, FILE_NAME, FAVOURITE);
        db.execSQL(query);
    }

    public void addSong(String song, String artist, String album, String filename) {
        this.getWritableDatabase().execSQL(String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES('%s', '%s', '%s', '%s')", TABLE_NAME, SONG, ARTIST, ALBUM, FILE_NAME, song, artist, album, filename));
    }

    public ArrayList<ArrayList<Object>> getAllSong() {
        ArrayList<ArrayList<Object>> return_array = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s ORDER BY %S DESC", TABLE_NAME, SONG_ID), null);
            if (cursor.moveToFirst()) {
                do {
                    ArrayList<Object> array_member = new ArrayList<>();
                    int song_id = cursor.getInt(0);
                    String song_icon = cursor.getString(1);
                    String song = cursor.getString(2);
                    String artist = cursor.getString(3);
                    String album = cursor.getString(4);
                    String filename = cursor.getString(5);
                    String is_favourite = cursor.getString(6);

                    array_member.add(song_id);
                    array_member.add(song_icon);
                    array_member.add(song);
                    array_member.add(artist);
                    array_member.add(album);
                    array_member.add(filename);
                    array_member.add(is_favourite);


                    return_array.add(array_member);
                }
                while (cursor.moveToNext()) ;
            }
            cursor.close();
        }
        catch (CursorIndexOutOfBoundsException e){
            return return_array;
        }
        return return_array;
    }


    public int getMaxSongId() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery(String.format("SELECT max(%s) FROM %s", SONG_ID, TABLE_NAME), null);
            cursor.moveToFirst();
            int index = cursor.getInt(0);
            cursor.close();
            return index;
        } catch (CursorIndexOutOfBoundsException e) {
            return 0;
        }
    }


    public void updateSong(int task_id, String title, String description) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format("UPDATE %s set title='%1$s', description='%2$s', date_created=datetime('now', 'localtime') WHERE task_id=%3$s", title, description, task_id);
        db.execSQL(query);
    }

    public void delete_task(int task_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM tasks WHERE task_id="+task_id);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
