package com.iGap.module;

import com.iGap.proto.ProtoGlobal;

public class StructSessionsgetActivelist {

    private long sessionId;
    private String name;
    private int appId;
    private int buildVersion;
    private String appVersion;
    private ProtoGlobal.Platform platform;
    private String platformVersion;
    private ProtoGlobal.Device device;
    private String deviceName;
    private ProtoGlobal.Language language;
    private String country;
    private boolean current;
    private int createTime;
    private int activeTime;
    private String ip;

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(int buildVersion) {
        this.buildVersion = buildVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public ProtoGlobal.Platform getPlatform() {
        return platform;
    }

    public void setPlatform(ProtoGlobal.Platform platform) {
        this.platform = platform;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    public ProtoGlobal.Device getDevice() {
        return device;
    }

    public void setDevice(ProtoGlobal.Device device) {
        this.device = device;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public ProtoGlobal.Language getLanguage() {
        return language;
    }

    public void setLanguage(ProtoGlobal.Language language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(int activeTime) {
        this.activeTime = activeTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
