package com.fewsteet.enlight;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * Created by peter on 11/13/16.
 */

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private Map<String, MRPCDeviceInfo> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mUUIDTV;
        public ListView mGroupsListView;
        public ViewHolder(View v) {
            super(v);
            mUUIDTV = (TextView)v.findViewById(R.id.deviceUUID);
            mGroupsListView = (ListView)v.findViewById(R.id.deviceGroups);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DeviceListAdapter(Map<String, MRPCDeviceInfo> myDataset) {
        mDataset = myDataset;
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
        int i = 0;
        for (MRPCDeviceInfo item:mDataset.values()) {
            if(i==position) {
                holder.mUUIDTV.setText(item.uuid);
                String[] from = { "name", "purpose" };
                int[] to = { android.R.id.text1, android.R.id.text2 };
                holder.mGroupsListView.setAdapter(new SimpleAdapter(this, item.aliases,
                        R.layout.view_group_item, from, to););
                break;
            }
            i++;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}