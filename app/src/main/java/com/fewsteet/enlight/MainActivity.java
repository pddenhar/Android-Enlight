package com.fewsteet.enlight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.google.gson.JsonElement;

import net.vector57.mrpc.MRPC;
import net.vector57.mrpc.Message;
import net.vector57.mrpc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private ArrayList<String> switches;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Util.getBroadcastAddress(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        switches = new ArrayList<>();
        switches.add("/A.light");
        switches.add("/B.light");
        switches.add("*.light");

        mRecyclerView = (RecyclerView) findViewById(R.id.enlight_controls_list);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ControlListAdapter(switches, MRPCSingleton.getInstance(this));
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    public void onPause() {
        super.onPause();
        try {
            Log.d(TAG, "Closing MRPC");
            MRPCSingleton.getInstance(this).getMRPC().close();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Starting MRPC");
        MRPCSingleton.getInstance(this).getMRPC().start();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    public void launchSettings(MenuItem item) {
        return;
    }
    public void launchDevices(MenuItem item) {
        Intent i = new Intent(this, DeviceBrowserActivity.class);
        this.startActivity(i);
    }
}
