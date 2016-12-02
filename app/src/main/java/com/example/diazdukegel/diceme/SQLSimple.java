package com.example.diazdukegel.diceme;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ivan on 11/21/16.
 * Imported from my QuizGame project
 */

class SQLSimple extends SQLiteOpenHelper {
    private static final String DB_NAME = "DiceDB";
    private static final int DB_VERSION = 2;
    static final String TABLE_NAME = "dicePasswords";
    private static final String COL_ID = "_id";
    static final String COL_NAME = "Label";
    static final String COL_PASS = "Password";
    private static final String STRING_CREATE =
            "CREATE TABLE " +TABLE_NAME + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + COL_NAME + " TEXT NOT NULL, " + COL_PASS + " TEXT NOT NULL);";

    /*private static final String STRING_CUSTOM_CREATE =
            " ( _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + COL_QUESTIONS + " TEXT NOT NULL, " + COL_QUESTION_TYPE + " TEXT NOT NULL, "
            + COL_ANSWERS + " TEXT NOT NULL, " + COL_ATTEMPT + " TEXT NOT NULL, " + COL_SCORE
            + " INTEGER NOT NULL);";
            */

    SQLSimple(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STRING_CREATE);
    }

    private void deleteTable(SQLiteDatabase db, String tableName){
        db.execSQL("DROP TABLE "+tableName);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    /**
     * Returns a query of everything inside of a table
     * @param db - the instance of the DB to look from
     * @param tableName - tableName to pull data from
     * @return return the query
     */
    public Cursor getAllFromTable(SQLiteDatabase db, String tableName){
        return db.query(tableName,
                new String[]{COL_ID,COL_NAME,COL_PASS},
                null,new String[]{},null,null,null);
    }

    /**
     * Returns an entire row of data that matches the row ID in the sqlite table
     * @param db the database instance
     * @param tableName  the table to look for the row in
     * @param row  the row to select and grab all data from
     * @return a Cursor type
     */
    public Cursor getRowFromTable(SQLiteDatabase db, String tableName, int row){
        return db.query(tableName,
                new String[]{COL_NAME,COL_PASS},
                "_id = "+row,new String[]{},null,null,null);

    }

    /**
     * This method will call the one above to delete all the tables in the DB
     * This will filter out two tables which cannot be deleted, sqlite_sequence and android_metadata
     * @param db - the Database object which to delete tables from
     * @param c - the cursor which contains the values of the DB object itself
     */
    public void deleteAllTables(SQLiteDatabase db, Cursor c){
        c.moveToFirst();
        while(!c.isAfterLast()){
            String tableName = c.getString(0);
            if(!tableName.contains("sqlite_sequence") && !tableName.contains("android_metadata")){
                deleteTable(db, tableName);
            }
            c.moveToNext();
        }
    }

    /**
     * Returns an entire col of data that matches the row ID in the sqlite table
     * @param db the database instance
     * @param tableName  the table to look for the col in
     * @param colName  the col to select and grab all data from
     * @return a Cursor type
     */
    public Cursor getColumnFromTable(SQLiteDatabase db, String tableName, String colName){
        return db.query(tableName, new String[]{COL_NAME, COL_PASS},
                " WHERE label = " + colName,new String[]{}, null, null, null);
    }
}
