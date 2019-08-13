package net.iGap.internetpackage;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MciInternetPackageFilterResponse {
    @SerializedName("daily")
    private List<String> daily;
    @SerializedName("gb")
    private List<String> gb;
    @SerializedName("mb")
    private List<String> mb;

    public List<String> getDaily() {
        return daily;
    }

    public List<String> getGb() {
        return gb;
    }

    public List<String> getMb() {
        return mb;
    }
}
