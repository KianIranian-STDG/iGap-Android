
package net.iGap.model.paymentPackage;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetFavoriteNumber {

    @SerializedName("data")
    private List<FavoriteNumber> mData;

    public List<FavoriteNumber> getData() {
        return mData;
    }

    public void setData(List<FavoriteNumber> data) {
        mData = data;
    }

}
