package net.iGap.news.repository.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsFirstPage {

    @SerializedName("sliders")
    private List<NewsSlider> mSlider;
    @SerializedName("buttons")
    private List<NewsMainBTN> mBtns;
    @SerializedName("news")
    private List<NewsFPList> mNews;
    @SerializedName("type")
    private int mType;

    public NewsFirstPage() {
    }

    public NewsFirstPage(List<NewsSlider> mSlider, List<NewsMainBTN> mBtns, List<NewsFPList> mNews, int mType) {
        this.mSlider = mSlider;
        this.mBtns = mBtns;
        this.mNews = mNews;
        this.mType = mType;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public List<NewsSlider> getmSlider() {
        return mSlider;
    }

    public void setmSlider(List<NewsSlider> mSlider) {
        this.mSlider = mSlider;
    }

    public List<NewsMainBTN> getmBtns() {
        return mBtns;
    }

    public void setmBtns(List<NewsMainBTN> mBtns) {
        this.mBtns = mBtns;
    }

    public List<NewsFPList> getmNews() {
        return mNews;
    }

    public void setmNews(List<NewsFPList> mNews) {
        this.mNews = mNews;
    }
}
