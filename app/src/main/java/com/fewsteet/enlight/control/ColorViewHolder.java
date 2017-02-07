package com.fewsteet.enlight.control;

import android.graphics.Color;
import android.view.View;

import com.fewsteet.enlight.R;
import com.rtugeek.android.colorseekbar.ColorSeekBar;

import net.vector57.android.mrpc.MRPCActivity;

/**
 * Created by Alex Sherman on 2/7/2017.
 */

public class ColorViewHolder extends ControlListAdapter.ControlViewHolder {
    ColorSeekBar seekBar;
    ColorViewHolder(View v) {
        super(v);
        seekBar = (ColorSeekBar)v.findViewById(R.id.colorSlider);
    }

    @Override
    protected void queryControlState(ControlListAdapter adapter, ControlItem item) {
        super.queryControlState(adapter, item);
    }

    @Override
    protected void setControlItem(ControlListAdapter adapter, final ControlItem item) {
        super.setControlItem(adapter, item);
        seekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i, int i1, int color) {
                float r = ((color >> 16) & 0xFF) / 255.0f;
                float g = ((color >> 8) & 0xFF) / 255.0f;
                float b = (color & 0xFF) / 255.0f;
                MRPCActivity.mrpc(item.path, new float[] { r, g, b});
            }
        });
    }
}
