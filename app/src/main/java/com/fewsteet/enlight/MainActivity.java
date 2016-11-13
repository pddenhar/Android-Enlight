package com.fewsteet.enlight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        attachSwitch(R.id.front, "/A.relay");
        attachSwitch(R.id.couch, "/B.relay");
        attachSwitch(R.id.all, "*.relay");
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
        mrpc.RPC(path, null, new Result.Callback() {
            @Override
            public void onSuccess(JsonElement value) {
                Boolean b = Message.gson().fromJson(value, Boolean.class);
                if(b != null) {
                    sw.setChecked(b);
                }
            }
        });
    }
}
