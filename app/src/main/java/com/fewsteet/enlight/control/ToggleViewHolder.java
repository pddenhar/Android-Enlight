package com.fewsteet.enlight.control;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import net.vector57.android.mrpc.MRPCActivity;
import com.fewsteet.enlight.R;
import com.google.gson.JsonElement;

import net.vector57.mrpc.MRPC;
import net.vector57.mrpc.Message;
import net.vector57.mrpc.Result;

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
    protected void bindItem(final ControlItem item) {
        super.bindItem(item);
        attachListener();
    }

    private void attachListener() {
        group_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            MRPCActivity.mrpc(item.path, isChecked);
            }
        });
    }

    @Override
    public void setControlItemValue(final JsonElement value) {
        super.setControlItemValue(value);
        group_toggle.setOnCheckedChangeListener(null);
        try {
            Boolean checked = Message.gson().fromJson(value, Boolean.class);
            group_toggle.setChecked(checked != null && checked);
        }
        catch(Exception e) {
            group_toggle.setChecked(false);
        }
        attachListener();
    }
}
