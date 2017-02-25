package com.fewsteet.enlight.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.fewsteet.enlight.EnlightApp;
import com.fewsteet.enlight.MainActivity;
import com.fewsteet.enlight.R;
import com.fewsteet.enlight.control.ControlItem;
import com.fewsteet.enlight.control.ControlSwitchDAO;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex Sherman on 2/24/2017.
 */

public class ImportControlDialog extends DialogFragment {
    public static void create(FragmentManager fragmentManager, String importedControlString) {
        Bundle args = new Bundle();
        args.putString("importedControls", importedControlString);
        ImportControlDialog dialog = new ImportControlDialog();
        dialog.setArguments(args);
        dialog.show(fragmentManager, "herp");
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final ArrayList<ControlItem> importedControls = EnlightApp.Gson().fromJson(getArguments().getString("importedControls"),
                new TypeToken<ArrayList<ControlItem>>() { }.getType());
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.dialog_import_controls, null);
        builder.setView(view)
                .setNeutralButton("Replace", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        List<ControlItem> controls = ControlSwitchDAO.getControls(getActivity());
                        controls.clear();
                        controls.addAll(importedControls);
                        ControlSwitchDAO.saveControls(getActivity());
                        MainActivity.notifyDataSetChanged();
                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ControlSwitchDAO.getControls(getActivity()).addAll(importedControls);
                        ControlSwitchDAO.saveControls(getActivity());
                        MainActivity.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }
}
