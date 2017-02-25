package com.fewsteet.enlight.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fewsteet.enlight.EnlightApp;
import com.fewsteet.enlight.R;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex Sherman on 2/23/2017.
 */

public class Preferences {
    public static List<String> blackListedServices(Context ctx) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        String blackListJson = sharedPref.getString("service_blacklist", ctx.getString(R.string.default_service_blacklist));
        return EnlightApp.Gson().fromJson(blackListJson, new TypeToken<ArrayList<String>>() {}.getType());
    }
    public static void filterBlackListedServices(Context ctx, List<String> services) {
        // Remove services we don't care about
        for (String blackListedService: blackListedServices(ctx)) {
            while(services.contains(blackListedService)) {
                services.remove(blackListedService);
            }
        }
    }
}
