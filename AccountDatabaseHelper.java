package com.cheddarsecurity.cheddarsafe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

/**
 * This class is the helper class that builds, adds, and removes entries in the
 * database containing all of the folders and the items in those as well.
 */
public class AccountDatabaseHelper extends SQLiteOpenHelper {

    /** Variables **/
    public static final String DATABASE_NAME = "cheddarsafe.db";
    public static final String ACC_TABLE_NAME = "acc_data";
    public static final String INFO_TABLE_NAME = "info_data";
    public static final String COL1 = "username";
    public static final String COL2 = "password";
    public static final String COL3 = "accountId";
    public static final String COL4 = "folder";

    /**
     * This is the constructor for this class.
     *
     * @param context The context for which this class acts upon.
     */
    public AccountDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    /**
     * Creates the database with all of the tables and fields as well.
     *
     * @param db The database that gets passed in.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createAccTable = "CREATE TABLE " + ACC_TABLE_NAME + "(username TEXT, password TEXT)";
        db.execSQL(createAccTable);
        String createInfoTable = "CREATE TABLE " + INFO_TABLE_NAME + "(username TEXT, password TEXT, accountId TEXT, folder TEXT)";
        db.execSQL(createInfoTable);
    }

    /**
     * If the version is updated, then drop the tables and recreate them
     * using the new version.
     *
     * @param db The database to update.
     * @param oldVersion The old version number.
     * @param newVersion The new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ACC_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INFO_TABLE_NAME);
        onCreate(db);
    }



    /**
     * Drops the account table.
     */
    public void dropAccTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(ACC_TABLE_NAME,"1",null);
    }
    ///////////////////////////////////////////////////////

    // LIST GET METHODS //

    /**
     * Gets the items from a specific folder.
     *
     * @param folder The folder to get the items from.
     * @return Returns a cursor with all the folder contents.
     */
    public Cursor getInfoAccountIdContents(String folder) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT accountId FROM " + INFO_TABLE_NAME + " WHERE username != '<>' AND " + COL4 + " = ?", new String[]{folder});
    }

    /**
     * Gets the folder contents in a specific order.
     *
     * @param folder The folder to get the items from.
     * @param ascending A boolean value if the folder items are ascending or not.
     * @return Returns a cursor with the folder contents ordered.
     */
    public Cursor getInfoAccountIdContentsOrdered(String folder, Boolean ascending) {
        SQLiteDatabase db = this.getReadableDatabase();
        String order;
        if(ascending) {
            order = "ASC";
        } else {
            order = "DESC";
        }
        return db.rawQuery("SELECT accountId FROM " + INFO_TABLE_NAME + " WHERE username != '<>' AND " + COL4 + " = ? ORDER BY accountId " + order, new String[]{folder});
    }

    /**
     * Drops the info table.
     */
    public void dropInfoTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(INFO_TABLE_NAME,"1",null);
    }

    /**
     * Gets user info with the given name.
     *
     * @param name The name of the account to pull information from.
     * @return Returns a cursor with the given username information.
     */
    public Cursor getUserInfo(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT " + COL1 + " FROM " + INFO_TABLE_NAME + " WHERE " + COL3 + " = ?"
                , new String[]{name});
    }

    /**
     * Gets pass info with the given name.
     *
     * @param name The name of the account to pull information from.
     * @return Returns a cursor with the given password information.
     */
    public Cursor getPassInfo(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT " + COL2 + " FROM " + INFO_TABLE_NAME + " WHERE " + COL3 + " = ?"
                , new String[]{name});
    }

    ///////////////////////////////////////////////////////

    // EDIT ENTRY METHODS //

    /**
     * Adds information data to the database.
     *
     * @param item1 The username of the account.
     * @param item2 The password of the account.
     * @param item3 The account id of the account.
     * @param item4 The folder the account is in.
     * @return True or false if the operation was successful.
     */
    public boolean addInfoData(String item1, String item2, String item3, String item4) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues add = new ContentValues();
        add.put(COL1, item1);
        add.put(COL2, item2);
        add.put(COL3, item3);
        add.put(COL4, item4);

        long result1 = db.insert(INFO_TABLE_NAME, null, add);
        if(result1 == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Deletes the entry in the database.
     *
     * @param user The username of the account.
     * @param pass The password of the account.
     * @param name The name of the account.
     * @param folder The folder the account is in.
     */
    public void deleteEntry(String user, String pass, String name, String folder) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(INFO_TABLE_NAME, COL1 + " = ? AND " + COL2 + " = ? AND "+ COL3 + " = ? AND " + COL4 + " = ?", new String[]{user, pass, name, folder});
    }
    ///////////////////////////////////////////////////////

    // FOLDER METHODS //

    /**
     * Gets the contents of a folder.
     *
     * @return Returns a cursor of the contents of a folder.
     */
    public Cursor getInfoFolderContents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT folder FROM " + INFO_TABLE_NAME + " WHERE " + COL1 + " = '<>' AND " + COL2 + " = '<>' AND " + COL3 + " = '<>' ", null);
    }

    /**
     * Adds a new entry into a folder.
     *
     * @param item1 The name of the folder to add to.
     * @return Returns true if the operation was successful.
     */
    public boolean addFolderData(String item1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues add = new ContentValues();
        add.put(COL1, "<>");
        add.put(COL2, "<>");
        add.put(COL3, "<>");
        add.put(COL4, item1);

        long result1 = db.insert(INFO_TABLE_NAME, null, add);
        if(result1 == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Deletes a folder with the given name.
     *
     * @param folder The folder to delete.
     */
    public void deleteFolder(String folder) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(INFO_TABLE_NAME, COL4 + " = ?", new String[]{folder});
    }

    /**
     * Updates the folder name.
     *
     * @param newName The new name of the folder to update to.
     * @param oldName The old name of the folder to change.
     */
    public void updateFolder(String newName, String oldName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL4, newName);
        db.update(INFO_TABLE_NAME, cv, COL4 + " = ?", new String[]{oldName});
    }

    /**
     * Checks to see if the folder repeats already.
     *
     * @param folder The name of the folder to see if it is repeated.
     * @return True or false if folder is repeated or not.
     */
    public boolean getIfFolderRepeat(String folder) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT " + COL4 + " FROM " + INFO_TABLE_NAME + " WHERE " + COL4 + " = ?"
                , new String[]{folder});
        return data.getCount() != 0;
    }

    /**
     * Get the order of the folder order.
     *
     * @param ascending True or false if the folders are ascending or not.
     * @return Returns a cursor of the order folders.
     */
    public Cursor getFoldersOrdered(Boolean ascending) {
        SQLiteDatabase db = this.getReadableDatabase();
        String order;
        if(ascending) {
            order = "ASC";
        } else {
            order = "DESC";
        }
        return db.rawQuery("SELECT " + COL4 + " FROM " + INFO_TABLE_NAME + " WHERE username = '<>' ORDER BY " + COL4 + " " + order, null);
    }
    ///////////////////////////////////////////////////////

    // SETTINGS METHOD //

    /**
     * Gets the array of the settings.
     *
     * @return Returns a string array of the different settings.
     */
    public String[] getSettingsArray() {
        String settingsText1 = "Change account information";
        String settingsText2 = "Change email address";
        String[] settingsArray = new String[] {settingsText1, settingsText2};
        return settingsArray;
    }
}
