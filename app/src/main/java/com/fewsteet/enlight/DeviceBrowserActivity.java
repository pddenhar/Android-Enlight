package com.fewsteet.enlight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.vector57.android_mrpc.MRPC;
import net.vector57.android_mrpc.Message;
import net.vector57.android_mrpc.Result;

import java.util.HashSet;
import java.util.List;

public class DeviceBrowserActivity extends AppCompatActivity {
    final static String TAG = "DeviceBrowserActivity";
    private MRPC mrpc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_browser);
        mrpc = MRPCSingleton.getInstance(getApplicationContext()).getMRPC();
        findDevices();
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
        final HashSet<String> devices = new HashSet<>();
        mrpc.RPC("*.alias", null, new Result.Callback() {
            @Override
            public void onSuccess(JsonElement value) {
                List<String> names = Message.gson().fromJson(value, new TypeToken<List<String>>(){}.getType());
                if(names != null) {
                    for (String name : names) {
                        devices.add(name);
                        Log.d(TAG, name);
                    }
                }
                super.onSuccess(value);
            }
        });
    }
}
