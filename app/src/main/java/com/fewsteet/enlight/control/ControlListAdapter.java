package com.fewsteet.enlight.control;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fewsteet.enlight.R;
import com.google.gson.JsonElement;

import net.vector57.android.mrpc.MRPCActivity;
import net.vector57.mrpc.Result;

import java.util.List;

public class ControlListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ControlItem> controlItems;

    public ControlListAdapter(List<ControlItem> control_items) {
        this.controlItems = control_items;
    }

    @Override
    public int getItemCount() {
        return controlItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (controlItems.get(position).type) {
            case toggle:
                return R.layout.view_toggle_item;
            case slider:
                return R.layout.view_slider_item;
            case button:
                return R.layout.view_button_item;
            case color:
                return R.layout.view_color_item;
        }
        return controlItems.get(position).type.ordinal();
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
            case R.layout.view_color_item:
                return new ColorViewHolder(v);
            default:
                throw new IllegalStateException("Unknown type in onCreateViewHolder");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((ControlViewHolder) holder).bindItem(controlItems.get(position));
        ((ControlViewHolder) holder).queryControlState();
    }

    public static class ControlViewHolder extends RecyclerView.ViewHolder {

        TextView label;
        ControlItem item;
        ControlViewHolder(View v) {
            super(v);
            label = (TextView)v.findViewById(R.id.group_label);
        }

        protected void bindItem(ControlItem item) {
            label.setText(item.name);
            this.item = item;
            setControlItemValue(item.state);
        }

        protected void setControlItemValue(JsonElement value) {
            item.state = value;
        }

        public void queryControlState() {
            final String path = item.path;
            MRPCActivity.mrpc(path, null, new Result.Callback() {
                @Override
                public void onSuccess(JsonElement value) {
                    setControlItemValue(value);
                }
            });
        };
    }

}