package net.iGap.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class PassCode {

    private static final String DB_NAME = "PassCode";
    private static final String DB_KEY = "instance";


    @SerializedName("isPassCode")
    private boolean isPassCode;

    @SerializedName("isPattern")
    private boolean isPattern;

    @SerializedName("isFingerPrint")
    private boolean isFingerPrint;

    @SerializedName("passCode")
    private String passCode;

    @SerializedName("kindPassCode")
    private int kindPassCode;

    private transient SharedPreferences sharedPreferences;

    private transient static PassCode instance;

    private PassCode() {

    }

    public static void initPassCode(Context context) {
        if (instance == null) {
            Gson gson = new Gson();
            SharedPreferences sharedPreferences = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
            String passCodeStr = sharedPreferences.getString(DB_KEY, "");
            if (passCodeStr == null || passCodeStr.equals("")) {
                instance = new PassCode();
            } else {
                instance = gson.fromJson(passCodeStr, PassCode.class);
            }
            instance.sharedPreferences = sharedPreferences;
        }
    }

    public static void initPassCode(Context context, boolean isPassCode, boolean isPattern, boolean isFingerPrint, String passCode, int kindPassCode) {
        instance = new PassCode();
        instance.isPassCode = isPassCode;
        instance.isPattern = isPattern;
        instance.isFingerPrint = isFingerPrint;
        instance.passCode = passCode;
        instance.kindPassCode = kindPassCode;
        instance.sharedPreferences = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        instance.saveToDb();
    }

    public static PassCode getInstance() {
        return instance;
    }

    private void saveToDb() {
        Gson gson = new Gson();
        sharedPreferences.edit().putString(DB_KEY, gson.toJson(this)).apply();
    }

    public boolean isFingerPrint() {
        return isFingerPrint;
    }

    public void setFingerPrint(boolean fingerPrint) {
        isFingerPrint = fingerPrint;
        saveToDb();
    }

    public int getKindPassCode() {
        return kindPassCode;
    }

    public void setKindPassCode(int kindPassCode) {
        this.kindPassCode = kindPassCode;
        saveToDb();
    }

    public boolean isPassCode() {
        return isPassCode;
    }

    public boolean isPattern() {
        return isPattern;
    }

    public void setPattern(boolean pattern) {
        isPattern = pattern;
        saveToDb();
    }

    public void setPassCode(boolean passCode) {
        isPassCode = passCode;
        saveToDb();
    }

    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
        saveToDb();
    }

}
