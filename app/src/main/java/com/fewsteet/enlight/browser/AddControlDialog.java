package com.fewsteet.enlight.browser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.fewsteet.enlight.ControlItem;
import com.fewsteet.enlight.R;

import java.util.ArrayList;

/**
 * Created by Alex Sherman on 12/20/2016.
 */

public class AddControlDialog extends DialogFragment {
    Spinner functionList;
    Spinner controlType;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final DeviceBrowserActivity act = (DeviceBrowserActivity)getActivity();
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final String name = getArguments().getString("name");
        ArrayList<String> services = getArguments().getStringArrayList("services");
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.dialog_add_control, null);
        builder.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    act.addControl(name, "/" + name + "." + (String)functionList.getSelectedItem(),
                            ControlItem.ControlType.values()[(int)controlType.getSelectedItemId()]);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        functionList = (Spinner) view.findViewById(R.id.service_spinner);
        controlType = (Spinner) view.findViewById(R.id.type_spinner);

        ArrayAdapter<String> functionAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, services);
        functionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        functionList.setAdapter(functionAdapter);


        ArrayAdapter<String> controlTypeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, ControlItem.ControlType.names());
        controlTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        controlType.setAdapter(controlTypeAdapter);
        // Create the AlertDialog object and return it
        return builder.create();
    }
}