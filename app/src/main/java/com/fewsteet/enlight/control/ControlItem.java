package com.fewsteet.enlight.control;

import com.google.gson.JsonElement;

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

    public ControlItem() {
        this.type = ControlType.toggle;
    }

    public ControlItem(String name, String path, ControlType type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ControlItem that = (ControlItem) o;

        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (type != that.type) return false;
        return state != null ? state.equals(that.state) : that.state == null;

    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }

}
