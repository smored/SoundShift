package com.bcit.soundshift;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "soundshift.db";
    private static final int DATABASE_VERSION = 1;
    private final File DATABASE_FILE;
    private final Context context;
    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DATABASE_FILE = context.getDatabasePath(DATABASE_NAME);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create your database schema here
    }

    public void createDataBase() throws IOException {
        context.deleteDatabase(DATABASE_NAME); //Delete this once we are done editing the database from the computer
        // If the database does not exist, copy it from the assets.
        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                // Copy the database from assests
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private boolean checkDataBase() {
        return DATABASE_FILE.exists();
    }

    private void copyDataBase() throws IOException {
        InputStream mInput = context.getAssets().open(DATABASE_NAME);
        OutputStream mOutput = new FileOutputStream(DATABASE_FILE);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades here
        // This method is called when the database version is increased
    }

    public void openDatabase() throws SQLiteException {
        if (database != null && database.isOpen()) {
            return;
        }
        database = SQLiteDatabase.openDatabase(DATABASE_FILE.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if(database != null) {
            database.close();
        }
        super.close();
    }

    public Cursor executeQuery(String query) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(query, null);
    }

    public Cursor getData(String query, ArrayList<String> values)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = values.toArray(new String[0]);
        return db.rawQuery(query, selectionArgs);
    }

    public static String replaceNamedParams(String query, @NonNull ArrayList<String> values) {
        for (int i = 0; i < values.size(); i++) {
            String paramName = ":param" + (i + 1);
            query = query.replace(paramName, values.get(i));
        }
        return query;
    }

    public static ArrayList<ArrayList<String>> cursorToList(Cursor cursor) {
        ArrayList<ArrayList<String>> resultList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ArrayList<String> rowList = new ArrayList<>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    rowList.add(cursor.getString(i));
                }
                resultList.add(rowList);
            } while (cursor.moveToNext());
        }

        return resultList;
    }

    static final String SongListQuery =
            "SELECT song.title, song.id, playlist.playlist, playlist.weight " +
                    "FROM playlist " +
                    "INNER JOIN playlist_songs " +
                    "    ON playlist.id = playlist_songs.playlist_id " +
                    "INNER JOIN song " +
                    "    ON song.id = playlist_songs.song_id " +
                    "WHERE playlist.id = :param2 " +
                    "UNION " +
                    "SELECT song.title, song.id, playlist.playlist, shift_connection.weight " +
                    "FROM shift_connection " +
                    "INNER JOIN playlist " +
                    "    ON shift_connection.playlist_2_id = playlist.id " +
                    "INNER JOIN playlist_songs " +
                    "    ON playlist.id = playlist_songs.playlist_id " +
                    "INNER JOIN song " +
                    "    ON song.id = playlist_songs.song_id " +
                    "WHERE shift_connection.playlist_1_id = :param2 AND shift_connection.shift_id = :param1 " +
                    "UNION " +
                    "SELECT song.title, song.id, playlist.playlist, shift_connection.weight " +
                    "FROM shift_connection " +
                    "INNER JOIN playlist " +
                    "    ON shift_connection.playlist_1_id = playlist.id " +
                    "INNER JOIN playlist_songs " +
                    "    ON playlist.id = playlist_songs.playlist_id " +
                    "INNER JOIN song " +
                    "    ON song.id = playlist_songs.song_id " +
                    "WHERE shift_connection.playlist_2_id = :param2 AND shift_connection.shift_id = :param1";
    static final String AllPlaylistsQuery =
            "SELECT shift_connection.playlist_1_id " +
                    "FROM shift_connection " +
                    "WHERE shift_connection.shift_id = :param1 " +
                    "UNION " +
                    "SELECT shift_connection.playlist_2_id " +
                    "FROM shift_connection " +
                    "WHERE shift_connection.shift_id = :param1";
}
