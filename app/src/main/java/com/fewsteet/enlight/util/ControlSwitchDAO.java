package com.fewsteet.enlight.util;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fewsteet.enlight.EnlightApp;
import com.fewsteet.enlight.R;
import com.fewsteet.enlight.control.ControlItem;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ControlSwitchDAO {

    private static final String TAG = ControlSwitchDAO.class.getSimpleName();

    public static List<ControlItem> getControls(Context context) {

        String layoutJson = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(getKey(context), getDefault(context));

        Type t = new TypeToken<List<ControlItem>>() {}.getType();

        return EnlightApp.Gson().fromJson(layoutJson, t);
    }

    public static void removeControl(Context context, ControlItem control) {

        List<ControlItem> layout = getControls(context);
        layout.remove(control);

        saveControls(context, layout);
    }

    public static void addControl(Context context, ControlItem control) {

        List<ControlItem> layout = getControls(context);
        layout.add(control);

        saveControls(context, layout);
    }

    public static void saveControls(Context context, List<ControlItem> controls) {

        String json = EnlightApp.Gson().toJson(controls);
        Log.d(TAG, json);

        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(getKey(context), json)
                .apply();
    }

    private static String getKey(Context context) {
        return context.getString(R.string.layout_preference_key);
    }

    private static String getDefault(Context context) {
        return context.getString(R.string.default_layout);
    }

}
