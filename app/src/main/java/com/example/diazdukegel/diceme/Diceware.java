package com.example.diazdukegel.diceme;


import java.util.HashMap;
import java.util.Random;

/**
 * This class implements diceware password generation based on it's algorithim. That is,
 * a Random number generatore will generate a number from 11111 - 666666 INCLUSIVE which are the
 * min range and max range. 6 digits compose an "index" value and once an index is called, a word
 * associated to that index is pulled. This 'dictionary' is modeled by the use of a hashmap
 * Created by ivan on 11/28/16.
 */

public class Diceware {
    private int numberOfWords;
    private HashMap<Integer,String> dictionary = new HashMap<>();
    private String password = "";
    private static int MIN = 111111;
    private static int MAX = 666666;

    /**
     * This constructor will grab the mutator arguments, and set the variables based on the args
     * Also it will call the setPassword method to set the password variable to be the output from
     * the dictionary
     * @param words - An Integer value which is defined by the user, it is a number of words desired
     * @param dictionaryMap - A hashmap which will be the dictionary in which to grab values from
     */
    public Diceware(int words, HashMap<Integer, String> dictionaryMap){
        this.numberOfWords = words;
        this.dictionary = dictionaryMap;
        setPassword();
    }

    /**
     * This uses the integer value defined by the constructr above to get a number of words
     * from the dictionary and then concatenates them into one long string
     * E.g "cat dog barrel cookie airplane"
     */
    private void setPassword(){
        Random rand = new Random();
        int index = 0;
        for(int i=0;i<numberOfWords;i++){
            index = rand.nextInt((MAX-MIN)+1)+MIN;
            password = password + " " + dictionary.get(Integer.toString(index));
        }
    }

    /**
     * Returns the unique generated password
     * @return a String type, which is the password
     */
    public String getPassword(){
        return password;
    }
}
