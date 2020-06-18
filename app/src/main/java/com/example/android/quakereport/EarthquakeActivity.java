/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;


public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<EarthquakeData>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static Context mContext;
    private RecyclerViewAdapter adapter;
    private RecyclerView earthquakeRecyclerView;
    private TextView emptyStateTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        mContext = this;

        adapter = new RecyclerViewAdapter(this, new ArrayList<EarthquakeData>());
        earthquakeRecyclerView = findViewById(R.id.recycler_view);
        earthquakeRecyclerView.setAdapter(adapter);
        earthquakeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyStateTextView = findViewById(R.id.empty_view);
        emptyStateTextView.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progress_bar_spinner);

        if (isInternetConnected()) {
            Log.e(LOG_TAG, "onCreate: initLoader is called form the Main activity");
            android.app.LoaderManager loaderManager = getLoaderManager();
            LoaderManager.getInstance(this).initLoader(0, null, this);
        } else {
            emptyStateTextView.setText(R.string.no_internet_connection);
            emptyStateTextView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @NonNull
    @Override
    public Loader<ArrayList<EarthquakeData>> onCreateLoader(int i, @Nullable Bundle bundle) {
        Log.e(LOG_TAG, "onCreateLoader: create the first and last loader");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(getString(R.string.settings_min_magnitude_key), getString(R.string.settings_min_magnitude_default));
        String limit = sharedPrefs.getString(getString(R.string.settings_limit_key), getString(R.string.settings_limit_default));
        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(QueryUtils.USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", limit);
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);
        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<EarthquakeData>> loader, ArrayList<EarthquakeData> earthquakeData) {
        Log.e(LOG_TAG, "onLoadFinished: update the UI with data you already have");
        progressBar.setVisibility(View.GONE);
        if (earthquakeData.isEmpty() || earthquakeData == null) {
            emptyStateTextView.setText(R.string.no_earthquakes);
            emptyStateTextView.setVisibility(View.VISIBLE);
            earthquakeRecyclerView.setVisibility(View.GONE);
        } else {
            adapter.setEarthquakeData(earthquakeData);
            earthquakeRecyclerView.setAdapter(adapter);
            earthquakeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<EarthquakeData>> loader) {
        Log.e(LOG_TAG, "onLoaderReset: called Done");
        adapter.setEarthquakeData(new ArrayList<EarthquakeData>());
    }

    public static Context getContext() {
        return mContext;
    }

}
