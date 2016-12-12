package com.fewsteet.enlight.browser;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.fewsteet.enlight.ControlItem;
import com.fewsteet.enlight.EnlightApp;
import com.fewsteet.enlight.R;
import com.fewsteet.enlight.util.MRPCDeviceInfo;
import com.fewsteet.enlight.util.Util;
import com.google.gson.reflect.TypeToken;

import net.vector57.mrpc.Message;
import net.vector57.mrpc.Result;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviceBrowserActivity extends AppCompatActivity {
    final static String TAG = "DeviceBrowserActivity";
    private final HashMap<String, MRPCDeviceInfo> devices = new HashMap<String, MRPCDeviceInfo>();
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
        mRecyclerView.setHasFixedSize(true);

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
        Log.d(TAG, "Starting MRPC");
        try {
            EnlightApp.MRPC().start(Util.getBroadcastAddress(this));
        } catch (IOException e) {
            Log.d(TAG, "MRPC start failed.");
            e.printStackTrace();
        }
        findDevices(null);
    }
    @Override
    public void onPause() {
        super.onPause();
        EnlightApp.storeApplicationState();

        try {
            Log.d(TAG, "Closing MRPC");
            EnlightApp.MRPC().close();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    public void findDevices(View v) {
        devices.clear();
        mAdapter.setData(devices.values());
        EnlightApp.MRPC().RPC("*.alias", null, new Result.Callback() {
            @Override
            public void onResult(Message.Response response) {
                if(response.error == null) {
                    String uuid = response.src;
                    ArrayList<String> names = Message.gson().fromJson(response.result, new TypeToken<ArrayList<String>>(){}.getType());
                    if(!devices.containsKey(uuid)) {
                        devices.put(uuid, new MRPCDeviceInfo(uuid));
                    }
                    devices.get(uuid).aliases = names;
                    mAdapter.setData(devices.values());
                }
            }
        });
    }
    public void addSwitch(String name, String path) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String layoutJson = sharedPref.getString(getString(R.string.layout_preference_key), getString(R.string.default_layout));

        Type t = new TypeToken<List<ControlItem>>() {}.getType();
        List<ControlItem> layout = EnlightApp.Gson().fromJson(layoutJson, t);
        layout.add(new ControlItem(name, path));
        String json = EnlightApp.Gson().toJson(layout);
        Log.d(TAG, json);
        sharedPref.edit().putString(getString(R.string.layout_preference_key),json).apply();
    }
}
