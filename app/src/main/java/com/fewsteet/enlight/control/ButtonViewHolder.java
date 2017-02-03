package com.fewsteet.enlight.control;

import android.view.View;
import android.widget.Button;

import net.vector57.android.mrpc.MRPCActivity;
import com.fewsteet.enlight.R;

import net.vector57.mrpc.MRPC;

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
    public void SetControlItem(final ControlItem item) {
        group_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MRPC mrpc = MRPCActivity.mrpc();
                if(mrpc != null) {
                    mrpc.RPC(item.path, null);
                }
            }
        });
    }
}
