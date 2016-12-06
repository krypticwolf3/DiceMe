package com.example.diazdukegel.diceme;

import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

/**
 * This class handles the Activity referred to as "Generate pass" where the user can generate
 * a password based on number of words they want for their password
 * Created by ivan on 11/28/16.
 */

public class GeneratePassword extends AppCompatActivity {
    private HashMap<Integer,String> dictionary;
    MainActivity main;
    private Button genPassBtn;
    private Button saveButton;
    private Button overrideButton;
    private Diceware dicePass;
    private Switch spaceSwitch;
    private boolean spaced, saveBtnEnabled, atDialog;
    private Spinner numOfWordsSpinner;
    private int numOfWords;
    private String passwordOutput="";
    private TextView passOutputTextView, category;
    private AlertDialog alertDialog;
    private EditText pass;
    private String desiredCategory;
    private SQLSimple dbHelper;
    private SQLiteDatabase db;

    // Keywords used to save the current instance for the user.
    private static final String STORE_PASS = "pass";
    private static final String STORE_USED = "used";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_pass);
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        genPassBtn = (Button)findViewById(R.id.genPassBtn);
        saveButton = (Button) findViewById(R.id.savePassword);
        spaceSwitch = (Switch) findViewById(R.id.spaceToggleSwitch);
        numOfWordsSpinner = (Spinner)findViewById(R.id.wordNumSpinner);
        passOutputTextView = (TextView)findViewById(R.id.passwordOutputTextView);

        spaced = true;
        atDialog = false;
        saveBtnEnabled = false;
        spaceSwitch.setTextOn("Yes");
        spaceSwitch.setTextOff("No");
        spaceSwitch.setChecked(true);
        saveButton.setEnabled(false);
        dbHelper = new SQLSimple(this);
        db = dbHelper.getWritableDatabase();

        if (savedInstanceState != null) {
            passwordOutput = savedInstanceState.getString("pass");
            passOutputTextView.setText(passwordOutput);
            saveButton.setEnabled(savedInstanceState.getBoolean("used"));
            saveBtnEnabled = true;
        }


        passOutputTextView.setOnClickListener(new View.OnClickListener() {
            @Override // touch the newly generated password and it is copied to clipboard
            public void onClick(View v) {
                Toast.makeText(GeneratePassword.this, "PASSWORD COPIED TO CLIPBOARD", Toast.LENGTH_SHORT).show();
                ClipboardManager clipManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copyPass", passwordOutput);
                clipManager.setPrimaryClip(clip);
            }
        });

        spaceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    spaced = true;
                } else {
                    // The toggle is disabled
                    spaced = false;
                }
            }
        });

        genPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main = new MainActivity();
                numOfWords = Integer.parseInt(numOfWordsSpinner.getSelectedItem().toString());
                Log.d("numOfWOrds", "NumOfWOrds" + Integer.toString(numOfWords));
                dictionary = main.getDictionary();
                dicePass = new Diceware(numOfWords, dictionary, spaced);
                passwordOutput = dicePass.getPassword();
                passOutputTextView.setText(passwordOutput);
                saveButton.setEnabled(true);
                saveBtnEnabled = true;
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

                /* Use the atDialog boolean to track if the dialog was showing, in case
                the dialog is destroyed for any reason.  We can then save and retrieve data.*/
                atDialog = true;
                alertDialog.show();

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override // override dialog's save button to not close automatically
                    public void onClick(View v) {
                        boolean close = false;
                        //Toast.makeText(GeneratePassword.this, "Hi", Toast.LENGTH_SHORT).show();
                        ContentValues cv = new ContentValues();
                        desiredCategory = category.getText().toString();
                        String newPass = pass.getText().toString();

                        String query = "SELECT Label FROM " + SQLSimple.TABLE_NAME + " WHERE Label='"+desiredCategory+"'";
                        Cursor cursor = db.rawQuery(query,null);
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

                atDialog = false;
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

        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        category = (EditText) dialogView.findViewById(R.id.categoryEditTextField);
        pass = (EditText) dialogView.findViewById(R.id.passEditTextField);
        overrideButton = (Button) dialogView.findViewById(R.id.overrideBtn);
        overrideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                desiredCategory = category.getText().toString();
                String newPass = pass.getText().toString();
                cv.put(SQLSimple.COL_NAME, desiredCategory);
                cv.put(SQLSimple.COL_PASS, newPass);

                /* Tom's code to replace entries already existing. */
                // It works, I swear!  I only removed the previous update query.
                String update = "UPDATE " + SQLSimple.TABLE_NAME +
                        " SET " + SQLSimple.COL_PASS + "='" + newPass + "'" +
                        " WHERE " + SQLSimple.COL_NAME + "='" + desiredCategory + "'";

                db.execSQL(update);
                /* End of Tom's code. */

                Toast.makeText(GeneratePassword.this, "CATEGORY: " + desiredCategory +
                        " OVERWRITTEN",Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });


        pass.setEnabled(false);
        //editText.setText("test label");
        alertDialog = dialogBuilder.create();
    }

    ////////////////////////////////////////////
    // Define supporting lifecycle functions. //
    ////////////////////////////////////////////

    /*
     * To avoid data loss from standard Android operations, save data using
     * saveInstanceState
     */
    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {

        savedInstanceState.putString(STORE_PASS, passwordOutput);
        savedInstanceState.putBoolean(STORE_USED, saveBtnEnabled);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause(){
        super.onPause();

        // Avoid crashing because the alert dialog is open.
        if(alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

        // Close the connections to the database.
        dbHelper.close();
        db.close();
    }

    @Override
    public void onResume(){
        super.onResume();

        // If the user was at the alert dialong, show it again.
        if (atDialog) {
            Toast.makeText(this, "ATTEMPTING TO SHOW THE ALERT AGAIN.", Toast.LENGTH_LONG).show();
            alertDialog.show();
        }

        // Reopen the connections to the database.
        dbHelper = new SQLSimple(this);
        db = dbHelper.getWritableDatabase();
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
                finish();
                return true;

            case R.id.action_savedPasswords:
                Intent displaySavedPasses = new Intent(this,DisplaySavedPasses.class);
                startActivity(displaySavedPasses);
                finish();
                return true;

            case R.id.action_generate_passwords:
                Toast.makeText(this, "Create new passwords on this page.",
                        Toast.LENGTH_LONG).show();

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}