package ro.dalbe.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * ---------------------------------------------------------
 *  PURPOSE:
 *  ---------------------------------------------------------
 *  This class manages the local SQLite database.
 *  It demonstrates:
 *   - Database creation (onCreate)
 *   - Version control (onUpgrade)
 *   - Insert and read operations (basic CRUD)
 *  ---------------------------------------------------------
 *  It extends SQLiteOpenHelper — Android’s built-in helper
 *  that simplifies database management.
 * ---------------------------------------------------------
 */
public class PostDatabaseHelper extends SQLiteOpenHelper {

    // ---------------------------------------------------------
    // CONSTANTS — define database name, version, and table schema
    // ---------------------------------------------------------
    private static final String DATABASE_NAME = "posts.db";   // database file name
    private static final int DATABASE_VERSION = 1;            // version number
    private static final String TABLE_NAME = "posts";          // single table name

    // Columns in our table
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_BODY = "body";

    /**
     * Constructor passes name, factory, and version
     * to the SQLiteOpenHelper superclass.
     */
    public PostDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ---------------------------------------------------------
    // onCreate() — called only once, when the database is created
    // ---------------------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create the table
        // The ID acts as primary key for each post
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_BODY + " TEXT)";
        db.execSQL(createTable); // execute the SQL
    }

    // ---------------------------------------------------------
    // onUpgrade() — called if the version number increases
    // ---------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing table and recreate it.
        // Used when structure changes between versions.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // ---------------------------------------------------------
    // insertPost() — adds one Post object to the database
    // ---------------------------------------------------------
    public void insertPost(Post post) {
        // Step 1: Open database in writable mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Step 2: Create ContentValues (key-value pairs)
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, post.getId());
        values.put(COLUMN_TITLE, post.getTitle());
        values.put(COLUMN_BODY, post.getBody());

        // Step 3: Insert data — if same ID exists, replace it
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        // Step 4: Always close the database to avoid memory leaks
        db.close();
    }

    // ---------------------------------------------------------
    // getAllPosts() — reads all rows from the table and
    // converts them into a list of Post objects
    // ---------------------------------------------------------
    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();

        // Step 1: Open database in read-only mode
        SQLiteDatabase db = this.getReadableDatabase();

        // Step 2: Execute SQL query — SELECT * FROM posts
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        // Step 3: Move through each row in the result set
        if (cursor.moveToFirst()) {
            do {
                // Read data from each column
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BODY));

                // Create Post object and add to list
                posts.add(new Post(id, title, body));
            } while (cursor.moveToNext());
        }

        // Step 4: Clean up resources
        cursor.close();
        db.close();

        // Step 5: Return list of all posts
        return posts;
    }
}