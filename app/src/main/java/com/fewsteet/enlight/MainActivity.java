package com.fewsteet.enlight;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fewsteet.enlight.browser.DeviceBrowserActivity;
import com.fewsteet.enlight.control.ControlItem;
import com.fewsteet.enlight.control.ControlListAdapter;
import com.fewsteet.enlight.control.ControlSwitchDAO;
import com.fewsteet.enlight.dialog.ImportControlDialog;
import com.fewsteet.enlight.util.MRPCResponses;
import com.fewsteet.enlight.util.Preferences;
import com.google.gson.JsonElement;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.vector57.android.mrpc.MRPCActivity;
import net.vector57.mrpc.Message;
import net.vector57.mrpc.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends MRPCActivity {

    private static String TAG = "MainActivity";

    private CoordinatorLayout rootView;
    private RecyclerView recyclerView;
    private ItemTouchHelper reorderHelper;
    private MenuItem menuItemConfirm;
    private MenuItem menuItemAdd;
    private MenuItem menuItemRefresh;
    private HashMap<String, HashSet<String>> serviceMap = new HashMap<>();
    private static MainActivity single;

    public static void notifyDataSetChanged() {
        if(single != null && single.recyclerView != null)
            single.recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        single = this;
        createReorderHelper();
        setContentView(R.layout.activity_main);

        rootView = (CoordinatorLayout) findViewById(R.id.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.enlight_controls_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        recyclerView.setAdapter(new ControlListAdapter(ControlSwitchDAO.getControls(this)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryServiceMap();
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

                    recyclerView.getAdapter().notifyItemRemoved(index);

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

    private void queryServiceMap() {
        final Context self = this;
        mrpc("*.info", null, new Result.Callback() {
            @Override
            public void onSuccess(JsonElement value) {
                super.onSuccess(value);
                MRPCResponses.InfoResponse infoResponse = Message.gson().fromJson(value, MRPCResponses.InfoResponse.class);
                ArrayList<String> filteredServices = new ArrayList<String>(infoResponse.services);
                Preferences.filterBlackListedServices(self, filteredServices);
                for(String path : infoResponse.aliases) {
                    if(!serviceMap.containsKey(path))
                        serviceMap.put(path, new HashSet<String>());
                    serviceMap.get(path).addAll(filteredServices);
                }
                if(!serviceMap.containsKey("*"))
                    serviceMap.put("*", new HashSet<String>());
                serviceMap.get("*").addAll(filteredServices);
            }
        });
    }

    private void initiateScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                ImportControlDialog.create(getFragmentManager(), result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                queryServiceMap();
                for(int i = 0; i < recyclerView.getChildCount(); i++) {
                    ((ControlListAdapter.ControlViewHolder)recyclerView.findViewHolderForLayoutPosition(i)).queryControlState();
                }
                return true;
            case R.id.scanlayout:
                initiateScan();
                return true;
            case R.id.generatescan:
                String url = "https://zxing.org/w/chart?cht=qr&chs=500x500&chld=L&choe=UTF-8&chl=";
                url += Uri.encode(EnlightApp.Gson().toJson(ControlSwitchDAO.getControls(this)));
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            case R.id.addcontrol:
                return true;
            case R.id.confirm:
                menuItemConfirm.setVisible(false);
                menuItemAdd.setVisible(true);
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
