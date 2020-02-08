
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.Expose;

import net.iGap.model.popularChannel.Slide;

import java.util.List;

public class SliderDataModel {

    @Expose
    private List<Slide> data;
    @Expose
    private Info info;
    @Expose
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
