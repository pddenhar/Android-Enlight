package com.fewsteet.enlight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.vector57.mrpc.MRPC;
import net.vector57.mrpc.Message;
import net.vector57.mrpc.Result;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.fewsteet.enlight.R.id.devices;

public class DeviceBrowserActivity extends AppCompatActivity {
    final static String TAG = "DeviceBrowserActivity";
    private MRPC mrpc;
    private final HashMap<String, MRPCDeviceInfo> devices = new HashMap<String, MRPCDeviceInfo>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TreeSet<String> groups;
    private TreeSet<String> all_names;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_browser);
        groups = new TreeSet<>();
        all_names = new TreeSet<>();

        mrpc = MRPCSingleton.getInstance(getApplicationContext()).getMRPC();
        findDevices();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new DeviceListAdapter(devices);
        mRecyclerView.setAdapter(mAdapter);
    }
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    public void launchSettings(MenuItem v) {
        return;
    }
    public void launchDevices(MenuItem v) {
        return;
    }

    public void findDevices() {
        mrpc.RPC("*.alias", null, new Result.Callback() {
            @Override
            public void onResult(Message.Response response) {
                if(response.error == null) {
                    List<String> names = Message.gson().fromJson(response.result, new TypeToken<List<String>>(){}.getType());
                    String uuid = response.src;
                    if(!devices.containsKey(uuid)) {
                        devices.put(uuid, new MRPCDeviceInfo(uuid));
                        Log.d(TAG, uuid);
                    }
                    if(names != null) {
                        for (String name : names) {
                            if(all_names.contains(name)) {
                                all_names.remove(name);
                                groups.add(name);
                            } else {
                                all_names.add(name);
                            }
                            devices.get(uuid).aliases.add(name);
                            mAdapter.notifyDataSetChanged();
                            Log.d(TAG, name);
                        }
                    }
                }
            }
        });
    }
}
