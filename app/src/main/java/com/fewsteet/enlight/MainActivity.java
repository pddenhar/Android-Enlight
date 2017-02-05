package com.fewsteet.enlight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fewsteet.enlight.browser.DeviceBrowserActivity;
import com.fewsteet.enlight.control.ControlItem;
import com.fewsteet.enlight.control.ControlListAdapter;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.vector57.android.mrpc.MRPCActivity;
import net.vector57.mrpc.MRPC;
import net.vector57.mrpc.Result;

import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends MRPCActivity {

    private static String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private List<ControlItem> switches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switches = readSwitches();

        recyclerView = (RecyclerView) findViewById(R.id.enlight_controls_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        recyclerView.setAdapter(
                new ControlListAdapter(switches, new ControlListAdapter.Listener() {
                    @Override
                    public void pressed(ControlItem item) {
                        MRPC mrpc = MRPCActivity.mrpc();
                        if(mrpc != null) {
                            mrpc.RPC(item.path, null);
                        }
                    }

                    @Override
                    public void toggled(ControlItem item, boolean enable) {
                        MRPC mrpc = MRPCActivity.mrpc();
                        if(mrpc != null) {
                            mrpc.RPC(item.path, enable);
                        }
                    }

                    @Override
                    public void seek(ControlItem item, boolean complete, float value) {
                        MRPC mrpc = MRPCActivity.mrpc();
                        if(complete) {
                            if(mrpc != null) {
                                mrpc.RPC(item.path, value, null, true);
                            }
                        } else {
                            if(mrpc != null) {
                                mrpc.RPC(item.path, value, null, false);
                            }
                        }
                    }
                })
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateControls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.devices:
                startActivity(new Intent(this, DeviceBrowserActivity.class));
                return true;
            case R.id.clear:
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .remove(getString(R.string.layout_preference_key))
                        .apply();
                switches.clear();
                recyclerView.getAdapter().notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private List<ControlItem> readSwitches() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String layoutJson = sharedPref.getString(getString(R.string.layout_preference_key), getString(R.string.default_layout));
        Log.d(TAG, layoutJson);
        Type t = new TypeToken<List<ControlItem>>() {}.getType();
        return EnlightApp.Gson().fromJson(layoutJson, t);
    }

    private void updateControls() {
        for (ControlItem item : switches) {
            switch(item.type) {
                case toggle:
                    updateToggle(item);
                    break;
            }
        }
    }

    private void updateToggle(final ControlItem item) {
        final String path = item.path;
        Log.d(TAG, "Sending message for " + path);
        mrpc(path, null, new Result.Callback() {
            @Override
            public void onSuccess(JsonElement value) {
                Log.d(TAG, "Got result for path " + path);
                item.state = value;
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

}
