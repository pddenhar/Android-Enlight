package com.fewsteet.enlight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.fewsteet.enlight.browser.DeviceBrowserActivity;
import com.fewsteet.enlight.control.ControlItem;
import com.fewsteet.enlight.control.ControlListAdapter;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.vector57.mrpc.Result;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MRPCActivity {
    private static String TAG = "MainActivity";
    private ArrayList<ControlItem> switches;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switches = new ArrayList<>();

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
        updateSwitchesFromPrefs();
        updateControls();
    }

    public void updateSwitchesFromPrefs() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String layoutJson = sharedPref.getString(getString(R.string.layout_preference_key), getString(R.string.default_layout));
        Log.d(TAG, layoutJson);
        Type t = new TypeToken<List<ControlItem>>() {}.getType();
        List<ControlItem> layout = EnlightApp.Gson().fromJson(layoutJson, t);
        switches.clear();
        switches.addAll(layout);
        mAdapter.notifyDataSetChanged();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    private void updateToggle(final ControlItem item) {
        final String path = item.path;
        Log.d(TAG, "Sending message for " + path);
        mrpc(path, null, new Result.Callback() {
            @Override
            public void onSuccess(JsonElement value) {
                Log.d(TAG, "Got result for path " + path);
                item.state = value;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateControls() {
        for (final ControlItem item: switches) {
            switch(item.type) {
                case toggle:
                    updateToggle(item);
                    break;
            }
        }
    }

    public void clearLayout(MenuItem item) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        sharedPref.edit().remove(getString(R.string.layout_preference_key)).commit();
        updateSwitchesFromPrefs();
    }

    public void launchDevices(MenuItem item) {
        Intent i = new Intent(this, DeviceBrowserActivity.class);
        this.startActivity(i);
    }

}
