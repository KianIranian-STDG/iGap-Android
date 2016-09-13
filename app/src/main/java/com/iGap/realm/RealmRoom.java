package com.iGap.realm;

import android.text.format.DateUtils;

import com.iGap.realm.enums.RoomType;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// note: realm doesn't support enum
// as a workaround, we save its toString() value
// https://github.com/realm/realm-java/issues/776
public class RealmRoom extends RealmObject {
    @PrimaryKey
    private long id;
    private String type;
    private String title;
    private String initials;
    private String color;
    private int unread_count;
    private boolean mute;
    private RealmChatRoom chat_room;
    private RealmGroupRoom group_room;
    private RealmChannelRoom channel_room;
    private long lastMessageId;
    private long lastMessageTime;

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(int lastMessageTime) {
        this.lastMessageTime = lastMessageTime * DateUtils.SECOND_IN_MILLIS;
    }

    public long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RoomType getType() {
        return (type != null) ? RoomType.valueOf(type) : null;
    }

    public void setType(RoomType type) {
        this.type = type.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getUnreadCount() {
        return unread_count;
    }

    public void setUnreadCount(int unread_count) {
        this.unread_count = unread_count;
    }

    public boolean getMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public RealmChatRoom getChatRoom() {
        return chat_room;
    }

    public void setChatRoom(RealmChatRoom chat_room) {
        this.chat_room = chat_room;
    }

    public RealmGroupRoom getGroupRoom() {
        return group_room;
    }

    public void setGroupRoom(RealmGroupRoom group_room) {
        this.group_room = group_room;
    }

    public RealmChannelRoom getChannelRoom() {
        return channel_room;
    }

    public void setChannelRoom(RealmChannelRoom channel_room) {
        this.channel_room = channel_room;
    }
}
