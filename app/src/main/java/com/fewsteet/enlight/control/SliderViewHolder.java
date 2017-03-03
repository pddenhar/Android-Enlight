package com.fewsteet.enlight.control;

import android.view.View;
import android.widget.SeekBar;

import net.vector57.android.mrpc.MRPCActivity;
import com.fewsteet.enlight.R;
import com.google.gson.JsonElement;

import net.vector57.mrpc.MRPC;

/**
 * Created by Alex Sherman on 1/26/2017.
 */


public class SliderViewHolder extends ControlListAdapter.ControlViewHolder {
    public SeekBar group_seek;
    public SliderViewHolder(View v) {
        super(v);
        group_seek = (SeekBar)v.findViewById(R.id.group_seek);
        group_seek.setMax(100);
    }

    private void attachListener() {
        group_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                MRPCActivity.mrpc(item.path, i / 100.0f, null, false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MRPC mrpc = MRPCActivity.mrpc();
                if(mrpc != null) {
                    mrpc.RPC(item.path, seekBar.getProgress() / 100.0f, null, true);
                }
            }
        });
    }

    @Override
    protected void setControlItemValue(JsonElement value) {
        super.setControlItemValue(value);
        if(value != null && value.getAsJsonPrimitive().isNumber()) {
            group_seek.setOnSeekBarChangeListener(null);
            group_seek.setProgress((int) (value.getAsFloat() * 100));
            attachListener();
        }
    }

    @Override
    public void bindItem(final ControlItem item) {
        super.bindItem(item);
        attachListener();
    }
}
