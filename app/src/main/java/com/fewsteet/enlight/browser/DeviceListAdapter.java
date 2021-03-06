package com.fewsteet.enlight.browser;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fewsteet.enlight.EnlightApp;
import com.fewsteet.enlight.R;
import com.fewsteet.enlight.debug.DebugActivity;
import com.fewsteet.enlight.dialog.AddControlDialog;
import com.fewsteet.enlight.util.MRPCResponses;
import com.fewsteet.enlight.util.Preferences;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.vector57.android.mrpc.MRPCActivity;
import net.vector57.mrpc.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by peter on 11/13/16.
 */

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<MRPCResponses.InfoResponse> mDataset;
    private DeviceBrowserActivity browserActivity;
    private String TAG = "DeviceListAdapter";


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mUUIDTV;
        public Button mOptionsButton;
        public TextView mDeviceName;
        public Button mAddToHome;
        public ViewHolder(View v) {
            super(v);
            mUUIDTV = (TextView)v.findViewById(R.id.deviceUUID);
            mOptionsButton = (Button)v.findViewById(R.id.device_options);
            mDeviceName = (TextView)v.findViewById(R.id.device_name);
            mAddToHome = (Button)v.findViewById(R.id.add_to_home_button);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DeviceListAdapter(DeviceBrowserActivity browserActivity) {
        this.browserActivity = browserActivity;
    }

    public void setData(Collection<MRPCResponses.InfoResponse> data) {
        mDataset = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DeviceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_device_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final MRPCResponses.InfoResponse item = mDataset.get(position);

        holder.mUUIDTV.setText("UUID: "+item.uuid);
        holder.mOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browserActivity.startActivity(DebugActivity.createIntent(browserActivity, item));
            }
        });
        if(item.aliases.size() > 0) {
            holder.mDeviceName.setText(item.aliases.get(0));
        } else {
            holder.mDeviceName.setText("*");
        }

        holder.mAddToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> aliases = new ArrayList<String>(item.aliases);
                aliases.add(item.uuid);
                HashMap<String, HashSet<String>> serviceMap = new HashMap<String, HashSet<String>>();
                HashSet<String> serviceList = new HashSet<String>(item.services);
                Preferences.filterBlackListedServices(browserActivity, serviceList);
                for(String name : aliases) {
                    serviceMap.put(name, serviceList);
                }
                if(item.aliases.size() > 0) {
                    AddControlDialog.create(serviceMap, browserActivity.getFragmentManager(), item.aliases.get(0));
                }
                else {
                    AddControlDialog.create(serviceMap, browserActivity.getFragmentManager());
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}