package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class ExtraDetail {

    @SerializedName("CoverImage")
    private String mCoverImage;
    @SerializedName("FullDescription")
    private String mFullDescription;
    @SerializedName("ShortDescription")
    private String mShortDescription;

    public List<Gallery> getmGallery() {
        return mGallery;
    }

    public void setmGallery(List<Gallery> mGallery) {
        this.mGallery = mGallery;
    }

    @SerializedName("Gallery")
    private List<Gallery> mGallery;


    public String getmCoverImage() {
        return mCoverImage;
    }

    public void setmCoverImage(String mCoverImage) {
        this.mCoverImage = mCoverImage;
    }

    public String getmFullDescription() {
        return mFullDescription;
    }

    public void setmFullDescription(String mFullDescription) {
        this.mFullDescription = mFullDescription;
    }

    public String getmShortDescription() {
        return mShortDescription;
    }

    public void setmShortDescription(String mShortDescription) {
        this.mShortDescription = mShortDescription;
    }
}
