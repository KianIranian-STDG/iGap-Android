package net.iGap.kuknos.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class KuknosWalletBalanceInfoM implements Parcelable {

    private String balance;
    private String assetType;
    private String assetCode;
    private String assetPicURL;

    public KuknosWalletBalanceInfoM() {
    }

    public KuknosWalletBalanceInfoM(String balance, String assetType, String assetCode, String assetPicURL) {
        this.balance = balance;
        this.assetType = assetType;
        this.assetCode = assetCode;
        this.assetPicURL = assetPicURL;
    }

    public KuknosWalletBalanceInfoM(Parcel in) {
        balance = in.readString();
        assetType = in.readString();
        assetCode = in.readString();
        assetPicURL = in.readString();
    }

    public static final Creator<KuknosWalletBalanceInfoM> CREATOR = new Creator<KuknosWalletBalanceInfoM>() {
        @Override
        public KuknosWalletBalanceInfoM createFromParcel(Parcel in) {
            return new KuknosWalletBalanceInfoM(in);
        }

        @Override
        public KuknosWalletBalanceInfoM[] newArray(int size) {
            return new KuknosWalletBalanceInfoM[size];
        }
    };

    public static Creator<KuknosWalletBalanceInfoM> getCREATOR() {
        return CREATOR;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetPicURL() {
        return assetPicURL;
    }

    public void setAssetPicURL(String assetPicURL) {
        this.assetPicURL = assetPicURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(balance);
        dest.writeString(assetType);
        dest.writeString(assetCode);
        dest.writeString(assetPicURL);
    }
}
