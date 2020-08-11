package net.iGap.model;

import androidx.annotation.Nullable;

public class MultiSelectStruct {
    private int action;
    private int name;

    public MultiSelectStruct(int action, int name) {
        this.action = action;
        this.name = name;
    }

    public int getAction() {
        return action;
    }

    public int getName() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        //add this check when want to delete special item and name is id of string resources
        if (obj instanceof MultiSelectStruct) {
            return this.name == ((MultiSelectStruct) obj).name;
        }
        return super.equals(obj);
    }
}