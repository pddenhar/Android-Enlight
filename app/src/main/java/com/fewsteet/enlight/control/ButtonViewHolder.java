package com.fewsteet.enlight.control;

import android.view.View;
import android.widget.Button;

import net.vector57.android.mrpc.MRPCActivity;
import com.fewsteet.enlight.R;
import com.fewsteet.enlight.control.drawable.EButton;

/**
 * Created by Alex Sherman on 1/26/2017.
 */


public class ButtonViewHolder extends ControlListAdapter.ControlViewHolder {
    public EButton group_button;
    public ButtonViewHolder(View v) {
        super(v);
        group_button = (EButton)v.findViewById(R.id.group_toggle);
    }
    @Override
    public void bindItem(final ControlItem item) {
        super.bindItem(item);
        if(item.argument != null) {
            String argString = item.argument.getAsString();
            if(argString != null) {
                argString = argString.replace('_', ' ');
                group_button.setText(argString);
            }
        }
        group_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MRPCActivity.mrpc(item.path, item.argument);
            }
        });
    }

    // Buttons don't have state
    @Override
    public void queryControlState() { }
}
