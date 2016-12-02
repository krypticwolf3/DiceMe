package com.example.diazdukegel.diceme;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.Toast;

public class DisplaySavedPasses extends AppCompatActivity {

    private SQLiteDatabase db;
    private SQLSimple dbHelper;
    private Cursor dbCursor;

    private ListView arrayHolder;
    private SimpleCursorAdapter listedCategoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_saved_passes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        /**
         * Read data from the database, should one exist, and display saved items.
         * By Thomas Kegel.
         */

        dbHelper = new SQLSimple(this);
        db = dbHelper.getReadableDatabase();

       /*
         * Build the user interface here.
         */
        if (db == null) {
            Toast.makeText(this, "No Database found. Go back and make some passwords!", Toast.LENGTH_LONG).show();
            finish(); // Closes the DisplaySavedPasses activity to return to the main screen.
        } else {
            // Retrieve the data from storage.
            String query = "SELECT * FROM " + SQLSimple.TABLE_NAME;
            dbCursor = db.rawQuery(query, null);

            // TEST CODE:
            Toast.makeText(this, "LOADING PASSES INTO DISPLAY LIST.", Toast.LENGTH_LONG).show();

            // Add the queried data to the ListView.
            arrayHolder = (ListView) findViewById(R.id.listViewForDisplayPasses);


            listedCategoriesAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.two_line_list_item, dbCursor,
                    new String[]{SQLSimple.COL_NAME,SQLSimple.COL_PASS},
                    new int[]{android.R.id.text1,android.R.id.text2},
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

            /*
            listedCategoriesAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1, dbCursor,
                    new String[]{SQLSimple.COL_NAME},
                    new int[]{android.R.id.text1},
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            */

            // Associate the array layout with the adapter.
            arrayHolder.setAdapter(listedCategoriesAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Reestablish connections to the database.
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + SQLSimple.TABLE_NAME;
        dbCursor = db.rawQuery(query, null);

        // Load the data into the ListView.
        /*
        listedCategoriesAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, dbCursor,
                new String[]{SQLSimple.COL_NAME},
                new int[]{android.R.id.text1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        */

        listedCategoriesAdapter = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item, dbCursor,
                new String[]{SQLSimple.COL_NAME,SQLSimple.COL_PASS},
                new int[]{android.R.id.text1,android.R.id.text2},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


        // Associate the array layout with the adapter.
        arrayHolder.setAdapter(listedCategoriesAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Close the connections to the database.
        dbHelper.close();
        db.close();
    }

}
