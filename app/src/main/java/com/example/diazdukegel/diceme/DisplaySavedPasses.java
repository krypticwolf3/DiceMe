package com.example.diazdukegel.diceme;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DisplaySavedPasses extends AppCompatActivity {

    private SQLSimple dbHelper;
    private SQLiteDatabase db;

    private NestedScrollView parentLayout;
    private LinearLayout childLayout;
    private TextView infoAtTopText;
    private TextView bodyText;
    private ArrayList<String> listedPasses;
    private ArrayAdapter<String> listedPassesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_saved_passes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
         * Build the user interface here.
         */

        parentLayout = (NestedScrollView) findViewById(R.id.displaySavedPassesParentView);
        childLayout = (LinearLayout) findViewById(R.id.displaySavedPassesChildView);
        infoAtTopText = (TextView) findViewById(R.id.infoAtTopText);
        bodyText = (TextView) findViewById(R.id.bodyText);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /**
         * Read data from the database, should one exist, and display saved items.
         * By Thomas Kegel.
         */

        dbHelper = new SQLSimple(this);
        db = dbHelper.getReadableDatabase();

        if (db == null) {
            infoAtTopText.setText("No Database found. Go back and make some passwords!");
        } else {
            // Remove the TextViews since we will use a ListView to display the retrieved data.
            childLayout.removeAllViewsInLayout();

            // Retrieve the data from storage.
            String query = "SELECT * FROM " + SQLSimple.COL_ID + "";
            Cursor dbCursor = db.rawQuery(query, null);
            dbCursor.moveToFirst();
            int position = 0;

            // TEST CODE:
            int count = 0;
            System.out.println("LOADING PASSES INTO DISPLAY LIST.");
            Toast.makeText(this, "LOADING PASSES INTO DISPLAY LIST.", Toast.LENGTH_LONG).show();

            while (!dbCursor.isAfterLast()) {
                listedPasses.add("Test Line: " + count++);
                dbCursor.moveToNext();
            }

            // TEST CODE:
            System.out.println("TEXT VIEWS REMOVED.");
            Toast.makeText(this, "TEXT VIEWS REMOVED.", Toast.LENGTH_LONG).show();

            // Add in the ListView.
            ListView arrayHolder = new ListView(this);
            ArrayAdapter myAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, listedPasses);

            // Associate the array layout with the adapter.
            arrayHolder.setAdapter(myAdapter);
            childLayout.addView(arrayHolder);
        }
    }
}
