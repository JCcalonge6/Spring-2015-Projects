package comjccalonge6.httpsgithub.jccalongescardpricer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JCcalonge on 5/12/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "searchManager",
            TABLE_SEARCHES = "searches",
            KEY_ID = "id",
            KEY_CARD = "cardName",
            KEY_IMAGEURI = "imageUri";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SEARCHES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CARD + " TEXT," + KEY_IMAGEURI + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + TABLE_SEARCHES);

        onCreate(db);
    }

    public void createHistory(History history) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_CARD, history.getCardName());
        values.put(KEY_IMAGEURI, history.getImageURI().toString());

        db.insert(TABLE_SEARCHES, null, values);
        db.close();
    }

    public History getHistory(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_SEARCHES, new String[] { KEY_ID, KEY_CARD, KEY_IMAGEURI }, KEY_ID + "=?", new String[] { String.valueOf(id)}, null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        History history = new History(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Uri.parse(cursor.getString(2)));
        db.close();
        cursor.close();
        return history;
    }

    public void deleteSearch(History history) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_SEARCHES, KEY_ID + "=?", new String[] { String.valueOf(history.getId()) });
        db.close();
    }

    public int getSearchesCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SEARCHES, null);
        int count = cursor.getCount();

        db.close();
        cursor.close();

        return count;
    }

    public int updateSearch(History history) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_CARD, history.getCardName());
        values.put(KEY_IMAGEURI, history.getImageURI().toString());

        int rowsAffected = db.update(TABLE_SEARCHES, values, KEY_ID + "=?", new String[] { String.valueOf(history.getId()) });

        db.close();

        return rowsAffected;
    }

    public List<History> getAllSearches() {
        List<History> searches = new ArrayList<History>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " +TABLE_SEARCHES, null);

        if (cursor.moveToFirst()) {
            do {
                searches.add(new History(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Uri.parse(cursor.getString(2))));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return searches;
    }
}
