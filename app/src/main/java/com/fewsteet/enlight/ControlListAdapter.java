package com.fewsteet.enlight;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import net.vector57.mrpc.MRPC;

import java.util.ArrayList;

/**
 * Created by peter on 12/8/16.
 */

public class ControlListAdapter extends RecyclerView.Adapter <ControlListAdapter.ViewHolder> {
    private ArrayList<String> paths;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView group_label;
        public ToggleButton group_toggle;
        public ViewHolder(View v) {
            super(v);
            group_label = (TextView)v.findViewById(R.id.group_label);
            group_toggle = (ToggleButton)v.findViewById(R.id.group_toggle);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ControlListAdapter(ArrayList<String> paths) {
        this.paths = paths;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ControlListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_toggle_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ControlListAdapter.ViewHolder vh = new ControlListAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ControlListAdapter.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.group_label.setText(paths.get(position));
        holder.group_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MRPC mrpc = EnlightApp.MRPC();
                mrpc.RPC(paths.get(position), isChecked);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return paths.size();
    }

}
