package com.fewsteet.enlight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.gson.JsonElement;

import net.vector57.android_mrpc.MRPC;
import net.vector57.android_mrpc.Message;
import net.vector57.android_mrpc.Result;

public class MainActivity extends AppCompatActivity {
    private MRPC mrpc;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mrpc = MRPCSingleton.getInstance(getApplicationContext()).getMRPC();
        attachSwitch(R.id.front, "/A.light");
        attachSwitch(R.id.couch, "/B.light");
        attachSwitch(R.id.all, "*.light");
    }
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    private void attachSwitch(int switchId, final String path) {
        updateSwitch(switchId, path);
        ((Switch)findViewById(switchId)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mrpc.RPC(path, isChecked);
            }
        });
    }
    private void updateSwitch(int switchId, final String path) {
        final Switch sw = (Switch)findViewById(switchId);
        mrpc.RPC(path, null, new Result.JSONCallback() {
            @Override
            public void onSuccess(JsonElement value) {
                Boolean b = Message.gson().fromJson(value, Boolean.class);
                if(b != null) {
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
