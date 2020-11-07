package net.iGap.kuknos.Model.Parsian;

import com.google.gson.annotations.SerializedName;

public class KuknosResponseModel<G> {

    @SerializedName("name")
    private String name;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private G data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public G getData() {
        return data;
    }

    public void setData(G data) {
        this.data = data;
    }
}
