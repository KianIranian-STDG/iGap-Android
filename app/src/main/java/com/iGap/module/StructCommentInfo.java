package com.iGap.module;

import java.util.ArrayList;

/**
 * Created by android3 on 8/31/2016.
 */
public class StructCommentInfo {

    public String senderPicturePath = "";
    public String senderID = "";
    public String senderName = "";
    public String message = "";
    public String date = "";
    public String time = "";
    public boolean maxLine = true;

    public boolean isChange = false;
    public ArrayList<String> allChanges = new ArrayList<>();

    public ArrayList<StructCommentInfo> replayMessageList;
}
