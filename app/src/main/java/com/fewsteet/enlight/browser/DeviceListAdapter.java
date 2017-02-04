package com.fewsteet.enlight.browser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fewsteet.enlight.EnlightApp;
import com.fewsteet.enlight.R;
import com.fewsteet.enlight.debug.DebugActivity;
import com.fewsteet.enlight.util.MRPCResponses;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.vector57.mrpc.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by peter on 11/13/16.
 */

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<MRPCResponses.DeviceInfo> mDataset;
    private DeviceBrowserActivity browserActivity;
    private String TAG = "DeviceListAdapter";
    private List<String> blackListedServices;

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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(browserActivity);
        String blackListJson = sharedPref.getString("service_blacklist", browserActivity.getString(R.string.default_service_blacklist));
        blackListedServices = EnlightApp.Gson().fromJson(blackListJson, new TypeToken<ArrayList<String>>() {}.getType());
    }

    public void setData(Collection<MRPCResponses.DeviceInfo> data) {
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

        final MRPCResponses.DeviceInfo item = mDataset.get(position);

        holder.mUUIDTV.setText(item.uuid);
        holder.mOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browserActivity.startActivity(DebugActivity.createIntent(browserActivity, item.uuid));
            }
        });

        holder.mDeviceName.setText(item.aliases.get(0));

        holder.mAddToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browserActivity.mrpc(item.uuid + ".configure_service", null, new Result.Callback() {
                    boolean opened = false;
                    @Override
                    public void onSuccess(JsonElement value) {
                        super.onSuccess(value);

                        // Don't open the dialog more than once on multiple responses
                        if(opened)
                            return;
                        opened = true;

                        HashMap<String, MRPCResponses.ServiceInfo> services =
                                EnlightApp.Gson().fromJson(value, new TypeToken<HashMap<String, MRPCResponses.ServiceInfo>>(){}.getType());

                        // Remove services we don't care about
                        for (String blackListedService: blackListedServices) {
                            if(services.containsKey(blackListedService)) {
                                services.remove(blackListedService);
                            }
                        }

                        Bundle args = new Bundle();
                        args.putStringArrayList("services", new ArrayList<String>(services.keySet()));
                        args.putStringArrayList("names", item.aliases);
                        AddControlDialog dialog = new AddControlDialog();
                        dialog.setArguments(args);
                        dialog.show(browserActivity.getFragmentManager(), "herp");
                    }
                });
                Log.d(TAG, "Add toggle clicked");
                //act.addControl(group, "/" + group +".light");

            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}