package com.example.diazdukegel.diceme;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

        /*
         * Read data from the database, should one exist, and display saved items.
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

            arrayHolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = (Cursor) arrayHolder.getItemAtPosition(position);
                    String data = cursor.getString(2);
                    //Toast.makeText(DisplaySavedPasses.this, "Password: " + data, Toast.LENGTH_SHORT).show();
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("copyPass", data);
                    clipboardManager.setPrimaryClip(clip);
                    Toast.makeText(DisplaySavedPasses.this, "PASSWORD COPIED TO CLIPBOARD", Toast.LENGTH_SHORT).show();
                }
            });

            // TODO: Add onItemLockClickListener to the ListView
            // This will be used to facilitate delete and/or copy of data.
            //AdapterView.OnItemLongClickListener test = new AdapterView.OnItemLongClickListener();
            //arrayHolder.setOnItemLongClickListener(new AdapterView.onItemLongClickListener());
            //listedCategoriesAdapter.notifyDataSetChanged();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_load_dictionaries:
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;

            case R.id.action_savedPasswords:
                Toast.makeText(this, "Any saved passwords are displayed below.",
                        Toast.LENGTH_LONG).show();

            case R.id.action_generate_passwords:
                Intent generatePasses = new Intent(this,GeneratePassword.class);
                startActivity(generatePasses);
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}