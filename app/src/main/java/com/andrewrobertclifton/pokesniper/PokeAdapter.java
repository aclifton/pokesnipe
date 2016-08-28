package com.andrewrobertclifton.pokesniper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by user on 8/28/16.
 */
public class PokeAdapter extends RecyclerView.Adapter<PokeAdapter.ViewHolder> {
    private Context context;
    private Pokemon[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtName;
        public TextView txtLocation;
        public Button button;

        public ViewHolder(View itemView, TextView txtName, TextView txtLocation, Button button) {
            super(itemView);
            this.txtName = txtName;
            this.txtLocation = txtLocation;
            this.button = button;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PokeAdapter(Context context, Pokemon[] myDataset) {
        mDataset = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PokeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        TextView txtName = (TextView) v.findViewById(R.id.txtName);
        TextView txtLocation = (TextView) v.findViewById(R.id.txtLocation);
        Button button = (Button) v.findViewById(R.id.btnGoto);
        ViewHolder vh = new ViewHolder(v, txtName, txtLocation, button);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Pokemon pokemon = mDataset[position];
        holder.txtName.setText(pokemon.getName());
        holder.txtLocation.setText(pokemon.getLat() + "," + pokemon.getLon());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGPSUpdateIntent(context, pokemon.getLat(), pokemon.getLon());
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public void sendGPSUpdateIntent(Context context, double lat, double lon) {
        Intent intent = new Intent(Constants.FAKE_GPS_ACTION);
        intent.putExtra(Constants.FAKE_GPS_EXTRA_LAT, lat);
        intent.putExtra(Constants.FAKE_GPS_EXTRA_LONG, lon);
        intent.setPackage(Constants.FAKE_GPS_PACKAGE);
        context.startService(intent);
    }
}


