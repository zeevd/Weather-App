package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("TODO", "onCreate");

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("TODO", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("TODO","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("TODO","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("TODO","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("TODO","onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        else if (id == R.id.view_loc_on_map) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String zipcode = preferences.getString(getString(R.string.key_zipcode), getString(R.string.default_zipcode));
            Uri zipUri = Uri.parse("geo:0,0?q=" + zipcode);
            Intent showZipOnMap = new Intent(Intent.ACTION_VIEW, zipUri);
            if (showZipOnMap.resolveActivity(getPackageManager()) != null)
                startActivity(showZipOnMap);
            else
                Toast.makeText(MainActivity.this, "Could not locate maps application on device", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
