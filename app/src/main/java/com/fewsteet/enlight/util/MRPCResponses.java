package com.fewsteet.enlight.util;

import android.content.Context;
import android.icu.text.IDNA;

import com.google.gson.JsonElement;

import net.vector57.android.mrpc.MRPCActivity;
import net.vector57.mrpc.MRPC;
import net.vector57.mrpc.Message;
import net.vector57.mrpc.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Alex Sherman on 12/20/2016.
 */

public class MRPCResponses {
    public static HashMap<String, HashSet<String>> serviceMap = new HashMap<>();
    public static HashMap<String, InfoResponse> nodeMap = new HashMap<>();
    public interface InfoResponseHandler {
        void handle(InfoResponse info);
    }
    public static void queryServiceMap(final Context ctx, final InfoResponseHandler handler) {
        MRPCActivity.mrpc("*.info", null, new Result.Callback() {
            @Override
            public void onSuccess(JsonElement value) {
                super.onSuccess(value);
                MRPCResponses.InfoResponse infoResponse = MRPC.gson().fromJson(value, MRPCResponses.InfoResponse.class);
                HashSet<String> filteredServices = new HashSet<String>(infoResponse.services);
                Preferences.filterBlackListedServices(ctx, filteredServices);
                nodeMap.put(infoResponse.uuid, infoResponse);
                for(String path : infoResponse.aliases) {
                    if(!serviceMap.containsKey(path))
                        serviceMap.put(path, new HashSet<String>());
                    serviceMap.get(path).addAll(filteredServices);
                }
                if(!serviceMap.containsKey("*"))
                    serviceMap.put("*", new HashSet<String>());
                serviceMap.get("*").addAll(filteredServices);
                if(handler != null)
                    handler.handle(infoResponse);
            }
        });
    }

    public static class ServiceInfo {
        public String name;
        public ArrayList<String> aliases;
    }
    public static class InfoResponse {
        public String uuid;
        public String ip;
        public ArrayList<String> aliases;
        public ArrayList<String> services;
    }
}
