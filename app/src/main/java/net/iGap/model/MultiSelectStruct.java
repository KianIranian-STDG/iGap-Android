package net.iGap.model;

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
}