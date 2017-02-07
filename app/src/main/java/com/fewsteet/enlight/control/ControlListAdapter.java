package com.fewsteet.enlight.control;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fewsteet.enlight.R;

import java.util.List;

public class ControlListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ControlItem> control_items;

    public ControlListAdapter(List<ControlItem> control_items) {
        this.control_items = control_items;
    }

    @Override
    public int getItemCount() {
        return control_items.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (control_items.get(position).type) {
            case toggle:
                return R.layout.view_toggle_item;
            case slider:
                return R.layout.view_slider_item;
            case button:
                return R.layout.view_button_item;
        }
        return control_items.get(position).type.ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        switch (viewType) {
            case R.layout.view_toggle_item:
                return new ToggleViewHolder(v);
            case R.layout.view_slider_item:
                return new SliderViewHolder(v);
            case R.layout.view_button_item:
                return new ButtonViewHolder(v);
            default:
                throw new IllegalStateException("Unknown type in onCreateViewHolder");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((ControlViewHolder) holder).setControlItem(this, control_items.get(position));
    }

    static class ControlViewHolder extends RecyclerView.ViewHolder {

        TextView label;

        ControlViewHolder(View v) {
            super(v);
            label = (TextView)v.findViewById(R.id.group_label);
        }

        protected void setControlItem(ControlListAdapter adapter, ControlItem item) {
            label.setText(item.name);
            if(!item.stateQueried) {
                item.stateQueried = true;
                queryControlState(adapter, item);
            }
        }

        protected void queryControlState(ControlListAdapter adapter, ControlItem item) { };
    }

}