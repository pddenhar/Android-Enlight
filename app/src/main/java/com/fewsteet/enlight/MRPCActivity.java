package com.fewsteet.enlight;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fewsteet.enlight.util.Util;
import com.google.gson.reflect.TypeToken;

import net.vector57.mrpc.MRPC;
import net.vector57.mrpc.Result;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex Sherman on 12/20/2016.
 */

public class MRPCActivity extends AppCompatActivity {
    private static int open_mrpcs = 0;
    private static MRPC _mrpc;
    public static MRPC mrpc() { return _mrpc; }
    public static void mrpc(String path, Object value, Result.Callback callback) { _mrpc.RPC(path, value, callback); }
    public static void mrpc(String path, Object value) { _mrpc.RPC(path, value); }

    private synchronized MRPC allocateMRPC() {
        if(open_mrpcs == 0) {
            if(BuildConfig.DEBUG && _mrpc != null)
                throw new AssertionError("Reference counting logic failure");

            Type t = new TypeToken<Map<String, List<String>>>() {}.getType();
            Map<String, List<String>> pathCache = Util.readFromFile(this, EnlightApp.PATH_CACHE_FILENAME, t);

            try {
                _mrpc = new MRPC(this, Util.getBroadcastAddress(this), pathCache);
            } catch (IOException e) {
                throw new AssertionError("I don't like checked exceptions");
            }
        }
        open_mrpcs++;
        return _mrpc;
    }

    private synchronized void deallocateMRPC() {
        open_mrpcs--;
        if(open_mrpcs == 0) {
            Map<String, List<String>> pathCache = _mrpc.getPathCache();
            Util.writeToFile(this, EnlightApp.PATH_CACHE_FILENAME, pathCache);
            _mrpc.close();
            _mrpc = null;
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Log.d("MRPC", "trimmed");
        }
    }

    @Override
    protected void onStart() {
        allocateMRPC();
        super.onStart();
    }

    @Override
    protected void onStop() {
        deallocateMRPC();
        super.onStop();
    }
}
