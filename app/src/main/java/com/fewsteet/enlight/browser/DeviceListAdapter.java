package com.fewsteet.enlight.browser;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fewsteet.enlight.R;
import com.fewsteet.enlight.debug.DebugActivity;
import com.fewsteet.enlight.util.MRPCDeviceInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by peter on 11/13/16.
 */

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<MRPCDeviceInfo> mDataset;
    private DeviceBrowserActivity browserActivity;
    private String TAG = "DeviceListAdapter";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mUUIDTV;
        public ListView mGroupsListView;
        public Button mOptionsButton;
        public ViewHolder(View v) {
            super(v);
            mUUIDTV = (TextView)v.findViewById(R.id.deviceUUID);
            mOptionsButton = (Button)v.findViewById(R.id.device_options);
            mGroupsListView = (ListView)v.findViewById(R.id.deviceGroups);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DeviceListAdapter(DeviceBrowserActivity browserActivity) {
        this.browserActivity = browserActivity;
    }

    public void setData(Collection<MRPCDeviceInfo> data) {
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

        final MRPCDeviceInfo item = mDataset.get(position);

        holder.mUUIDTV.setText(item.uuid);
        holder.mOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browserActivity.startActivity(DebugActivity.createIntent(browserActivity, item.uuid));
            }
        });
        holder.mGroupsListView.setAdapter(new GroupListAdapter(browserActivity, item.aliases));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}