package com.example.diazdukegel.diceme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private BufferedReader read;
    private Button presetDictionary;
    private Button userDictionary;
    private Context c = MainActivity.this;
    private String stringLine = "";
    private String[] word;
    private int key;
    private String value;
    private HashMap<Integer, String> dictionaryOfWords = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                while(stringLine !=null){
                    try{
                        stringLine = read.readLine();
                        Log.d("fileReading","stringLine: "+stringLine);
                        word = stringLine.split("\\s+");
                        Log.d("fileReading","word: "+word[0] + " " + word[1]);
                        key = Integer.parseInt(word[0]);
                        value = word[1];
                        dictionaryOfWords.put(key,value);
                    }catch(Exception e){
                        Log.d("fileReading","FILE COULD NOT BE READ WTF");
                    }
                    startGenPassActivity();
                }
            }
        });

    }

    /**
     * Start the generate password activity where the user can generate a unique password
     */
    private void startGenPassActivity(){
        Intent intent = new Intent(this,GeneratePassword.class);
        intent.putExtra("dictionaryWords",dictionaryOfWords);
        startActivity(intent);
    }


}
