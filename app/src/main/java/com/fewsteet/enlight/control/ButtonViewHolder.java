package com.fewsteet.enlight.control;

import android.view.View;
import android.widget.Button;

import net.vector57.android.mrpc.MRPCActivity;
import com.fewsteet.enlight.R;

/**
 * Created by Alex Sherman on 1/26/2017.
 */


public class ButtonViewHolder extends ControlListAdapter.ControlViewHolder {
    public Button group_button;
    public ButtonViewHolder(View v) {
        super(v);
        group_button = (Button)v.findViewById(R.id.group_toggle);
    }
    @Override
    public void bindItem(final ControlItem item) {
        super.bindItem(item);
        group_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MRPCActivity.mrpc(item.path, null);
            }
        });
    }

    // Buttons don't have state
    @Override
    public void queryControlState() { }
}
