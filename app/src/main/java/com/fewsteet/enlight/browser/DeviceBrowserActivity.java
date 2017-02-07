package com.fewsteet.enlight.browser;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.vector57.android.mrpc.MRPCActivity;
import com.fewsteet.enlight.R;
import com.fewsteet.enlight.util.MRPCResponses;
import com.google.gson.reflect.TypeToken;

import net.vector57.mrpc.Message;
import net.vector57.mrpc.Result;

import java.util.ArrayList;
import java.util.HashMap;

public class DeviceBrowserActivity extends MRPCActivity {
    final static String TAG = "DeviceBrowserActivity";
    private final HashMap<String, MRPCResponses.DeviceInfo> devices = new HashMap<String, MRPCResponses.DeviceInfo>();
    private ArrayList<String> blackListedServices = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private DeviceListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_browser);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new DeviceListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    public void onResume() {
        super.onResume();
        findDevices(null);
    }

    public void findDevices(View v) {
        devices.clear();
        mAdapter.setData(devices.values());
        mrpc().RPC("*.alias", null, new Result.Callback() {
            @Override
            public void onResult(Message.Response response) {
                if(response.error == null) {
                    String uuid = response.src;
                    ArrayList<String> names = Message.gson().fromJson(response.result, new TypeToken<ArrayList<String>>(){}.getType());
                    if(!devices.containsKey(uuid)) {
                        devices.put(uuid, new MRPCResponses.DeviceInfo(uuid));
                    }
                    devices.get(uuid).aliases = names;
                    mAdapter.setData(devices.values());
                }
            }
        });
    }
}
