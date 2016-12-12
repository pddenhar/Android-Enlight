package com.fewsteet.enlight.debug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.fewsteet.enlight.EnlightApp;
import com.fewsteet.enlight.R;
import com.fewsteet.enlight.util.Util;
import com.google.gson.JsonElement;

import net.vector57.mrpc.Message;
import net.vector57.mrpc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DebugActivity extends AppCompatActivity {

    private static final String LOGTAG = "DebugActivity";
    private static final String GUID_KEY = "GUID";

    public static Intent createIntent(Activity caller, String guid) {
        return new Intent(caller, DebugActivity.class).putExtra(GUID_KEY, guid);
    }

    private ProgressBar progressBar;
    private View content;
    private Spinner functionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        progressBar = (ProgressBar) findViewById(R.id.debug_progress);
        content = findViewById(R.id.debug_content);
        functionList = (Spinner) findViewById(R.id.function_spinner);

        final TextView requestView = (TextView) findViewById(R.id.request);
        final TextView responseView = (TextView) findViewById(R.id.response);

        findViewById(R.id.perform_request_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                responseView.setText("");

                EnlightApp.MRPC().RPC(
                        getIntent().getStringExtra(GUID_KEY) + "." + functionList.getSelectedItem(),
                        requestView.getText().toString(),
                        new Result.Callback() {
                            @Override
                            public void onResult(Message.Response response) {

                                String responseString;
                                if(response.error != null) {
                                    responseString = "Error:\n" + response.error.toString();
                                } else {
                                    responseString = "Success:\n " + response.result.toString();
                                }
                                responseView.setText(responseString);
                            }
                        }
                );
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOGTAG, "Starting MRPC");
        try {
            EnlightApp.MRPC().start(Util.getBroadcastAddress(this));
        } catch (IOException e) {
            throw new RuntimeException("MRPC start failed.", e);
        }

        EnlightApp.MRPC().RPC(getIntent().getStringExtra(GUID_KEY) + ".configure_service", null, new Result.Callback() {
            @Override
            public void onResult(Message.Response response) {
                progressBar.setVisibility(View.GONE);
                super.onResult(response);
                content.setVisibility(View.VISIBLE);

            }
            @Override
            public void onSuccess(JsonElement value) {

                final List<String> functions = new ArrayList<>();
                for(Map.Entry<String, JsonElement> child : value.getAsJsonObject().entrySet()) {
                    functions.add(child.getKey());
                }

                ArrayAdapter<String> functionAdapter = new ArrayAdapter<>(DebugActivity.this, android.R.layout.simple_spinner_item, functions);
                functionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                functionList.setAdapter(functionAdapter);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        EnlightApp.storeApplicationState();

        try {
            Log.d(LOGTAG, "Closing MRPC");
            EnlightApp.MRPC().close();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

}
