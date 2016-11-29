package com.example.diazdukegel.diceme;


import android.util.Log;

import java.util.HashMap;
import java.util.Random;

/**
 * This class implements diceware password generation based on it's algorithim. That is,
 * a Random number generatore will generate a number from 1111 - 66666 INCLUSIVE which are the
 * min range and max range. 6 digits compose an "index" value and once an index is called, a word
 * associated to that index is pulled. This 'dictionary' is modeled by the use of a hashmap
 * Created by ivan on 11/28/16.
 */

public class Diceware {
    private int numberOfWords;
    private HashMap<Integer,String> map;
    private String password;
    private static int MIN = 1; //A die's lowest number is 1 traditionally
    private static int MAX = 6; //A die's highest number is 6 traditionally

    /**
     * This constructor will grab the mutator arguments, and set the variables based on the args
     * Also it will call the setPassword method to set the password variable to be the output from
     * the dictionary
     * @param words - An Integer value which is defined by the user, it is a number of words desired
     * @param dictionary - A hashmap which will be the dictionary in which to grab values from
     */
    public Diceware(int words, HashMap<Integer, String> dictionary){
        this.numberOfWords = words;
        this.map = dictionary;
        setPassword();
    }

    /**
     * This uses the integer value defined by the constructor above to get a number of words
     * from the dictionary and then concatenates them into one long string
     * E.g "cat dog barrel cookie airplane"
     */
    private void setPassword(){
        Random rand = new Random();
        int num;
        String words = "";
        String index;
        for(int i=0;i<numberOfWords;i++){
            index = "";
            for(int x=MIN; x<MAX; x++){
                num = rand.nextInt((MAX-MIN)+1)+MIN;
                index = index + Integer.toString(num);
            }
            Log.d("diceware","index: "+index);
            Log.d("diceWare","diceWareMap: " +index + " "+map.get(Integer.parseInt(index)));
            words = words + " " + map.get(Integer.parseInt(index));
            if(words.contains("null")){
                Log.d("diceWare","PASSWORD IS NULL");
            }
        }
        password = words;

    }

    /**
     * Returns the unique generated password
     * @return a String type, which is the password
     */
    public String getPassword(){
        return password;
    }
}
