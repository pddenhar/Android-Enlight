package com.fewsteet.enlight;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fewsteet.enlight.browser.DeviceBrowserActivity;
import com.fewsteet.enlight.control.ControlItem;
import com.fewsteet.enlight.control.ControlListAdapter;
import com.fewsteet.enlight.util.ControlSwitchDAO;
import com.google.gson.JsonElement;

import net.vector57.android.mrpc.MRPCActivity;
import net.vector57.mrpc.MRPC;
import net.vector57.mrpc.Result;

import java.util.List;

public class MainActivity extends MRPCActivity {

    private static String TAG = "MainActivity";

    private CoordinatorLayout rootView;
    private RecyclerView recyclerView;
    private List<ControlItem> switches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = (CoordinatorLayout) findViewById(R.id.activity_main);

        switches = ControlSwitchDAO.getControls(this);

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

        new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, // Drag direction
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT  // Swipe direction
                ) {
                    @Override
                    public boolean onMove(
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            RecyclerView.ViewHolder target
                    ) {
                        int selected = recyclerView.getChildAdapterPosition(viewHolder.itemView);
                        int moveTo = recyclerView.getChildAdapterPosition(viewHolder.itemView);

                        switches.add(moveTo, switches.remove(selected));
                        ControlSwitchDAO.saveControls(MainActivity.this, switches);

                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                        final int index = recyclerView.getChildAdapterPosition(viewHolder.itemView);
                        final ControlItem removed = switches.get(index);

                        ControlSwitchDAO.removeControl(MainActivity.this, removed);
                        switches.remove(index);
                        recyclerView.getAdapter().notifyDataSetChanged();

                        Snackbar.make(rootView, "Deleted switch", Snackbar.LENGTH_LONG)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        switches.add(index, removed);
                                        ControlSwitchDAO.saveControls(MainActivity.this, switches);
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                        Snackbar.make(rootView, "Switch Restored", Snackbar.LENGTH_SHORT).show();
                                    }
                                })
                                .show();
                    }
        }).attachToRecyclerView(recyclerView);
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
