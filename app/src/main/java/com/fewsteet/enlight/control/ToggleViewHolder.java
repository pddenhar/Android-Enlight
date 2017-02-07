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
    public void setControlItem(ControlListAdapter adapter, final ControlItem item) {
        super.setControlItem(adapter, item);
        group_toggle.setOnCheckedChangeListener(null);
        try {
            Boolean checked = Message.gson().fromJson(item.state, Boolean.class);
            group_toggle.setChecked(checked != null && checked);
        }
        catch(Exception e) {
            group_toggle.setChecked(false);
        }
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

    @Override
    public void queryControlState(final ControlListAdapter adapter, final ControlItem item) {
        final String path = item.path;
        MRPCActivity.mrpc(path, null, new Result.Callback() {
            @Override
            public void onSuccess(JsonElement value) {
                item.state = value;
                item.stateQueried = true;
                setControlItem(adapter, item);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
