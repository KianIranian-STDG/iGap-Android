package net.iGap.igasht;

import com.google.gson.annotations.SerializedName;

public class IGashtProvince {

    @SerializedName("id")
    private int id;
    @SerializedName("province_name")
    private String provinceName;
    @SerializedName("english_name")
    private String englishName;
    @SerializedName("activation")
    private boolean activation;

    public int getId() {
        return id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public boolean isActivation() {
        return activation;
    }
}
