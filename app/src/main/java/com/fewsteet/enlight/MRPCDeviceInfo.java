package com.fewsteet.enlight;

import net.vector57.mrpc.MRPC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Created by peter on 11/13/16.
 */

public class MRPCDeviceInfo {
    public String uuid;
    public ArrayList<String> aliases;

    public MRPCDeviceInfo(String uuid) {
        this.uuid = uuid;
    }
}
