package net.iGap.model;

import com.google.gson.annotations.SerializedName;

public class UploadData {

    @SerializedName("token")
    private String token;
    @SerializedName("room_id")
    private String roomID;
    @SerializedName("name")
    private String fileName;
    @SerializedName("size")
    private String fileSize;
    @SerializedName("uploaded_size")
    private String uploadedSize;
    @SerializedName("extension")
    private String fileExtension;

    public void setToken(String token) {
        this.token = token;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setUploadedSize(String uploadedSize) {
        this.uploadedSize = uploadedSize;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getToken() {
        return token;
    }

    public String getRoomID() {
        return roomID;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getUploadedSize() {
        return uploadedSize;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
