package com.fewsteet.enlight;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import net.vector57.mrpc.MRPC;

import java.util.ArrayList;

/**
 * Created by peter on 12/8/16.
 */

public class ControlListAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private ArrayList<ControlItem> control_items;
    public static class ToggleViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        public TextView group_label;
        public ToggleButton group_toggle;
        public ToggleViewHolder(View v) {
            super(v);
            group_label = (TextView)v.findViewById(R.id.group_label);
            group_toggle = (ToggleButton)v.findViewById(R.id.group_toggle);
            view=v;
        }
    }
    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        public TextView group_label;
        public Button group_button;
        public ButtonViewHolder(View v) {
            super(v);
            group_label = (TextView)v.findViewById(R.id.group_label);
            group_button = (Button)v.findViewById(R.id.group_toggle);
            view=v;
        }
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
        switch (ControlItem.ControlType.values()[holder.getItemViewType()]) {
            case toggle:
                ToggleViewHolder toggle_holder = (ToggleViewHolder)holder;
                toggle_holder.group_label.setText(control_items.get(position).name);
                toggle_holder.group_toggle.setOnCheckedChangeListener(null);
                toggle_holder.group_toggle.setChecked(control_items.get(position).state);
                toggle_holder.group_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        MRPC mrpc = MRPCActivity.mrpc();
                        if(mrpc != null) {
                            mrpc.RPC(control_items.get(position).path, isChecked);
                        }
                    }
                });
                break;
            case button:
                ButtonViewHolder button_holder = (ButtonViewHolder)holder;
                button_holder.group_label.setText(control_items.get(position).name);
                button_holder.group_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MRPC mrpc = MRPCActivity.mrpc();
                        if(mrpc != null) {
                            mrpc.RPC(control_items.get(position).path, null);
                        }
                    }
                });
                break;
        }

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
