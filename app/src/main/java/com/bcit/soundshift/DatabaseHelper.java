package com.bcit.soundshift;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
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


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DATABASE_FILE = context.getDatabasePath(DATABASE_NAME);
        this.context = context;
    }

    private static final String TABLE_SONG = "song";
    private static final String ID_COL = "id";
    private static final String TITLE_COL = "title";
    private static final String FILENAME_COL = "filename";
    private static final String ALBUM_ID_COL = "album_id";
    private static final String ARTIST_ID_COL = "artist_id";
    private static final String LENGTH_COL = "length";

    private static final String TABLE_PLAYLIST_SONGS = "playlist_songs";
    private static final String PLAYLIST_ID_COL = "playlist_id";
    private static final String SONG_ID_COL = "song_id";

    private static final String TABLE_SHIFT_CONNECTION = "shift_connection";
    private static final String SHIFT_ID_COL = "shift_id";
    private static final String PLAYLIST_1_ID_COL = "playlist_1_id";
    private static final String PLAYLIST_2_ID_COL = "playlist_2_id";
    private static final String WEIGHT_COL = "weight";
    private static final String COMBINED_COLUMNS_COL = "combined_columns";

    private static final String TABLE_PLAYLIST = "playlist";
    private static final String PLAYLIST_NAME_COL = "playlist";
    private static final String PLAYLIST_WEIGHT_COL = "weight";

    private static final String TABLE_ARTIST = "artist";
    private static final String ARTIST_NAME_COL = "artist";

    private static final String TABLE_ALBUM = "album";
    private static final String ALBUM_NAME_COL = "album";
    private static final String COVER_ART_COL = "cover_art";

    private static final String TABLE_SHIFT = "shift";
    private static final String SHIFT_NAME_COL = "shift";

    public void createDataBase() throws IOException {
        context.deleteDatabase(DATABASE_NAME); //Delete this once we are done editing the database from the computer
        // If the database does not exist, copy it from the assets.
        boolean mDataBaseExist = checkDataBase();
        if (!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                // Copy the database from assets
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    public void writeToDownloads() {
        // Check if the app has permission to write to external storage
            // Permission is granted, proceed with writing the file
        try {
            // Get the Downloads directory
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            // Create the output file
            File outputFile = new File(downloadsDir, DATABASE_NAME);
            // Create parent directories if they don't exist
            outputFile.getParentFile().mkdirs();
            // Open the database file from the app's internal storage
            InputStream inputStream = new FileInputStream(context.getDatabasePath(DATABASE_NAME));
            // Open the output stream
            OutputStream outputStream = new FileOutputStream(outputFile);
            // Copy the database file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            // Close streams
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            // Show a toast message indicating success
            Toast.makeText(context, "Database exported to Downloads folder", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            // Show a toast message indicating failure
            Toast.makeText(context, "Error exporting database", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSongTable = "CREATE TABLE IF NOT EXISTS " + TABLE_SONG + " (" +
                ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TITLE_COL + " TEXT NOT NULL," +
                FILENAME_COL + " TEXT UNIQUE," +
                ALBUM_ID_COL + " INTEGER," +
                ARTIST_ID_COL + " INTEGER," +
                LENGTH_COL + " REAL," +
                "FOREIGN KEY(" + ALBUM_ID_COL + ") REFERENCES album(" + ID_COL + ")," +
                "FOREIGN KEY(" + ARTIST_ID_COL + ") REFERENCES artist(" + ID_COL + ")" +
                ");";

        String createPlaylistSongsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PLAYLIST_SONGS + " (" +
                PLAYLIST_ID_COL + " INTEGER," +
                SONG_ID_COL + " INTEGER," +
                "UNIQUE(" + PLAYLIST_ID_COL + "," + SONG_ID_COL + ")," +
                "FOREIGN KEY(" + SONG_ID_COL + ") REFERENCES " + TABLE_SONG + "(" + ID_COL + ")," +
                "FOREIGN KEY(" + PLAYLIST_ID_COL + ") REFERENCES " + TABLE_PLAYLIST + "(" + ID_COL + ")" +
                ");";

        String createShiftConnectionTable = "CREATE TABLE IF NOT EXISTS " + TABLE_SHIFT_CONNECTION + " (" +
                SHIFT_ID_COL + " INTEGER NOT NULL," +
                PLAYLIST_1_ID_COL + " INTEGER NOT NULL," +
                PLAYLIST_2_ID_COL + " INTEGER NOT NULL," +
                WEIGHT_COL + " INTEGER NOT NULL CHECK(" + WEIGHT_COL + " > 0 AND " + WEIGHT_COL + " <= 1)," +
                COMBINED_COLUMNS_COL + " TEXT GENERATED ALWAYS AS (CASE WHEN " + PLAYLIST_1_ID_COL + " < " + PLAYLIST_2_ID_COL + " THEN " + PLAYLIST_1_ID_COL + " || " + PLAYLIST_2_ID_COL + " || " + SHIFT_ID_COL + " ELSE " + PLAYLIST_2_ID_COL + " || " + PLAYLIST_1_ID_COL + " || " + SHIFT_ID_COL + " END) VIRTUAL," +
                "FOREIGN KEY(" + PLAYLIST_1_ID_COL + ") REFERENCES " + TABLE_PLAYLIST + "(" + ID_COL + ")," +
                "FOREIGN KEY(" + PLAYLIST_2_ID_COL + ") REFERENCES " + TABLE_PLAYLIST + "(" + ID_COL + ")," +
                "FOREIGN KEY(" + SHIFT_ID_COL + ") REFERENCES " + TABLE_SHIFT + "(" + ID_COL + ")" +
                ");";

        String createPlaylistTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PLAYLIST + " (" +
                ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PLAYLIST_NAME_COL + " TEXT NOT NULL UNIQUE," +
                PLAYLIST_WEIGHT_COL + " REAL NOT NULL CHECK(" + PLAYLIST_WEIGHT_COL + " <= 1 AND " + PLAYLIST_WEIGHT_COL + " > 0)" +
                ");";

        String createArtistTable = "CREATE TABLE IF NOT EXISTS " + TABLE_ARTIST + " (" +
                ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ARTIST_NAME_COL + " TEXT NOT NULL UNIQUE" +
                ");";

        String createAlbumTable = "CREATE TABLE IF NOT EXISTS " + TABLE_ALBUM + " (" +
                ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ALBUM_NAME_COL + " TEXT NOT NULL UNIQUE," +
                COVER_ART_COL + " BLOB" +
                ");";

        String createShiftTable = "CREATE TABLE IF NOT EXISTS " + TABLE_SHIFT + " (" +
                ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SHIFT_NAME_COL + " TEXT NOT NULL UNIQUE" +
                ");";

        //String createUniqueIndex = "CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_combination_shift ON " + TABLE_SHIFT_CONNECTION + " (" +
        //       COMBINED_COLUMNS_COL + ") WHERE " + SHIFT_ID_COL + " IS NOT NULL;";

        db.execSQL(createSongTable);
        db.execSQL(createPlaylistSongsTable);
        db.execSQL(createShiftConnectionTable);
        db.execSQL(createPlaylistTable);
        db.execSQL(createArtistTable);
        db.execSQL(createAlbumTable);
        db.execSQL(createShiftTable);
        //db.execSQL(createUniqueIndex);
    }

    public void addNewSong(String title, String filename, int albumId, int artistId, double length) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TITLE_COL, title);
        values.put(FILENAME_COL, filename);
        values.put(ALBUM_ID_COL, albumId);
        values.put(ARTIST_ID_COL, artistId);
        values.put(LENGTH_COL, length);

        db.insert(TABLE_SONG, null, values);
        db.close();
    }

    public void addNewPlaylistSong(int playlistId, int songId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PLAYLIST_ID_COL, playlistId);
        values.put(SONG_ID_COL, songId);

        db.insert(TABLE_PLAYLIST_SONGS, null, values);
        db.close();
    }

    public void modifyData(String tableName, int id, ArrayList<String> newDataList) {
        switch (tableName) {
            case "playlist":
                modifyPlaylist(id, newDataList.get(1), Double.parseDouble(newDataList.get(2)));
                break;
            case "artist":
                modifyArtist(id, newDataList.get(1));
                break;
            case "album"://TODO Change album table to included artist_id
                modifyAlbum(id, newDataList.get(1), null);
                break;
            case "song":
                modifySong(id, newDataList.get(1), newDataList.get(2), Integer.parseInt(newDataList.get(3)), Integer.parseInt(newDataList.get(4)), Double.parseDouble(newDataList.get(5)));
                break;
            case "shift":
                modifyShift(id, newDataList.get(1));
                break;
            default:
                // Handle default case if needed
                break;
        }
    }

    public ArrayList<String> getColumnDataTypes(String tableName) {
        ArrayList<String> columnDataTypes = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String dataType = cursor.getString(cursor.getColumnIndex("type"));
                    columnDataTypes.add(dataType);
                }
            } finally {
                cursor.close();
            }
        }

        db.close();

        return columnDataTypes;
    }

    public void addNewShiftConnection(int shiftId, int playlist1Id, int playlist2Id, float weight) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SHIFT_ID_COL, shiftId);
        values.put(PLAYLIST_1_ID_COL, playlist1Id);
        values.put(PLAYLIST_2_ID_COL, playlist2Id);
        values.put(WEIGHT_COL, weight);

        db.insert(TABLE_SHIFT_CONNECTION, null, values);
        db.close();
    }

    public void addNewPlaylist(String playlistName, double weight) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PLAYLIST_NAME_COL, playlistName);
        values.put(PLAYLIST_WEIGHT_COL, weight);

        db.insert(TABLE_PLAYLIST, null, values);
        db.close();
    }

    public void addNewArtist(String artistName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ARTIST_NAME_COL, artistName);

        db.insert(TABLE_ARTIST, null, values);
        db.close();
    }

    public void addNewAlbum(String albumName, byte[] coverArt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ALBUM_NAME_COL, albumName);
        values.put(COVER_ART_COL, coverArt);

        db.insert(TABLE_ALBUM, null, values);
        db.close();
    }

    public void addNewShift(String shiftName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SHIFT_NAME_COL, shiftName);

        db.insert(TABLE_SHIFT, null, values);
        db.close();
    }

    // Remove song by ID
    public void removeSong(int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("song", "id=?", new String[]{String.valueOf(songId)});
        db.close();
    }

    // Remove playlist by ID
    public void removePlaylist(int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("playlist", "id=?", new String[]{String.valueOf(playlistId)});
        db.close();
    }

    // Remove artist by ID
    public void removeArtist(int artistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("artist", "id=?", new String[]{String.valueOf(artistId)});
        db.close();
    }

    // Remove album by ID
    public void removeAlbum(int albumId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("album", "id=?", new String[]{String.valueOf(albumId)});
        db.close();
    }

    // Remove shift by ID
    public void removeShift(int shiftId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("shift", "id=?", new String[]{String.valueOf(shiftId)});
        db.close();
    }

    public void removeSongFromPlaylist(int playlistId, int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("playlist_songs", "playlist_id=? AND song_id=?", new String[]{String.valueOf(playlistId), String.valueOf(songId)});
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHIFT_CONNECTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHIFT);
        db.execSQL("DROP INDEX IF EXISTS idx_unique_combination_shift");

        onCreate(db);
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

    public void modifyPlaylist(int id, String newName, double newWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("playlist", newName);
        values.put("weight", newWeight);
        db.update("playlist", values, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void modifySong(int id, String newTitle, String newFileName, int newAlbumId, int newArtistId, double newLength) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", newTitle);
        values.put("filename", newFileName);
        values.put("album_id", newAlbumId);
        values.put("artist_id", newArtistId);
        values.put("length", newLength);
        db.update("song", values, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void modifyAlbum(int id, String newAlbum, byte[] newCoverArt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("album", newAlbum);
        values.put("cover_art", newCoverArt);
        db.update("album", values, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void modifyArtist(int id, String newArtist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("artist", newArtist);
        db.update("artist", values, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void modifyShift(int id, String newShift) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("shift", newShift);
        db.update("shift", values, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void modifyPlaylistSong(int playlistId, int songId, int newPlaylistId, int newSongId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM playlist_songs WHERE playlist_id=? AND song_id=?", new Object[]{playlistId, songId});
        ContentValues values = new ContentValues();
        values.put("playlist_id", newPlaylistId);
        values.put("song_id", newSongId);
        db.insert("playlist_songs", null, values);
        db.close();
    }
    public ArrayList<String> getColumnNames(String tableName) {
        ArrayList<String> columnNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String columnName = cursor.getString(cursor.getColumnIndex("name"));
                columnNames.add(columnName);
            }
            cursor.close();
        }
        return columnNames;
    }

    public Cursor executeQuery(String query) {
        SQLiteDatabase db = getReadableDatabase();
        Log.i(TAG, query);
        Cursor cursor = db.rawQuery(query, null);
        // Do not close db here, let the caller handle it
        return cursor;
    }

    public Cursor executeQuery(String query, ArrayList<String> values) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = values.toArray(new String[0]);
        String log = query;
        for (int i = 0; i < values.size(); i++) {
            log = log + " " + values.get(i);
        }
        Log.i(TAG, log);
        Cursor cursor = db.rawQuery(query, selectionArgs);
        // Do not close db here, let the caller handle it
        return cursor;
    }

    public Cursor executeQuery(String query, String values) {
        ArrayList<String> array = new ArrayList<String>();
        array.add(values);
        return executeQuery(query, array);
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
        // Close the cursor
        if (cursor != null) {
            cursor.close();
        }

        return resultList;
    }

    public static ArrayList<String> cursorToSingleList(Cursor cursor) {
        ArrayList<ArrayList<String>> resultList = new ArrayList<>();
        resultList = cursorToList(cursor);

        return resultList.get(0);
    }

    public static String cursorToSingleColumn(Cursor cursor) {
        ArrayList<String> resultList = new ArrayList<>();
        resultList = cursorToSingleList(cursor);

        return resultList.get(0);
    }

    static final String SongListQuery =
            "SELECT song.title, playlist.id, song.id, playlist.weight " +
                    "FROM playlist " +
                    "INNER JOIN playlist_songs " +
                    "    ON playlist.id = playlist_songs.playlist_id " +
                    "INNER JOIN song " +
                    "    ON song.id = playlist_songs.song_id " +
                    "WHERE playlist.id = :param2 AND song.id != :param3 " +
                    "UNION " +
                    "SELECT song.title, playlist.id, song.id, shift_connection.weight " +
                    "FROM shift_connection " +
                    "INNER JOIN playlist " +
                    "    ON shift_connection.playlist_2_id = playlist.id " +
                    "INNER JOIN playlist_songs " +
                    "    ON playlist.id = playlist_songs.playlist_id " +
                    "INNER JOIN song " +
                    "    ON song.id = playlist_songs.song_id " +
                    "WHERE shift_connection.playlist_1_id = :param2 AND shift_connection.shift_id = :param1 AND song.id != :param3 " +
                    "UNION " +
                    "SELECT song.title, playlist.id, song.id, shift_connection.weight " +
                    "FROM shift_connection " +
                    "INNER JOIN playlist " +
                    "    ON shift_connection.playlist_1_id = playlist.id " +
                    "INNER JOIN playlist_songs " +
                    "    ON playlist.id = playlist_songs.playlist_id " +
                    "INNER JOIN song " +
                    "    ON song.id = playlist_songs.song_id " +
                    "WHERE shift_connection.playlist_2_id = :param2 AND shift_connection.shift_id = :param1 AND song.id != :param3";
    static final String AllPlaylistsQuery =
            "SELECT shift_connection.playlist_1_id " +
                    "FROM shift_connection " +
                    "WHERE shift_connection.shift_id = :param1 " +
                    "UNION " +
                    "SELECT shift_connection.playlist_2_id " +
                    "FROM shift_connection " +
                    "WHERE shift_connection.shift_id = :param1";
    static final String PlaylistSongArtistNames = "SELECT playlist.playlist, song.title, artist.artist " +
                    "FROM playlist_songs " +
                    "INNER JOIN song " +
                    "ON playlist_songs.song_id = song.id " +
                    "INNER JOIN playlist " +
                    "ON playlist_songs.playlist_id = playlist.id " +
                    "INNER JOIN artist " +
                    "ON song.artist_id = artist.id " +
                    "WHERE playlist.id = :param1 AND song.id = :param2";

    @SuppressLint("Range")
    public int getRowIdIfExists(String tableName, String columnName, String value) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {"id"};
        String selection = columnName + " = ?";
        String[] selectionArgs = {value};

        Cursor cursor = db.query(tableName, columns, selection, selectionArgs, null, null, null);

        int id = -1; // Default value if row doesn't exist

        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
        }

        db.close();
        return id;
    }


    public void removeShiftConnection(int shiftId, int playlist1Id, int playlist2Id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Execute an SQL DELETE query to remove the shift connection record with the given parameters
        db.delete(TABLE_SHIFT_CONNECTION,
                SHIFT_ID_COL + " = ? AND " +
                        PLAYLIST_1_ID_COL + " = ? AND " +
                        PLAYLIST_2_ID_COL + " = ?",
                new String[]{String.valueOf(shiftId), String.valueOf(playlist1Id), String.valueOf(playlist2Id)});
        db.close();
    }
}
