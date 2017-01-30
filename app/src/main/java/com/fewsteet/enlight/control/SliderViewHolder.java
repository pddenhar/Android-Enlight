package com.fewsteet.enlight.control;

import android.view.View;
import android.widget.SeekBar;

import com.fewsteet.enlight.MRPCActivity;
import com.fewsteet.enlight.R;

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

    @Override
    public void SetControlItem(final ControlItem item) {
        super.SetControlItem(item);
        group_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                MRPC mrpc = MRPCActivity.mrpc();
                if(mrpc != null) {
                    mrpc.RPC(item.path, i / 100.0f, null, false);
                }
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
}
