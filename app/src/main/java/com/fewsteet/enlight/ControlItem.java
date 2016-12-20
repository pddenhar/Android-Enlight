package com.fewsteet.enlight;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peter on 12/8/16.
 */

public class ControlItem {
    public enum ControlType {
        toggle,
        button;
        public static String[] names() {
            ControlType[] states = values();
            String[] names = new String[states.length];

            for (int i = 0; i < states.length; i++) {
                names[i] = states[i].name();
            }

            return names;
        }
    }
    String path;
    String name;
    ControlType type;
    boolean state;

    public ControlItem(String name, String path, ControlType type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }
}
