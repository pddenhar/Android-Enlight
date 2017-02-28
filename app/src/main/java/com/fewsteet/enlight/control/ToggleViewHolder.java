package com.fewsteet.enlight.control;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import net.vector57.android.mrpc.MRPCActivity;
import com.fewsteet.enlight.R;
import com.fewsteet.enlight.control.drawable.EToggleButton;
import com.google.gson.JsonElement;

import net.vector57.mrpc.MRPC;
import net.vector57.mrpc.Message;
import net.vector57.mrpc.Result;

/**
 * Created by Alex Sherman on 1/26/2017.
 */


public class ToggleViewHolder extends ControlListAdapter.ControlViewHolder {
    // each data item is just a string in this case
    public EToggleButton group_toggle;
    public ToggleViewHolder(View v) {
        super(v);
        group_toggle = (EToggleButton)v.findViewById(R.id.group_toggle);
    }

    @Override
    protected void bindItem(final ControlItem item) {
        super.bindItem(item);
        attachListener();
        group_toggle.setText(item.name);
    }

    private void attachListener() {
        group_toggle.setOnCheckedChangeListener(new EToggleButton.CheckChangeListener() {
            @Override
            public void onCheckedChanged(EToggleButton buttonView, boolean isChecked) {
                MRPCActivity.mrpc(item.path, isChecked);
            }
        });
    }

    @Override
    public void queryControlState() {
        this.setControlItemValue(false);
        super.queryControlState();
    }

    @Override
    public void setControlItemValue(final JsonElement value) {
        super.setControlItemValue(value);
        boolean bvalue = group_toggle.isChecked();
        try {
            Boolean checked = MRPC.gson().fromJson(value, Boolean.class);
            bvalue |= checked != null && checked;
        }
        catch(Exception e) { }
        setControlItemValue(bvalue);
    }

    private void setControlItemValue(boolean value) {
        group_toggle.setOnCheckedChangeListener(null);
        group_toggle.setChecked(value);
        attachListener();
    }
}
