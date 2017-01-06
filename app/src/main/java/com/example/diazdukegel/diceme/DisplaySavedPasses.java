package com.example.diazdukegel.diceme;


import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * DisplaySavedPasses - This class is used to define and display the section of DiceMe where users
 *                      may view, copy, or delete their saved passwords previously generated.
 */

public class DisplaySavedPasses extends AppCompatActivity {

    private boolean loaded;         // Flag tracking if a dictionary is loaded.
    private SQLiteDatabase db;      // Database object
    private SQLSimple dbHelper;     // Database helper object
    private Cursor dbCursor;        // Cursors can iterate through a database to find elements
    private ListView arrayHolder;   // The view framework for listing items on screen
    private SimpleCursorAdapter listedCategoriesAdapter; // Holds the results of a cursor search
    private int currPosition;       // Holds which item of the list is selected
    private String deleteCategory;  // Used to temporarily save the category to be deleted.
    AlertDialog alertDialog;        // Object that is used for a popup option message.

    /**
     * onCreate - Runs any time an instance of this activity is first opened (multiples possible)
     * @param savedInstanceState - the data bundle automatically generated, which we can add to.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_saved_passes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        /*
         * Read data from the database, should one exist, and display saved items.
         */

        Bundle loadedIntentBundle = getIntent().getExtras();
        if (loadedIntentBundle != null) {
            loaded = loadedIntentBundle.getBoolean(MainActivity.DICTIONARY_LOADED);
        }

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
            //Toast.makeText(this, "LOADING PASSES INTO DISPLAY LIST.", Toast.LENGTH_LONG).show();

            // Add the queried data to the ListView.
            arrayHolder = (ListView) findViewById(R.id.listViewForDisplayPasses);

            listedCategoriesAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.two_line_list_item, dbCursor,
                    new String[]{SQLSimple.COL_NAME,SQLSimple.COL_PASS},
                    new int[]{android.R.id.text1,android.R.id.text2},
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

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

            //DELETE ALERT DIALOG
            DialogInterface.OnClickListener actionListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch(i){
                        case 0:

                            Cursor cursor  = (Cursor) arrayHolder.getItemAtPosition(currPosition);
                            String category = cursor.getString(cursor.getColumnIndexOrThrow(SQLSimple.COL_NAME));
                            String whereClause = " " + "Label = ";
                            db.delete(SQLSimple.TABLE_NAME, whereClause +"'" + category + "'",null);
                            String[] allColumns = new String[]{"_id", SQLSimple.COL_NAME, SQLSimple.COL_PASS};
                            Cursor newCursor = db.query(SQLSimple.TABLE_NAME, allColumns, null, null, null, null, null);
                            listedCategoriesAdapter.swapCursor(newCursor);
                            break;
                        default:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this); //create alert dialog when deleting
            builder.setTitle("Are You Sure You Want to Remove This?");
            String[] options = new String[]{"Remove"};
            builder.setItems(options, actionListener);
            builder.setNegativeButton("Cancel", null);
            alertDialog = builder.create();

            arrayHolder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Cursor cursor = (Cursor) arrayHolder.getItemAtPosition(i);
                    deleteCategory = cursor.getString(cursor.getColumnIndexOrThrow(SQLSimple.COL_NAME));
                    Log.e("Delete",deleteCategory);
                    alertDialog.show();
                    currPosition = i;
                    return true;
                }
            });

            listedCategoriesAdapter.notifyDataSetChanged();
        }
    }

    /**
     * onResume - called every time this activity is resumed (i.e. after paused and then comes
     *              back into focus/use by the user).  It's main purpose, currently, is to reopen
     *              the program's database connections to properly store saved passwords.
     */
    @Override
    public void onResume() {
        super.onResume();

        // Reestablish connections to the database.
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + SQLSimple.TABLE_NAME;
        dbCursor = db.rawQuery(query, null);

        // Load the data into the ListView.
        listedCategoriesAdapter = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item, dbCursor,
                new String[]{SQLSimple.COL_NAME,SQLSimple.COL_PASS},
                new int[]{android.R.id.text1,android.R.id.text2},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        // Associate the array layout with the adapter.
        arrayHolder.setAdapter(listedCategoriesAdapter);
    }

    /**
     * onPause - called every time the program loses focus or is closed. Currently only used to
     *              close the program's database connections to avoid memory leaks.
     */
    @Override
    public void onPause() {
        super.onPause();

        // Close the connections to the database.
        dbHelper.close();
        dbCursor.close();
        db.close();
    }

    /**
     * onCreateOptionsMenu - standard memu setup.
     * @param menu - uses a standard menu object that is automatically given.
     * @return - always returns true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    /**
     * onOptionsItemSelected - custom menu options used to open up other sections of the app
     * @param item - uses the standard MenuItem given automatically.
     * @return boolean - true unless errors occur.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_load_dictionaries:
                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra(MainActivity.DICTIONARY_LOADED, loaded);
                startActivity(intent);
                finish();
                return true;

            case R.id.action_saved_passwords:
                Toast.makeText(this, "Any saved passwords are currently displayed.",
                        Toast.LENGTH_LONG).show();

                return true;

            case R.id.action_generate_passwords:
                if (loaded) {
                    Intent generatePasses = new Intent(this, GeneratePassword.class);
                    generatePasses.putExtra(MainActivity.DICTIONARY_LOADED, loaded);
                    startActivity(generatePasses);
                    finish();
                    return true;
                }

                Toast.makeText(this, "Pick a dictionary first.", Toast.LENGTH_LONG).show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}