package com.fewsteet.enlight.control;

import android.view.View;

import com.fewsteet.enlight.R;
import com.fewsteet.enlight.control.drawable.ColorSeekBar;

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
    protected void bindItem(final ControlItem item) {
        super.bindItem(item);
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
