package net.iGap.model.igasht;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import net.iGap.G;

public class LocationDetail implements Parcelable {

    @SerializedName("id")
    private int id;
    @SerializedName("top_text")
    private String topText;
    @SerializedName("bottom_text")
    private String bottomText;
    @SerializedName("english_top_text")
    private String englishTopText;
    @SerializedName("english_bottom_text")
    private String englishBottomText;

    protected LocationDetail(Parcel in) {
        id = in.readInt();
        topText = in.readString();
        bottomText = in.readString();
        englishTopText = in.readString();
        englishBottomText = in.readString();
    }

    public static final Creator<LocationDetail> CREATOR = new Creator<LocationDetail>() {
        @Override
        public LocationDetail createFromParcel(Parcel in) {
            return new LocationDetail(in);
        }

        @Override
        public LocationDetail[] newArray(int size) {
            return new LocationDetail[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getTopText() {
        return topText;
    }

    public String getBottomText() {
        return bottomText;
    }

    public String getEnglishTopText() {
        return englishTopText;
    }

    public String getEnglishBottomText() {
        return englishBottomText;
    }

    public String getTopTextWithLanguage() {
        switch (G.selectedLanguage) {
            case "fa":
                return getTopText();
            case "en":
                return getEnglishTopText();
            default:
                return getTopText();
        }
    }

    public String getBottomTextWithLanguage() {
        switch (G.selectedLanguage) {
            case "fa":
                return getBottomText();
            case "en":
                return getEnglishBottomText();
            default:
                return getBottomText();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(topText);
        dest.writeString(bottomText);
        dest.writeString(englishTopText);
        dest.writeString(englishBottomText);
    }
}
