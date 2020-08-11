
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

import net.iGap.model.popularChannel.Slide;

import java.util.List;

public class SliderDataModel {

    @SerializedName("data")
    private List<Slide> data;
    @SerializedName("info")
    private Info info;
    @SerializedName("type")
    private String type;

    public List<Slide> getData() {
        return data;
    }

    public void setData(List<Slide> data) {
        this.data = data;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
