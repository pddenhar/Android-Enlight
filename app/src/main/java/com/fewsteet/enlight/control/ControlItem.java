package com.fewsteet.enlight.control;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

/**
 * Created by peter on 12/8/16.
 */

public class ControlItem {
    public enum ControlType {
        toggle,
        slider,
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
    public String path;
    public String name;
    public ControlType type;
    public JsonElement state;

    public ControlItem(String name, String path, ControlType type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }
}
