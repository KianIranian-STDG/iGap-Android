package net.iGap.news.repository.model;

import com.google.gson.annotations.SerializedName;

public class NewsPublisher {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("mainAddress")
    private String address;

    public NewsPublisher() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
