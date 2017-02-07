package com.fewsteet.enlight.control;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fewsteet.enlight.EnlightApp;
import com.fewsteet.enlight.R;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ControlSwitchDAO {

    private static final String TAG = ControlSwitchDAO.class.getSimpleName();
    private static List<ControlItem> controls = null;
    public static List<ControlItem> getControls(Context context) {
        if(controls == null) {
            String layoutJson = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(getKey(context), getDefault(context));

            Type t = new TypeToken<List<ControlItem>>() { }.getType();
            controls = EnlightApp.Gson().fromJson(layoutJson, t);
            for(int i = 0; i < controls.size();) {
                if(controls.get(i) == null || !controls.get(i).isValid()) {
                    controls.remove(i);
                    continue;
                }
                i++;
            }
        }
        return controls;
    }

    public static ControlItem removeControl(Context context, int index) {
        List<ControlItem> layout = getControls(context);
        ControlItem output = layout.remove(index);
        saveControls(context);
        return output;
    }

    public static void removeControl(Context context, ControlItem control) {
        List<ControlItem> layout = getControls(context);
        layout.remove(control);
        saveControls(context);
    }

    public static void addControl(Context context, int index, ControlItem control) {
        List<ControlItem> layout = getControls(context);
        layout.add(index, control);
        saveControls(context);
    }

    public static void addControl(Context context, ControlItem control) {
        List<ControlItem> layout = getControls(context);
        layout.add(control);
        saveControls(context);
    }

    public static void clear(Context context) {
        getControls(context).clear();
        saveControls(context);
    }

    public static void saveControls(Context context) {

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
