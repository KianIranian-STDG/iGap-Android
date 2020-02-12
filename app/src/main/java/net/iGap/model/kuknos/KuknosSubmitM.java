package net.iGap.model.kuknos;

import com.google.gson.annotations.SerializedName;

public class KuknosSubmitM {

    @SerializedName("ok")
    private int ok;
    @SerializedName("name")
    private String name;
    @SerializedName("message")
    private String message;

    public KuknosSubmitM() {
    }

    public KuknosSubmitM(int ok, String name, String message) {
        this.ok = ok;
        this.name = name;
        this.message = message;
    }

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

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }
}
