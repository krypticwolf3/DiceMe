package com.example.diazdukegel.diceme;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import static android.app.PendingIntent.getActivity;

/**
 * This class handles the Activity referred to as "Generate pass" where the user can generate
 * a password based on number of words they want for their password
 * Created by ivan on 11/28/16.
 */

public class GeneratePassword extends AppCompatActivity {
    private HashMap<Integer,String> dictionary;
    MainActivity main;
    private Button genPassBtn, saveButton, overrideButton;
    private Diceware dicePass;
    private Spinner numOfWordsSpinner;
    private int numOfWords;
    private String passwordOutput="";
    private TextView passOutputTextView, category;
    private AlertDialog alertDialog;
    private EditText pass;
    private String desiredCategory;
    private SQLSimple dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_pass);
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        genPassBtn = (Button)findViewById(R.id.genPassBtn);
        saveButton = (Button) findViewById(R.id.savePassword);
        numOfWordsSpinner = (Spinner)findViewById(R.id.wordNumSpinner);
        passOutputTextView = (TextView)findViewById(R.id.passwordOutputTextView);
        saveButton.setEnabled(false);
        dbHelper = new SQLSimple(this);
        db = dbHelper.getWritableDatabase();

        Intent callerIntent = getIntent();
        /*dictionaryOfWords = (HashMap<Integer, String>)callerIntent.getSerializableExtra(
                "dicitonaryOfWords");*/



        genPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main = new MainActivity();
                numOfWords = Integer.parseInt(numOfWordsSpinner.getSelectedItem().toString());
                Log.d("numOfWOrds","NumOfWOrds"+Integer.toString(numOfWords));
                dictionary = main.getDictionary();
                dicePass = new Diceware(numOfWords,dictionary);
                passwordOutput = dicePass.getPassword();
                passOutputTextView.setText(passwordOutput);
                saveButton.setEnabled(true);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(GeneratePassword.this, "Pass: " + passwordOutput, Toast.LENGTH_SHORT).show();
                if(passwordOutput.equals("")){
                    pass.setHint("Generate Password First!");
                }else {
                    pass.setText(passwordOutput);
                }
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override // override dialog's save button to not close automatically
                    public void onClick(View v) {
                        boolean close = false;
                        //Toast.makeText(GeneratePassword.this, "Hi", Toast.LENGTH_SHORT).show();
                        ContentValues cv = new ContentValues();
                        desiredCategory = category.getText().toString();
                        String newPass = pass.getText().toString();

                        Cursor cursor = null;
                        String query = "SELECT Label FROM " + SQLSimple.TABLE_NAME + " WHERE Label='"+desiredCategory+"'";
                        cursor = db.rawQuery(query,null);
                        Log.e("Category Count",  ""+ cursor.getCount());

                        System.out.println("CHECK: " + DatabaseUtils.dumpCursorToString(cursor));

                        // check to see how many of intented category is already present.
                        //if more than 0, then ask user to override
                        if(cursor.getCount() > 0){
                            Toast.makeText(GeneratePassword.this, "CATEGORY ALREADY EXISTS! PRESS TO OVERRIDE", Toast.LENGTH_SHORT).show();
                            overrideButton.setVisibility(View.VISIBLE);
                        }else{
                            cv.put(SQLSimple.COL_NAME, desiredCategory);
                            cv.put(SQLSimple.COL_PASS, newPass);
                            db.insert(SQLSimple.TABLE_NAME, null, cv);
                            close = true;
                        }
                        if(close){
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this); //build alert dialog
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override// no need for anything here. onclick will be overrided later.
            public void onClick(DialogInterface dialog, int which) {
                Log.e("Failed", "Try Again");
            }
        });

//      dialogBuilder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                Toast.makeText(GeneratePassword.this, "Pass: " + passwordOutput, Toast.LENGTH_SHORT).show();
//
//                ContentValues cv = new ContentValues();
//                desiredCategory = category.getText().toString();
//                String newPass = pass.getText().toString();
//
//                Cursor cursor = null;
//                String query = "SELECT Label FROM " + SQLSimple.TABLE_NAME + " WHERE Label='"+desiredCategory+"'";
//                cursor = db.rawQuery(query, null);
//                Log.e("Category Count",  ""+ cursor.getCount());
//                Toast.makeText(GeneratePassword.this, "HWAAAAA", Toast.LENGTH_SHORT).show();
//                if(cursor.getCount() > 0){
//                    Toast.makeText(GeneratePassword.this, "Category Already Exists. Press Button to Override", Toast.LENGTH_SHORT);
//
//                }else{
//                    cv.put("Label",desiredCategory);
//                    cv.put("Password", newPass);
//                    db.insert(SQLSimple.TABLE_NAME, null, cv);
//                    Toast.makeText(GeneratePassword.this, "Password Inserted to " + desiredCategory, Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                }
//            }
//        });
        //dialogBuilder.setNegativeButton("CANCEL", null);
        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        category = (EditText) dialogView.findViewById(R.id.category);
        pass = (EditText) dialogView.findViewById(R.id.password);
        overrideButton = (Button) dialogView.findViewById(R.id.override);
        overrideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                desiredCategory = category.getText().toString();
                String newPass = pass.getText().toString();
                cv.put(SQLSimple.COL_NAME, desiredCategory);
                cv.put(SQLSimple.COL_PASS, newPass);
                db.replace(SQLSimple.TABLE_NAME,null, cv);
                Toast.makeText(GeneratePassword.this, "CATEGORY: "+desiredCategory + " OVERRIDED",Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });


        pass.setEnabled(false);
        //editText.setText("test label");
        alertDialog = dialogBuilder.create();
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_OK);
        finish();
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
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }




}
