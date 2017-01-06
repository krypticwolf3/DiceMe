package com.example.diazdukegel.diceme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public static final String DICTIONARY_LOADED = "Is_one_loaded";
    public static HashMap<Integer, String> dictionaryOfWords = new HashMap<>();

    private BufferedReader read;
    private Context c = MainActivity.this;
    private boolean loaded;
    private static final int READ_REQUEST_CODE = 42; //google bs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Button presetDictionary = (Button)findViewById(R.id.presetDicBtn); //preset Dictionary btn
        Button userDictionary = (Button)findViewById(R.id.userDicBtn); //import dictionary btn

        Bundle loadedIntentBundle = getIntent().getExtras();
        loaded = (loadedIntentBundle != null) && (loadedIntentBundle.getBoolean(DICTIONARY_LOADED));

        /**
         * When this event triggers, it will read the preset raw file known as "dictionary.txt"
         * which has a hardcoded min and max index range. It will then read each line in the file
         * and split it based on white space, the index number gets converted into an integer
         * and the word associated to that index is kept as a String, then inserted into the HashMap
         */
        presetDictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loaded) {
                    InputStream file = c.getResources().openRawResource(R.raw.dictionary); //name of the preset dictionary file
                    read = new BufferedReader(new InputStreamReader(file));
                    try {
                        new loadDictionary().execute(read);
                        loaded = true;
                        startGenPassActivity();
                        finish();
                    } catch (Exception e) {
                        Log.d("AsyncTask", "AsyncFailure, trace: " + e);
                    }
                } else {
                    // The preset dictionary is already in use.
                    startGenPassActivity();
                    finish();
                }
            }
        });

        userDictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
    }

    /**
     * Returns the HashMap that was initialized by the AsyncTask
     * @return - Hashmap object
     */
    public HashMap<Integer, String> getDictionary(){
        return dictionaryOfWords;
    }

    /**
     * Start the generate password activity where the user can generate a unique password
     */
    private void startGenPassActivity(){
        Intent intent = new Intent(this, GeneratePassword.class);
        intent.putExtra(DICTIONARY_LOADED, loaded);
        startActivity(intent);
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an a plain text.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("text/plain");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                Uri uri = resultData.getData();
                try{
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    assert inputStream != null;
                    read = new BufferedReader(new InputStreamReader(
                            inputStream));
                    //Log.d("userDic","Uri :" + uri.getPath() + "\nreader: " + read);
                    new loadDictionary().execute(read);

                    loaded = true;
                    startGenPassActivity();
                }catch (Exception e){
                    Log.e("fileChoosingAnd", "ERROR:" + e);
                }
                //Log.i("fileChoosingAnd", "Uri: " + uri.toString());
            }
        }
    }



    /**
     * AsyncTask class that will spawn a new thread via Google thread handling style, and load in
     * the dictionary file and initialize the hashmap based on what it read from the dictionary.
     */
    private class loadDictionary extends AsyncTask<BufferedReader, Integer, String>{
        String result;

        @Override
        protected void onPreExecute(){
            //load dictionary toast
            Toast.makeText(getApplicationContext(),"Loading dictionary",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(BufferedReader... reader){

            try {
                String stringLine = read.readLine();

                while(stringLine != null) {
                    String[] word = stringLine.split("\\s+");
                    int key = Integer.parseInt(word[0]);
                    String value = word[1];
                    dictionaryOfWords.put(key, value);
                    //Log.d("userDic", "Key,value: " + key + " " + dictionaryOfWords.get(key));

                    stringLine = read.readLine();
                }
            } catch (Exception e) {
                Log.d("fileReading", e.toString());
                Log.d("fileReading", "FILE COULD NOT BE READ WTF");
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
            // TODO: Find remedy for deprecated code.
            setProgress(progress[0]);
            Log.d("AsyncTask","Progress: " + progress[0].toString());

        }

        @Override
        protected void onPostExecute(String dicName){
            Toast.makeText(getApplicationContext()," Dictionary Loaded",
                    Toast.LENGTH_SHORT).show();

            try {
                read.close();
            } catch (Exception e) {
                Log.d("onPostExecute", e.toString());
                Log.d("onPostExecute", "COULD NOT CLOSE THE READER.");
            }
        }
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
                // The user is already where they would pick a dictionary.
                Toast.makeText(this, "Your choices are displayed already.",
                        Toast.LENGTH_LONG).show();
                return true;

            case R.id.action_generate_passwords:
                // Go to make new passwords.
                if (loaded) {
                    Intent generatePasses = new Intent(this, GeneratePassword.class);
                    generatePasses.putExtra(DICTIONARY_LOADED, loaded);
                    startActivity(generatePasses);
                    finish();
                    return true;
                }

                Toast.makeText(this, "Pick a pre-set or custom dictionary first.",
                        Toast.LENGTH_LONG).show();
                return true;

            case R.id.action_saved_passwords:
                // View your saved passwords.
                Intent displaySavedPasses = new Intent(this, DisplaySavedPasses.class);
                displaySavedPasses.putExtra(DICTIONARY_LOADED, loaded);
                startActivity(displaySavedPasses);

                Toast.makeText(getApplicationContext(), "Viewing saved passwords.",
                        Toast.LENGTH_LONG).show();

                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
