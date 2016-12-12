package com.fewsteet.enlight;

import android.app.Application;
import android.content.Context;

import com.fewsteet.enlight.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.vector57.mrpc.MRPC;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by peter on 12/8/16.
 */

public class EnlightApp extends Application {
    private static Context context;
    private static MRPC mrpc;
    private static Gson gson;
    public static final String PATH_CACHE_FILENAME = "path_cache.json";
    public static final String LAYOUT_CONFIG_FILENAME = "layout_config.json";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static synchronized MRPC MRPC() {
        if(mrpc == null) {
            Type t = new TypeToken<Map<String, List<String>>>() {}.getType();
            Map<String, List<String>> pathCache = Util.readFromFile(context, PATH_CACHE_FILENAME, t);
            mrpc = new MRPC(context, pathCache);
        }
        return mrpc;
    }
    public static synchronized Gson Gson() {
        if(gson == null) {
            gson = new Gson();
        }
        return gson;
    }
    public static void storeApplicationState() {
        Map<String, List<String>> pathCache = EnlightApp.MRPC().getPathCache();
        Util.writeToFile(context, EnlightApp.PATH_CACHE_FILENAME, pathCache);
    }
}
