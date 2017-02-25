package com.fewsteet.enlight.util;

import java.util.ArrayList;

/**
 * Created by Alex Sherman on 12/20/2016.
 */

public class MRPCResponses {
    public static class DeviceInfo {
        public String uuid;
        public ArrayList<String> aliases;
        public DeviceInfo(String uuid) {
            this.uuid = uuid;
        }
    }
    public static class ServiceInfo {
        public String name;
        public ArrayList<String> aliases;
    }
    public static class InfoResponse {
        public String uuid;
        public String ip;
        public ArrayList<String> aliases;
        public ArrayList<String> services;
    }
}
