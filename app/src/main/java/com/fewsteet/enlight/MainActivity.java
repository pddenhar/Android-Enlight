package com.fewsteet.enlight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.gson.JsonElement;

import net.vector57.mrpc.MRPC;
import net.vector57.mrpc.Message;
import net.vector57.mrpc.Result;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private MRPC mrpc;
    private HashMap <String, Switch> switches;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mrpc = MRPCSingleton.getInstance(getApplicationContext()).getMRPC();
        switches = new HashMap<>();
        switches.put("/A.light", (Switch)findViewById(R.id.front));
        switches.put("/B.light", (Switch)findViewById(R.id.couch));
        switches.put("*.light", (Switch)findViewById(R.id.all));

        for(Map.Entry<String, Switch> entry : switches.entrySet()) {
            String path = entry.getKey();
            Switch s = entry.getValue();
            attachSwitch(s, path);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        try {
            Log.d(TAG, "Closing MRPC");
            mrpc.close();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Starting MRPC");
        mrpc.start();
        for(Map.Entry<String, Switch> entry : switches.entrySet()) {
            String path = entry.getKey();
            Switch s = entry.getValue();
            updateSwitch(s, path);
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    private void attachSwitch(final Switch sw, final String path) {
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mrpc.RPC(path, isChecked);
            }
        });
    }
    private void updateSwitch(final Switch sw, final String path) {
        Log.d(TAG, "Sending message for "+path);
        mrpc.RPC(path, null, new Result.Callback() {
            @Override
            public void onSuccess(JsonElement value) {
                Log.d(TAG, "Got result for path "+path + " and switch "+sw);
                Boolean b = Message.gson().fromJson(value, Boolean.class);
                if(b != null && sw != null) {

                    sw.setChecked(b);
                }
            }
        });
    }
    public void launchSettings(MenuItem item) {
        return;
    }
    public void launchDevices(MenuItem item) {
        Intent i = new Intent(this, DeviceBrowserActivity.class);
        this.startActivity(i);
    }
}
