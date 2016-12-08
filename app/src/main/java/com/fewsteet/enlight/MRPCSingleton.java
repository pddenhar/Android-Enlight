package com.fewsteet.enlight;

import android.content.Context;

import net.vector57.mrpc.MRPC;

/**
 * Created by peter on 12/8/16.
 */

public class MRPCSingleton {
    private static MRPCSingleton mInstance;
    private MRPC mrpc_instance;
    private static Context mCtx;

    private MRPCSingleton(Context context) {
        // getApplicationContext() is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        // because those can be destroyed at any time
        mCtx = context.getApplicationContext();
    }

    public static synchronized MRPCSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MRPCSingleton(context);
        }
        return mInstance;
    }

    public MRPC getMRPC() {
        if (mrpc_instance == null) {
            mrpc_instance = new MRPC(mCtx);
        }
        return mrpc_instance;
    }
}
