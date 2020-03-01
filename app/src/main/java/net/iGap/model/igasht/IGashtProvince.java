package net.iGap.model.igasht;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class IGashtProvince implements Parcelable {

    @SerializedName("id")
    private int id;
    @SerializedName("province_name")
    private String provinceName;
    @SerializedName("english_name")
    private String englishName;
    @SerializedName("activation")
    private boolean activation;

    protected IGashtProvince(Parcel in) {
        id = in.readInt();
        provinceName = in.readString();
        englishName = in.readString();
        activation = in.readByte() != 0;
    }

    public static final Creator<IGashtProvince> CREATOR = new Creator<IGashtProvince>() {
        @Override
        public IGashtProvince createFromParcel(Parcel in) {
            return new IGashtProvince(in);
        }

        @Override
        public IGashtProvince[] newArray(int size) {
            return new IGashtProvince[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(provinceName);
        dest.writeString(englishName);
        dest.writeByte((byte) (activation ? 1 : 0));
    }
}
