package com.fewsteet.enlight;

import android.content.Context;
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
import com.fewsteet.enlight.control.ControlSwitchDAO;
import com.google.gson.JsonElement;

import net.vector57.android.mrpc.MRPCActivity;
import net.vector57.mrpc.MRPC;
import net.vector57.mrpc.Result;

import java.util.List;

public class MainActivity extends MRPCActivity {

    private static String TAG = "MainActivity";

    private CoordinatorLayout rootView;
    private RecyclerView recyclerView;
    private ItemTouchHelper reorderHelper;
    private MenuItem menuItemConfirm;
    private MenuItem menuItemAdd;
    private MenuItem menuItemRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createReorderHelper();
        setContentView(R.layout.activity_main);

        rootView = (CoordinatorLayout) findViewById(R.id.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.enlight_controls_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        recyclerView.setAdapter(new ControlListAdapter(ControlSwitchDAO.getControls(this)));
    }

    private void createReorderHelper() {
        final Context ctx = this;
        reorderHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN, // Drag direction
                    ItemTouchHelper.RIGHT |ItemTouchHelper.RIGHT  // Swipe direction
            ) {

                @Override
                public boolean onMove(
                        RecyclerView recyclerView,
                        RecyclerView.ViewHolder viewHolder,
                        RecyclerView.ViewHolder target
                ) {
                    int selected = viewHolder.getAdapterPosition();
                    int destination = target.getAdapterPosition();
                    ControlSwitchDAO.addControl(ctx, destination, ControlSwitchDAO.removeControl(ctx, selected));
                    recyclerView.getAdapter().notifyItemMoved(selected, destination);
                    return true;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                    final int index = recyclerView.getChildAdapterPosition(viewHolder.itemView);
                    final ControlItem removed = ControlSwitchDAO.removeControl(ctx, index);

                    recyclerView.getAdapter().notifyDataSetChanged();

                    Snackbar.make(rootView, "Deleted switch", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ControlSwitchDAO.addControl(ctx, index, removed);
                                    recyclerView.getAdapter().notifyDataSetChanged();
                                    Snackbar.make(rootView, "Switch Restored", Snackbar.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ControlSwitchDAO.saveControls(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        menuItemConfirm = menu.findItem(R.id.confirm);
        menuItemAdd = menu.findItem(R.id.addcontrol);
        menuItemRefresh = menu.findItem(R.id.refresh);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                for(int i = 0; i < recyclerView.getChildCount(); i++) {
                    ((ControlListAdapter.ControlViewHolder)recyclerView.findViewHolderForLayoutPosition(i)).queryControlState();
                }
                return true;
            case R.id.confirm:
                menuItemConfirm.setVisible(false);
                //menuItemAdd.setVisible(true);
                menuItemRefresh.setVisible(true);
                reorderHelper.attachToRecyclerView(null);
                return true;
            case R.id.devices:
                startActivity(new Intent(this, DeviceBrowserActivity.class));
                return true;
            case R.id.reorder:
                menuItemConfirm.setVisible(true);
                menuItemAdd.setVisible(false);
                menuItemRefresh.setVisible(false);
                reorderHelper.attachToRecyclerView(recyclerView);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
