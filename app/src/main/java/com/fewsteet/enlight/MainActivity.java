package com.fewsteet.enlight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.fewsteet.enlight.browser.DeviceBrowserActivity;
import com.fewsteet.enlight.util.Util;
import com.google.gson.JsonElement;

import net.vector57.mrpc.Message;
import net.vector57.mrpc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private ArrayList<ControlItem> switches;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switches = new ArrayList<>();
        switches.add(new ControlItem("Front", "/A.light"));
        switches.add(new ControlItem("Couch", "/B.light"));
        switches.add(new ControlItem("All Lights", "*.light"));

        mRecyclerView = (RecyclerView) findViewById(R.id.enlight_controls_list);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ControlListAdapter(switches);
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
        updateControls();
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


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    private void updateControls() {
        for (final ControlItem item: switches) {
            final String path = item.path;
            Log.d(TAG, "Sending message for " + path);
            EnlightApp.MRPC().RPC(path, null, new Result.Callback() {
                @Override
                public void onSuccess(JsonElement value) {
                    Log.d(TAG, "Got result for path " + path);
                    Boolean b = Message.gson().fromJson(value, Boolean.class);
                    if (b != null) {
                        item.state=b;
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public void launchSettings(MenuItem item) {
        return;
    }
    public void launchDevices(MenuItem item) {
        Intent i = new Intent(this, DeviceBrowserActivity.class);
        this.startActivity(i);
    }
}
