package com.fewsteet.enlight.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.fewsteet.enlight.MainActivity;
import com.fewsteet.enlight.control.ControlItem;
import com.fewsteet.enlight.R;
import com.fewsteet.enlight.control.ControlSwitchDAO;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alex Sherman on 12/20/2016.
 */

public class AddControlDialog extends DialogFragment {
    Spinner functionList;
    Spinner controlType;
    Spinner nameList;
    LinearLayout argumentRow;
    EditText argumentText;
    public static void create(HashMap<String, ArrayList<String>> serviceMap, FragmentManager fragmentManager) {
        create(serviceMap, fragmentManager, null);
    }
    public static void create(HashMap<String, ArrayList<String>> serviceMap, FragmentManager fragmentManager, String selectedName) {
        Bundle args = new Bundle();
        args.putSerializable("serviceMap", serviceMap);
        args.putString("selectedName", selectedName);
        AddControlDialog dialog = new AddControlDialog();
        dialog.setArguments(args);
        dialog.show(fragmentManager, "herp");
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context act = getActivity();
        LayoutInflater inflater = getActivity().getLayoutInflater();

        HashMap<String, ArrayList<String>> _serviceMap = (HashMap<String, ArrayList<String>>)getArguments().getSerializable("serviceMap");
        if(_serviceMap == null)
            _serviceMap = new HashMap<>();
        final HashMap<String, ArrayList<String>> serviceMap = _serviceMap;
        final String selectedName = getArguments().getString("selectedName");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.dialog_add_control, null);
        nameList = (Spinner) view.findViewById(R.id.path_name_spinner);
        functionList = (Spinner) view.findViewById(R.id.service_spinner);
        controlType = (Spinner) view.findViewById(R.id.type_spinner);
        argumentRow = (LinearLayout) view.findViewById(R.id.argument_row);
        argumentText = (EditText) view.findViewById(R.id.argument_text);
        builder.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String name = (String)nameList.getSelectedItem();
                            JsonElement argument = new JsonParser().parse(argumentText.getText().toString());
                        ControlItem control = new ControlItem(name, "/" + name + "." + (String)functionList.getSelectedItem(),
                                argument, ControlItem.ControlType.values()[(int)controlType.getSelectedItemId()]);
                        ControlSwitchDAO.addControl(act, control);
                        MainActivity.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        // Service List
        final ArrayAdapter<String> functionAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
        functionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        functionList.setAdapter(functionAdapter);

        // Name List
        final ArrayAdapter<String> pathNameAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, new ArrayList<>(serviceMap.keySet()));
        if(selectedName != null && pathNameAdapter.getPosition(selectedName) > 0) {
            pathNameAdapter.remove(selectedName);
            pathNameAdapter.insert(selectedName, 0);
        }
        pathNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                functionAdapter.clear();
                functionAdapter.addAll(serviceMap.get(pathNameAdapter.getItem(i)));
                functionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        nameList.setAdapter(pathNameAdapter);

        // Control Type List
        ArrayAdapter<String> controlTypeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, ControlItem.ControlType.names());
        controlTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        controlType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ControlItem.ControlType type = ControlItem.ControlType.values()[i];
                if(type == ControlItem.ControlType.button) {
                    argumentRow.setVisibility(View.VISIBLE);
                }
                else {
                    argumentRow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        controlType.setAdapter(controlTypeAdapter);

        return builder.create();
    }
}
