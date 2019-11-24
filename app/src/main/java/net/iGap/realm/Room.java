/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.realm;

import android.text.format.DateUtils;

import androidx.annotation.Nullable;

import net.iGap.G;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperString;
import net.iGap.interfaces.OnClientGetRoomMessage;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.enums.RoomType;
import net.iGap.module.structs.StructMessageOption;
import net.iGap.proto.ProtoClientGetPromote;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestChannelUpdateDraft;
import net.iGap.request.RequestChatUpdateDraft;
import net.iGap.request.RequestClientGetRoom;
import net.iGap.request.RequestClientGetRoomMessage;
import net.iGap.request.RequestGroupUpdateDraft;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

import static net.iGap.G.userId;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class Room{

    private long id;
    private String type;
    private String title;
    private String groupRole;
    private String channelRole;

    public Room(long id, String type, String title, String groupRole, String channelRole) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.groupRole = groupRole;
        this.channelRole = channelRole;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProtoGlobal.Room.Type getType() {
        return ProtoGlobal.Room.Type.valueOf(type);
    }

    public void setType(RoomType type) {
        this.type = type.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        try {
            this.title = title;
        } catch (Exception e) {
            this.title = HelperString.getUtf8String(title);
        }
    }

    public static boolean isBot(long userId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);
            if (realmRegisteredInfo != null) {
                return realmRegisteredInfo.isBot();
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GroupChatRole getGroupRole() {
        return (groupRole != null) ? GroupChatRole.valueOf(groupRole) : null;
    }

    public void setGroupRole(String groupRole) {
        this.groupRole = groupRole;
    }

    public ChannelChatRole getChannelRole() {
        return (channelRole != null) ? ChannelChatRole.valueOf(channelRole) : null;
    }

    public void setChannelRole(String channelRole) {
        this.channelRole = channelRole;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return id == ((Room)obj).getId();
    }
}
