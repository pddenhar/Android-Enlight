package com.fewsteet.enlight;

import android.app.Application;
import android.content.Context;

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
    public static final String PATH_CACHE_FILENAME = "path_cache.json";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Type t = new TypeToken<Map<String, List<String>>>() {}.getType();
        Map<String, List<String>> pathCache = Util.readFromFile(this, PATH_CACHE_FILENAME, t);
    }

    public static synchronized MRPC MRPC() {
        if(mrpc == null) {
            Type t = new TypeToken<Map<String, List<String>>>() {}.getType();
            Map<String, List<String>> pathCache = Util.readFromFile(context, PATH_CACHE_FILENAME, t);
            mrpc = new MRPC(context, pathCache);
        }
        return mrpc;
    }
}
