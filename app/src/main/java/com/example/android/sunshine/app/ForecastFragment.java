package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class ForecastFragment extends Fragment {
    ListView weatherList;
    ArrayAdapter weatherAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onStart() {
        super.onStart();
        getWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_refresh:
                getWeather();

            default:
                return false;
        }
    }

    private void getWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String zipcode = preferences.getString(getString(R.string.key_zipcode),getString(R.string.default_zipcode));
        new FetchWeatherTask().execute(zipcode);
    }

    public ForecastFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);


        weatherList = (ListView) rootView.findViewById(R.id.listview_forecast);
        weatherAdapter = new ArrayAdapter(getActivity(),R.layout.list_item,R.id.list_item_textview,new ArrayList());
        weatherList.setAdapter(weatherAdapter);
        weatherList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String weatherString = (String) weatherAdapter.getItem(i);
                Intent moveToDetailIntent = new Intent(getActivity(),DetailActivity.class).putExtra("weather",weatherString);
                startActivity(moveToDetailIntent);
            }
        });


        return rootView;
    }

    class FetchWeatherTask extends AsyncTask<String,Void,String[]>{
        final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        final String OWM_API_KEY="b881dcc0dd10d3cf3fef6f1a6c8fbea8";
        final String DAY_COUNT = "7";
        final String UNITS = "metric";

        private String celsiusToFahrenheit(String tempInCelsius){
            return String.valueOf(Float.parseFloat(tempInCelsius) * (9/5) + 32);
        }

        @Override
        protected void onPostExecute(String[] s) {
            weatherAdapter.clear();
            weatherAdapter.addAll(Arrays.asList(s));
            Log.v("TODO REMOVE", "network op done");
        }

        @Override
        protected String[] doInBackground(String... strings) {
            if (strings==null || strings.length==0) return null;
            String zipCode = strings[0];
            StringBuffer json = new StringBuffer();
            String[] daysInfo = new String[7];

            try {
                Uri requestUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("appid", OWM_API_KEY)
                        .appendQueryParameter("cnt", DAY_COUNT)
                        .appendQueryParameter("zip", zipCode)
                        .appendQueryParameter("units", UNITS)
                        .build();
                URL requestUrl = new URL(requestUri.toString());
                HttpURLConnection owmConnection = (HttpURLConnection) requestUrl.openConnection();
                owmConnection.setRequestMethod("GET");
                owmConnection.connect();

                InputStream jsonStream = owmConnection.getInputStream();
                int temp = 0;
                while ((temp = jsonStream.read()) != -1){
                    json.append((char) temp);
                }

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String t = preferences.getString(getString(R.string.key_temperature), getString(R.string.default_temperature_option));
                boolean isCelsius = t.equals("0") ? true : false;

                JSONObject jsonObj = new JSONObject(json.toString());
                JSONArray days = jsonObj.getJSONArray("list");

                for (int i=0; i<7; i++) {
                    JSONObject day = days.getJSONObject(i);
                    JSONObject temperature = day.getJSONObject("temp");
                    String minTemp = isCelsius ? temperature.getString("min") : celsiusToFahrenheit(temperature.getString("min"));
                    String maxTemp = isCelsius ? temperature.getString("max") : celsiusToFahrenheit(temperature.getString("max"));
                    String main = day.getJSONArray("weather").getJSONObject(0).getString("main");
                    String description = minTemp + " " + maxTemp + " " + main;
                    daysInfo[i] = description;
                    Log.v("TODO",description);
                }
                Log.v("TODO REMOVE",json.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                return daysInfo;
            }
        }
    }
}
