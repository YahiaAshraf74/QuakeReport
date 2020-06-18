package com.example.android.quakereport;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EarthquakeData {
    private String  location;
    private double magnitude;
    private String url;
    private long timeInMilliseconds;
    private Date dateObject;
    private String primaryLocation, locationOffset;

    public EarthquakeData(double magnitude, String location, long timeInMilliseconds,String url) {
        this.magnitude = magnitude;
        this.location = location;
        this.timeInMilliseconds = timeInMilliseconds;
        this.url = url;
        dateObject = new Date(timeInMilliseconds);
        splitLocation(location);
        setPrecision(magnitude);
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getLocation() {
        return location;
    }

    public long getTimeInMilliseconds() {
        return timeInMilliseconds;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */

    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    public String getTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(this.dateObject);
    }

    private void setPrecision(double magnitude){
        this.magnitude = (double) Math.round(magnitude * 10) / 10;
    }

    private void splitLocation(String location){
        final String LOCATION_SEPARATOR = " of ";
        if (location.contains(LOCATION_SEPARATOR)) {
            String[] parts = location.split(LOCATION_SEPARATOR);
            locationOffset = parts[0] + LOCATION_SEPARATOR;
            primaryLocation = parts[1];
        } else {
            locationOffset = EarthquakeActivity.getContext().getResources().getString(R.string.near_the);
            primaryLocation = location;
        }
    }

    public String getPrimaryLocation() {
        return primaryLocation;
    }

    public String getLocationOffset() {
        return locationOffset;
    }
}
