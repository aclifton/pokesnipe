package com.andrewrobertclifton.pokesniper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

/**
 * Created by user on 8/28/16.
 */
public class PokeAdapter extends RecyclerView.Adapter<PokeAdapter.ViewHolder> {
    private Context context;
    private List<Pokemon> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtName;
        public TextView txtLocation;
        public TextView txtExpire;
        public Button button;

        public ViewHolder(View itemView) {
            super(itemView);
            this.txtName = (TextView) itemView.findViewById(R.id.txtName);
            this.txtLocation = (TextView) itemView.findViewById(R.id.txtLocation);
            this.txtExpire = (TextView) itemView.findViewById(R.id.txtExpire);
            this.button = (Button) itemView.findViewById(R.id.btnGoto);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PokeAdapter(Context context, List<Pokemon> myDataset) {
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
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Pokemon pokemon = mDataset.get(position);
        holder.txtName.setText(pokemon.getName());
        holder.txtLocation.setText(pokemon.getLat() + "," + pokemon.getLon());
        holder.txtExpire.setText(String.format("%ds",pokemon.getExpireTime()-System.currentTimeMillis()/1000));
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
        return mDataset.size();
    }

    public void sendGPSUpdateIntent(Context context, double lat, double lon) {
        Intent intent = new Intent(Constants.FAKE_GPS_ACTION);
        intent.putExtra(Constants.FAKE_GPS_EXTRA_LAT, lat);
        intent.putExtra(Constants.FAKE_GPS_EXTRA_LONG, lon);
        intent.setPackage(Constants.FAKE_GPS_PACKAGE);
        context.startService(intent);
    }
}


