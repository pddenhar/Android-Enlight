package com.fewsteet.enlight.control;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.fewsteet.enlight.R;

import net.vector57.mrpc.Message;

import java.util.List;

public class ControlListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface Listener {
        void pressed(ControlItem item); //TODO is this necessary?
        void toggled(ControlItem item, boolean enable);
        void seek(ControlItem item, boolean complete, float value);
    }

    private final List<ControlItem> control_items;
    private final Listener listener;

    public ControlListAdapter(List<ControlItem> control_items, Listener listener) {
        this.control_items = control_items;
        this.listener = listener;
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
                return R.layout.view_slider_item;
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
        ((ControlViewHolder) holder).update(control_items.get(position), listener);
    }

    static abstract class ControlViewHolder extends RecyclerView.ViewHolder {

        TextView label;

        ControlViewHolder(View v) {
            super(v);
            label = (TextView)v.findViewById(R.id.group_label);
        }

        void update(ControlItem item, Listener listener) {
            label.setText(item.name);
            updateSelf(item, listener);
        }

        abstract void updateSelf(ControlItem controlItem, Listener listener);

    }

    static class ToggleViewHolder extends ControlListAdapter.ControlViewHolder {

        private ToggleButton toggle;

        ToggleViewHolder(View v) {
            super(v);
            toggle = (ToggleButton) v.findViewById(R.id.group_toggle);
        }

        @Override
        void updateSelf(final ControlItem item, final Listener listener) {
            Boolean checked = Message.gson().fromJson(item.state, Boolean.class);
            toggle.setChecked(checked != null && checked);

            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    listener.toggled(item, isChecked);
                }
            });
        }

    }

    class ButtonViewHolder extends ControlListAdapter.ControlViewHolder {
/* Carson has made this good with this super useful and necessary comment, as Marshall askedher to do. What a technical genius with much schooling and intelligence wow */
        private Button button;
        ButtonViewHolder(View v) {
            super(v);
            button = (Button)v.findViewById(R.id.group_toggle);
        }

        @Override
        void updateSelf(final ControlItem item, final Listener listener) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.pressed(item);
                }
            });
        }

    }

    class SliderViewHolder extends ControlListAdapter.ControlViewHolder {

        SeekBar seek;

        SliderViewHolder(View v) {
            super(v);
            seek = (SeekBar)v.findViewById(R.id.group_seek);
            seek.setMax(100);
        }

        @Override
        void updateSelf(final ControlItem controlItem, final Listener listener) {
            seek.setProgress(0); //TODO get progress from device
            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    listener.seek(controlItem, false, progress / 100f);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    listener.seek(controlItem, false, seekBar.getProgress() / 100f);
                }
            });
        }

    }

}