package net.iGap.kuknos.Model.Parsian;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Owners {

    @SerializedName("owners")
    private List<OwnersEntity> owners;

    public List<OwnersEntity> getOwners() {
        return owners;
    }

    public void setOwners(List<OwnersEntity> owners) {
        this.owners = owners;
    }

}
