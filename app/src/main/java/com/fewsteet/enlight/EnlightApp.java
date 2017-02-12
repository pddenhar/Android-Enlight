package com.fewsteet.enlight;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;

import net.vector57.mrpc.MRPC;

/**
 * Created by peter on 12/8/16.
 */

public class EnlightApp extends Application {
    private static Gson gson;

    public static synchronized Gson Gson() {
        if(gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
