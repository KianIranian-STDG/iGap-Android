package net.iGap.model.news;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsApiArg implements Parcelable {

    public static final Creator<NewsApiArg> CREATOR = new Creator<NewsApiArg>() {
        @Override
        public NewsApiArg createFromParcel(Parcel in) {
            return new NewsApiArg(in);
        }

        @Override
        public NewsApiArg[] newArray(int size) {
            return new NewsApiArg[size];
        }
    };
    private int start;
    private int display;
    private int groupID;
    private NewsType mType;

    public NewsApiArg(int start, int display, int groupID, NewsType mType) {
        this.start = start;
        this.display = display;
        this.groupID = groupID;
        this.mType = mType;
    }

    private NewsApiArg(Parcel in) {
        start = in.readInt();
        display = in.readInt();
        groupID = in.readInt();
        mType = NewsType.values()[in.readInt()];
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getDisplay() {
        return display;
    }

    public void setDisplay(int display) {
        this.display = display;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public NewsType getmType() {
        return mType;
    }

    public void setmType(NewsType mType) {
        this.mType = mType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(start);
        dest.writeInt(display);
        dest.writeInt(groupID);
        dest.writeInt(mType.ordinal());
    }

    public enum NewsType {Latest, MOST_HITS, FEATURED_NEWS, GROUP_NEWS, FEATURED_GROUP, ERGENT_GROUP, ERGENT, CONTROVERSIAL_NEWS, RELATED_NEWS}
}
