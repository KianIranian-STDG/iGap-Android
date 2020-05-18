
package net.iGap.model.paymentPackage;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChargeFavorite {

    @SerializedName("data")
    private List<FavoriteNumber> data;

    public List<FavoriteNumber> getData() {
        return data;
    }

    public void setData(List<FavoriteNumber> data) {
        this.data = data;
    }

}
