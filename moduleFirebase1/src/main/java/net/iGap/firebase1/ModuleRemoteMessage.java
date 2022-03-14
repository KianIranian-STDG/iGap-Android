package net.iGap.firebase1;

import java.util.Map;

public class ModuleRemoteMessage {
    private ModuleNotification notification;
    private Map<String, String> data;
    private String collapseKey;
    private String form;
    private String messageId;
    private String messageType;
    private int originalPriority;
    private int priority;
    private long sentTime;
    private String to;
    private int ttl;

    public ModuleRemoteMessage(ModuleNotification notification, Map<String, String> data, String collapseKey, String form, String messageId, String messageType, int originalPriority, int priority, long sentTime, String to, int ttl) {
        this.notification = notification;
        this.data = data;
        this.collapseKey = collapseKey;
        this.form = form;
        this.messageId = messageId;
        this.messageType = messageType;
        this.originalPriority = originalPriority;
        this.priority = priority;
        this.sentTime = sentTime;
        this.to = to;
        this.ttl = ttl;
    }

    public ModuleNotification getNotification() {
        return notification;
    }

    public void setNotification(ModuleNotification notification) {
        this.notification = notification;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getCollapseKey() {
        return collapseKey;
    }

    public void setCollapseKey(String collapseKey) {
        this.collapseKey = collapseKey;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public int getOriginalPriority() {
        return originalPriority;
    }

    public void setOriginalPriority(int originalPriority) {
        this.originalPriority = originalPriority;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
