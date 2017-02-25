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
        button,
        color;
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
    public JsonElement argument;
    public transient JsonElement state;

    public ControlItem(String name, String path, JsonElement argument, ControlType type) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.argument = argument;
    }

    public boolean isValid() {
        return path != null && name != null && type != null;
    }
}
