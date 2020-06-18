package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<EarthquakeData> earthquakeData;

    public RecyclerViewAdapter(Context context, ArrayList<EarthquakeData> earthquakeData) {
        this.context = context;
        this.earthquakeData = earthquakeData;
    }

    public void setEarthquakeData(ArrayList<EarthquakeData> earthquakeData) {
        this.earthquakeData = earthquakeData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
//        Log.e("viewHolder", "onCreateViewHolder Item");
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
//        Log.e("viewHolder", "onBindViewHolder create date");
        final EarthquakeData currentEarthquake = earthquakeData.get(i);
        viewHolder.getMagnitude().setText(Double.toString(currentEarthquake.getMagnitude()));
        viewHolder.getLocationOffset().setText(currentEarthquake.getLocationOffset());
        viewHolder.getPrimaryLocation().setText(currentEarthquake.getPrimaryLocation());
        viewHolder.getDate().setText(currentEarthquake.getDate());
        viewHolder.getTime().setText(currentEarthquake.getTime());
        GradientDrawable magnitudeCircle = (GradientDrawable) viewHolder.getMagnitude().getBackground();
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);
        viewHolder.getLinearLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentEarthquake.getUrl()));
                context.startActivity(websiteIntent);
            }
        });
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(context, magnitudeColorResourceId);
    }

    @Override
    public int getItemCount() {
        return earthquakeData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView magnitude, locationOffset, primaryLocation, date, time;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.magnitude = itemView.findViewById(R.id.magnitude_text);
            this.primaryLocation = itemView.findViewById(R.id.city_text);
            this.locationOffset = itemView.findViewById(R.id.distance_text);
            this.date = itemView.findViewById(R.id.date_text);
            this.time = itemView.findViewById(R.id.time_text);
            this.linearLayout = itemView.findViewById(R.id.linear_layout_list_item);
        }

        public TextView getMagnitude() {
            return magnitude;
        }

        public TextView getPrimaryLocation() {
            return primaryLocation;
        }

        public TextView getLocationOffset() {
            return locationOffset;
        }

        public TextView getDate() {
            return date;
        }

        public TextView getTime() {
            return time;
        }

        public LinearLayout getLinearLayout() {
            return linearLayout;
        }
    }
}
