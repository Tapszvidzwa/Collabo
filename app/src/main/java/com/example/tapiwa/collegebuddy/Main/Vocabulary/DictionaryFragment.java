package com.example.tapiwa.collegebuddy.Main.Vocabulary;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

//add dependencies to your class
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.Main.HomePageFragment;
import com.example.tapiwa.collegebuddy.Main.MainFrontPage;
import com.example.tapiwa.collegebuddy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tapiwa on 10/5/17.
 */

public class DictionaryFragment extends Fragment {


    Button searchButton;
    TextInputEditText word_to_search;
    TextView searchedWord, meaning;
    View vocabSearchView;
    public String rootWordSearched, searchedMeaning;

        public DictionaryFragment() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            vocabSearchView = inflater.inflate(R.layout.vocabulary_fragment, container, false);

            MainFrontPage.toolbar.setTitle("Vocabulary");

            searchButton = (Button) vocabSearchView.findViewById(R.id.seach_word_button);
            word_to_search = (TextInputEditText) vocabSearchView.findViewById(R.id.word_to_search);
            searchedWord = (TextView) vocabSearchView.findViewById(R.id.searched_word);
            meaning = (TextView) vocabSearchView.findViewById(R.id.meaning);

            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String searchTxt = word_to_search.getText().toString();
                    new CallbackTask().execute(inflections(searchTxt));
                }
            });


            return vocabSearchView;
        }

        private String dictionaryEntries(String rootWord) {
            final String language = "en";
            final String word = rootWord;
            final String word_id = word.toLowerCase();
            return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id;
        }


    private String inflections(String searchWord) {
        final String language = "en";
        final String word = searchWord;
        final String word_id = word.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/inflections/" + language + "/" + word_id;
    }

    private void searchForWordMeaning(String rootWord) {
        new CallbackTask().execute(dictionaryEntries(rootWord));
    }

    @Override
    public void onStop() {
        super.onStop();
        AppUsageAnalytics.incrementPageVisitCount("Dictionary");
    }



    //in android calling network requests on the main thread forbidden by default
    //create class to do async job
    private class CallbackTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            //TODO: replace with your own app id and app key
            final String app_id = "d5e57fa2";
            final String app_key = "e63ddb5ae509e11b9d95f01ff027de4b";
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestProperty("app_id",app_id);
                urlConnection.setRequestProperty("app_key",app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                return stringBuilder.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
         String text = "";

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray object = jsonObject.getJSONArray("results");


                    JSONObject obj = object.getJSONObject(0);
                    JSONArray lexicalEntries = obj.getJSONArray("lexicalEntries");
                    JSONObject obj2 = lexicalEntries.getJSONObject(0);
                    JSONArray obj2Arr = obj2.getJSONArray("inflectionOf");

                    JSONObject jsnObj = obj2Arr.getJSONObject(0);
                    text = jsnObj.getString("text");


            } catch (JSONException e) {
                Log.d("VOCAB_JSON_RESPONSE", e.toString());
            }

            searchedWord.setText(text);

            rootWordSearched = text;

            new CallbackTaskWordMeaning().execute(dictionaryEntries(text));
           // returnedText = text;
        }
    }

    private class CallbackTaskWordMeaning extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            //TODO: replace with your own app id and app key
            final String app_id = "d5e57fa2";
            final String app_key = "e63ddb5ae509e11b9d95f01ff027de4b";
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("app_id", app_id);
                urlConnection.setRequestProperty("app_key", app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                return stringBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

    try {
             JSONObject obj = new JSONObject(result);
        JSONArray results = obj.getJSONArray("results");

        int c = results.length();
        JSONObject resultsObject = results.getJSONObject(0);
        JSONArray ey  = resultsObject.getJSONArray("lexicalEntries");
        JSONObject obj2 = ey.getJSONObject(0);
        JSONArray obj3 = obj2.getJSONArray("entries");
        JSONObject obj4 = obj3.getJSONObject(0);
        JSONArray arr = obj4.getJSONArray("senses");
        JSONObject obj5 = arr.getJSONObject(0);
        JSONArray arr2 = obj5.getJSONArray("definitions");
        String word_meaning = arr2.getString(0);

        meaning.setText(word_meaning);

        return;

            } catch (JSONException d) {
    //

        }

        meaning.setText("Could not find word definition");

        }

        }
    }

