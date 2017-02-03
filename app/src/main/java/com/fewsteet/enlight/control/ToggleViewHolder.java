package com.fewsteet.enlight.control;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import net.vector57.android.mrpc.MRPCActivity;
import com.fewsteet.enlight.R;

import net.vector57.mrpc.MRPC;
import net.vector57.mrpc.Message;

/**
 * Created by Alex Sherman on 1/26/2017.
 */


public class ToggleViewHolder extends ControlListAdapter.ControlViewHolder {
    // each data item is just a string in this case
    public ToggleButton group_toggle;
    public ToggleViewHolder(View v) {
        super(v);
        group_toggle = (ToggleButton)v.findViewById(R.id.group_toggle);
    }
    @Override
    public void SetControlItem(final ControlItem item) {
        super.SetControlItem(item);
        group_toggle.setOnCheckedChangeListener(null);
        Boolean checked = Message.gson().fromJson(item.state, Boolean.class);

        if(checked != null)
            group_toggle.setChecked(checked);

        group_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MRPC mrpc = MRPCActivity.mrpc();
                if(mrpc != null) {
                    mrpc.RPC(item.path, isChecked);
                }
            }
        });
    }
}
