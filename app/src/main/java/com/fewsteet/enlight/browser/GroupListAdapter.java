package com.fewsteet.enlight.browser;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fewsteet.enlight.EnlightApp;
import com.fewsteet.enlight.MainActivity;
import com.fewsteet.enlight.R;
import com.fewsteet.enlight.util.MRPCResponses;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.vector57.mrpc.Result;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by peter on 12/9/16.
 */

public class GroupListAdapter extends ArrayAdapter<String> {
    private final String TAG = "GroupListAdapter";
    private static DeviceBrowserActivity act;

    public GroupListAdapter(DeviceBrowserActivity context, ArrayList<String> users) {
        super(context, 0, users);
        this.act = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final String group = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_group_item, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.group_name);
        Button addHomeButton = (Button) convertView.findViewById(R.id.add_to_home_button);
        addHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.mrpc("/" + group + ".configure_service", null, new Result.Callback() {
                    @Override
                    public void onSuccess(JsonElement value) {
                        super.onSuccess(value);
                        HashMap<String, MRPCResponses.ServiceInfo> services =
                                EnlightApp.Gson().fromJson(value, new TypeToken<HashMap<String, MRPCResponses.ServiceInfo>>(){}.getType());
                        Bundle args = new Bundle();
                        args.putStringArrayList("services", new ArrayList<String>(services.keySet()));
                        args.putString("name", group);
                        AddControlDialog dialog = new AddControlDialog();
                        dialog.setArguments(args);
                        dialog.show(act.getFragmentManager(), "herp");
                    }
                });
                Log.d(TAG, "Add toggle clicked");
                //act.addControl(group, "/" + group +".light");

            }
        });
        // Populate the data into the template view using the data object
        tvName.setText(group);
        // Return the completed view to render on screen
        return convertView;
    }
}
