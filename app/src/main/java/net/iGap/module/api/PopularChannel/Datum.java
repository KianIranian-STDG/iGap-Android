
package net.iGap.module.api.PopularChannel;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Datum {

    @SerializedName("data_count")
    private Long mDataCount;
    @SerializedName("id")
    private String mId;
    @SerializedName("info")
    private Info mInfo;
    @SerializedName("slides")
    private List<Slide> mSlides;
    @SerializedName("type")
    private String mType;

    public Long getDataCount() {
        return mDataCount;
    }

    public void setDataCount(Long dataCount) {
        mDataCount = dataCount;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Info getInfo() {
        return mInfo;
    }

    public void setInfo(Info info) {
        mInfo = info;
    }

    public List<Slide> getSlides() {
        return mSlides;
    }

    public void setSlides(List<Slide> slides) {
        mSlides = slides;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
