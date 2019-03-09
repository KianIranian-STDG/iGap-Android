package net.iGap.module.dashboard;

import java.util.ArrayList;

public class DashboardModel {
    int type;
    ArrayList<String> imageList;
    ArrayList<String> actionList;
    long height;


    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    public ArrayList<String> getActionList() {
        return actionList;
    }

    public void setActionList(ArrayList<String> actionList) {
        this.actionList = actionList;
    }
}
