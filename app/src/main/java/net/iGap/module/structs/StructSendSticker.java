package net.iGap.module.structs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StructSendSticker {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("groupId")
    @Expose
    private String groupId;

    @SerializedName("token")
    @Expose
    private String token;

    public StructSendSticker(String id, String name, String groupId, String token) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}