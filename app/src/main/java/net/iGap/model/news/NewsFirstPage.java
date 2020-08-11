package net.iGap.model.news;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsFirstPage {

    @SerializedName("buttons")
    private List<NewsMainBTN> mBtns;
    @SerializedName("news")
    private List<NewsFPList> mNews;
    @SerializedName("type")
    private int mType;

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
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
