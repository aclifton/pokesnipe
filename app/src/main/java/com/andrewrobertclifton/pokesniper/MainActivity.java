package com.andrewrobertclifton.pokesniper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CODE = 1;
    private static final String DEFAULT_BBOX = "40.701339,-74.0282637,40.80042,-73.9268037";
    private static final int PERMISSION_REQUEST = 5;
    private String requestUrlFormatString = "https://skiplagged.com/api/pokemon.php?bounds=%s";

    private LocationManager locationManager;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;
    private ArrayList<Pokemon> pokemonArray;
    PokeFindTask pokeFindTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refresh();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent();
            intent.setClass(this, SettingsActivity.class);
            startActivityForResult(intent, CODE);
        } else if (item.getItemId() == R.id.refresh) {
            refresh();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class PokeFindTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;
            String json = null;
            try {
                URL url = new URL(params[0]);
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
                Log.e(TAG, "MalformedURLException", e);
            } catch (IOException e) {
                Log.e(TAG, "IOException", e);
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
                if (json != null) {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray pokemons = (JSONArray) jsonObject.get("pokemons");
                    pokemonArray = new ArrayList<>(pokemons.length());
                    for (int x = 0; x < pokemons.length(); x++) {
                        pokemonArray.add(Pokemon.fromJSONObject((JSONObject) pokemons.get(x)));
                    }
                } else {
                    pokemonArray = new ArrayList<>(1);
                }
                String filterString = sharedPreferences.getString("filter", "");
                String[] filters = filterString.split(",");
                boolean reverse = sharedPreferences.getBoolean("reverse", false);
                ArrayList<Pokemon> filterPokemon = filterString.length() == 0 ?
                        pokemonArray :
                        (reverse ? reverseFilter(pokemonArray, filters) : filter(pokemonArray, filters));
                if (filterPokemon.size() == 0) {
                    filterPokemon.add(new Pokemon(-1, "Missingno", 0, 0, System.currentTimeMillis() / 1000));
                }
                int sort = Integer.valueOf(sharedPreferences.getString("sort", "0"));
                switch (sort) {
                    case 0:
                        Collections.sort(filterPokemon, Comparators.ID_COMPARATOR);
                        break;
                    case 1:
                        Collections.sort(filterPokemon, Comparators.NAME_COMPARATOR);
                        break;
                    case 2:
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
                            pokeFindTask = null;
                            return;
                        } else {
                            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            Collections.sort(filterPokemon, new Comparators.DistanceComparator(location.getLatitude(), location.getLongitude()));
                        }
                        break;
                    default:
                        break;
                }
                PokeAdapter listAdapter = new PokeAdapter(MainActivity.this, filterPokemon);
                recyclerView.swapAdapter(listAdapter, true);
            } catch (JSONException e) {
                Log.e(TAG, "JSONException", e);
            }
            pokeFindTask = null;
        }
    }

    private void refresh() {
        if (pokeFindTask == null) {
            String bbox = sharedPreferences.getString("bbox", "");
            if (bbox == null || bbox.length() == 0 || bbox.split(",").length != 4) {
                bbox = DEFAULT_BBOX;
            }
            String url = String.format(requestUrlFormatString, bbox);
            pokeFindTask = new PokeFindTask();
            pokeFindTask.execute(url);
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

    private static ArrayList<Pokemon> filter(ArrayList<Pokemon> pokemons, String... filters) {
        ArrayList<Pokemon> filtered = new ArrayList<>();
        for (Pokemon pokemon : pokemons) {
            for (String s : filters) {
                if (s.equalsIgnoreCase(pokemon.getName())) {
                    filtered.add(pokemon);
                    continue;
                }
            }
        }
        return filtered;
    }

    private static ArrayList<Pokemon> reverseFilter(ArrayList<Pokemon> pokemons, String... filters) {
        HashSet<Pokemon> filtered = new HashSet<>(pokemons);
        for (Pokemon pokemon : pokemons) {
            for (String s : filters) {
                if (s.equalsIgnoreCase(pokemon.getName())) {
                    filtered.remove(pokemon);
                    continue;
                }
            }
        }
        return new ArrayList<>(filtered);
    }

}
