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
    private final HashMap<String, MRPCResponses.InfoResponse> devices = new HashMap<String, MRPCResponses.InfoResponse>();
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
        mAdapter.setData(MRPCResponses.nodeMap.values());
    }

    public void findDevices(View v) {
        MRPCResponses.nodeMap.clear();
        MRPCResponses.queryServiceMap(this, new MRPCResponses.InfoResponseHandler() {
            @Override
            public void handle(MRPCResponses.InfoResponse info) {
                mAdapter.setData(MRPCResponses.nodeMap.values());
            }
        });
    }
}
