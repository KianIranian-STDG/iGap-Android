package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ExtraDetail {

    @SerializedName("CoverImage")
    private String mCoverImage;
    @SerializedName("FullDescription")
    private String mFullDescription;
    @SerializedName("ShortDescription")
    private String mShortDescription;
    @SerializedName("Gallery_1")
    private String mGallery1;
    @SerializedName("Gallery_2")
    private String mGallery2;
    @SerializedName("Gallery_3")
    private String mGallery3;
    @SerializedName("Gallery_4")
    private String mGallery4;


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

    public String getmGallery1() {
        return mGallery1;
    }

    public void setmGallery1(String mGallery1) {
        this.mGallery1 = mGallery1;
    }

    public String getmGallery2() {
        return mGallery2;
    }

    public void setmGallery2(String mGallery2) {
        this.mGallery2 = mGallery2;
    }

    public String getmGallery3() {
        return mGallery3;
    }

    public void setmGallery3(String mGallery3) {
        this.mGallery3 = mGallery3;
    }

    public String getmGallery4() {
        return mGallery4;
    }

    public void setmGallery4(String mGallery4) {
        this.mGallery4 = mGallery4;
    }

    public String getmShortDescription() {
        return mShortDescription;
    }

    public void setmShortDescription(String mShortDescription) {
        this.mShortDescription = mShortDescription;
    }
}
