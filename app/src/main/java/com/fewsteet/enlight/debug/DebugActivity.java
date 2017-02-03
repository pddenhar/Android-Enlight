package com.fewsteet.enlight.debug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import net.vector57.android.mrpc.MRPCActivity;
import com.fewsteet.enlight.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.vector57.mrpc.Message;
import net.vector57.mrpc.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DebugActivity extends MRPCActivity {

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
                mrpc(
                        getIntent().getStringExtra(GUID_KEY) + "." + functionList.getSelectedItem(),
                        new JsonParser().parse(requestView.getText().toString()),
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

        mrpc().RPC(getIntent().getStringExtra(GUID_KEY) + ".configure_service", null, new Result.Callback() {
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
}
