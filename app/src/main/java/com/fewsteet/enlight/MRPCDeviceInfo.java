package com.fewsteet.enlight;

import net.vector57.android_mrpc.MRPC;

import java.util.HashSet;
import java.util.TreeSet;

/**
 * Created by peter on 11/13/16.
 */

public class MRPCDeviceInfo {
    public String uuid;
    public HashSet<String> aliases = new HashSet<String>();

    public MRPCDeviceInfo(String uuid) {
        this.uuid = uuid;
    }
}
