
package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    public static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=5&limit=100";


    private QueryUtils() {
    }

    /**
     * open the connection between the app and USGS API to get the Json response
     */
    private static String makeHTTPRequest(URL url) {
        String JSONResponse = "";
        if (url == null) {
            return JSONResponse;
        }
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                JSONResponse = readFromStream(inputStream);
            } else {
                Log.e("makeHTTPRequest Method", "Error response code: " + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return JSONResponse;
    }

    /**
     * buffer the input stream and return the string of Json response
     */
    private static String readFromStream(InputStream inputStream) {
        String JSONResponse = "";
        if (inputStream == null) {
            return JSONResponse;
        }
        StringBuilder JSONResponseBuilder = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                JSONResponseBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONResponse = JSONResponseBuilder.toString();
        return JSONResponse;
    }

    /**
     * Return a URL object from the string which are the request from the API USGS
     */
    private static URL makeURL(String requestURL) {
        URL url = null;
        try {
            url = new URL(requestURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static ArrayList<EarthquakeData> fetchEarthquakesData(String requestURL){
        Log.e("QueryUtils", "fetchEarthquakesData: fetch the data");
        URL url = makeURL(requestURL);
        String JSONResponse = makeHTTPRequest(url);
        ArrayList<EarthquakeData> earthquakeData = extractEarthquakes(JSONResponse);
        return earthquakeData;
    }

    /**
     * Return a list of {@link EarthquakeData} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<EarthquakeData> extractEarthquakes(String JSONResponse) {
        if (TextUtils.isEmpty(JSONResponse)) {
            return new ArrayList<EarthquakeData>();
        }
        ArrayList<EarthquakeData> earthquakes = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(JSONResponse);
            JSONArray earthquakesJSON = root.getJSONArray("features");
            for (int i = 0; i < earthquakesJSON.length(); i++) {
                JSONObject earthquakeProperties = ((JSONObject) earthquakesJSON.get(i)).getJSONObject("properties");
                double mag = earthquakeProperties.getDouble("mag");
                String place = earthquakeProperties.getString("place");
                long timeInMilliseconds = earthquakeProperties.getLong("time");
                String url = earthquakeProperties.getString("url");
                earthquakes.add(new EarthquakeData(mag, place, timeInMilliseconds, url));
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return earthquakes;
    }

}