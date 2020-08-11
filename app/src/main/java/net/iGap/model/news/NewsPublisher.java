package net.iGap.model.news;

import com.google.gson.annotations.SerializedName;

public class NewsPublisher {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("mainAddress")
    private String address;
    @SerializedName("image")
    private String image;

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

    public String getImage() {
        if (image == null || image.isEmpty())
            return null;
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
