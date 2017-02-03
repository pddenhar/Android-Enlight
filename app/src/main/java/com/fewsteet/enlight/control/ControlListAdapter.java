package com.fewsteet.enlight.control;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fewsteet.enlight.R;

import java.util.ArrayList;

/**
 * Created by peter on 12/8/16.
 */

public class ControlListAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private ArrayList<ControlItem> control_items;

    public static class ControlViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView group_label;
        public ControlViewHolder(View v) {
            super(v);
            group_label = (TextView)v.findViewById(R.id.group_label);
        }
        public void SetControlItem(ControlItem item) { }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ControlListAdapter(ArrayList<ControlItem> control_items) {
        this.control_items = control_items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh = null;
        View v;
        switch (ControlItem.ControlType.values()[viewType]) {
            case toggle:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_toggle_item, parent, false);
                vh = new ToggleViewHolder(v);
                break;
            case slider:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_slider_item, parent, false);
                vh = new SliderViewHolder(v);
                break;
            case button:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_button_item, parent, false);
                vh = new ButtonViewHolder(v);
                break;
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ControlViewHolder cv_holder = (ControlViewHolder)holder;
        cv_holder.group_label.setText(control_items.get(position).name);
        cv_holder.SetControlItem(control_items.get(position));

    }

    @Override
    public int getItemViewType(int position) {
        return control_items.get(position).type.ordinal();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return control_items.size();
    }

}
