package com.example.diazdukegel.diceme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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
    private Diceware dicePass;
    private Spinner numOfWordsSpinner;
    private int numOfWords;
    private String passwordOutput;
    private TextView passOutputTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_pass);
        genPassBtn = (Button)findViewById(R.id.genPassBtn);
        numOfWordsSpinner = (Spinner)findViewById(R.id.wordNumSpinner);
        passOutputTextView = (TextView)findViewById(R.id.passwordOutputTextView);
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
            }
        });
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_OK);
        finish();
    }

}
