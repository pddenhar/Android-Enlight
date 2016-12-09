package com.fewsteet.enlight.browser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fewsteet.enlight.R;

import java.util.ArrayList;

/**
 * Created by peter on 12/9/16.
 */

public class GroupListAdapter extends ArrayAdapter<String> {
    private final String TAG = "GroupListAdapter";

    public GroupListAdapter(Context context, ArrayList<String> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String group = getItem(position);
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
                Log.d(TAG, "Add toggle clicked");
            }
        });
        // Populate the data into the template view using the data object
        tvName.setText(group);
        // Return the completed view to render on screen
        return convertView;
    }
}
