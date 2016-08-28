package com.andrewrobertclifton.pokesniper;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CODE = 1;
    private String requestUrl = "https://skiplagged.com/api/pokemon.php?bounds=40.701339,-74.0282637,40.80042,-73.9268037";

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settings){
            Intent intent=new Intent();
            intent.setClass(this,SettingsActivity.class);
            startActivityForResult(intent,CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new PokeFindTask().execute();
    }

    private class PokeFindTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;
            String json = null;
            try {
                URL url = new URL(requestUrl);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    StringBuilder stringBuilder = new StringBuilder();
                    inputStream = connection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    json = stringBuilder.toString();
                    Log.d(TAG, json);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                tryClose(bufferedReader);
                tryClose(inputStream);
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray pokemons = (JSONArray) jsonObject.get("pokemons");
                JSONObject[] pokemonArray = new JSONObject[pokemons.length()];
                for (int x = 0; x < pokemons.length(); x++) {
                    pokemonArray[x] = (JSONObject) pokemons.get(x);
                }
                PokeAdapter listAdapter = (PokeAdapter) recyclerView.getAdapter();
                if (listAdapter == null) {
                    listAdapter = new PokeAdapter(MainActivity.this,pokemonArray);
                    recyclerView.swapAdapter(listAdapter, true);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static void tryClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
