package com.example.android.quakereport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;

public class EarthquakeLoader extends AsyncTaskLoader<ArrayList<EarthquakeData>> {
    private String urlAPI;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        urlAPI = url;
    }

    @Override
    protected void onStartLoading() {
        Log.e("EarthquakeLoader", "onStartLoading: forceload method also called");
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<EarthquakeData> loadInBackground() {
        Log.e(this.toString(), "loadInBackground: make the in background thread to fetch the data form API ");
        if(urlAPI == null || urlAPI.isEmpty())
            return null;
        return QueryUtils.fetchEarthquakesData(urlAPI);
    }
}
