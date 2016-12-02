package com.example.diazdukegel.diceme;

import android.content.Context;
import android.content.Intent;
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
    public static HashMap<Integer, String> dictionaryOfWords = new HashMap<>();
    private BufferedReader read;
    private Button presetDictionary;
    private Button userDictionary;
    private Context c = MainActivity.this;
    private String stringLine = "";
    private String[] word;
    private int key;
    private String value;
    private Menu mi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        //mi.setIcon(R.drawable.ic_action_name);

        presetDictionary = (Button)findViewById(R.id.presetDicBtn); //preset Dictionary btn
        userDictionary = (Button)findViewById(R.id.userDicBtn); //import dictionary btn

        /**
         * When this event triggers, it will read the preset raw file known as "dictionary.txt"
         * which has a hardcoded min and max index range. It will then read each line in the file
         * and split it based on white space, the index number gets converted into an integer
         * and the word associated to that index is kept as a String, then inserted into the HashMap
         */
        presetDictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputStream file = c.getResources().openRawResource(R.raw.dictionary); //name of the preset dictionary file
                read = new BufferedReader(new InputStreamReader(file));
                try {
                    new loadDictionary().execute(read);
                    startGenPassActivity();
                }catch (Exception e){
                    Log.d("AsyncTask","AsyncFailure, trace: "+e);
                }
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
        Intent intent = new Intent(this,GeneratePassword.class);
        startActivity(intent);
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
            while(stringLine !=null) {
                try {
                    stringLine = read.readLine();
                    word = stringLine.split("\\s+");
                    key = Integer.parseInt(word[0]);
                    value = word[1];
                    dictionaryOfWords.put(key, value);
                } catch (Exception e) {
                    Log.d("fileReading", "FILE COULD NOT BE READ WTF");
                }
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
            setProgress(progress[0]);
            Log.d("AsyncTask","Progress: "+progress[0].toString());

        }

        @Override
        protected void onPostExecute(String dicName){
            Toast.makeText(getApplicationContext()," Dictionary Loaded",
                    Toast.LENGTH_SHORT).show();
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
                Intent loadDictionary = new Intent(this,MainActivity.class);
                startActivity(loadDictionary);
                return true;

            case R.id.action_savedPasswords:
                Intent displaySavedPasses = new Intent(this,DisplaySavedPasses.class);
                startActivity(displaySavedPasses);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
