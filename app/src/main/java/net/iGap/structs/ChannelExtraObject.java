package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmChannelExtra;

public class ChannelExtraObject {
    public long messageId;
    public String signature;
    public String viewsLabel;
    public String thumbsUp;
    public String thumbsDown;

    public static ChannelExtraObject create(RealmChannelExtra realmChannelExtra) {
        ChannelExtraObject channelExtraObject = new ChannelExtraObject();
        channelExtraObject.messageId = realmChannelExtra.getMessageId();
        channelExtraObject.signature = realmChannelExtra.getSignature();
        channelExtraObject.viewsLabel = realmChannelExtra.getViewsLabel();
        channelExtraObject.thumbsUp = realmChannelExtra.getThumbsUp();
        channelExtraObject.thumbsDown = realmChannelExtra.getThumbsDown();
        return channelExtraObject;
    }

    public static ChannelExtraObject create(ProtoGlobal.RoomMessage.ChannelExtra protoChannelExtra, long messageId) {
        ChannelExtraObject channelExtraObject = new ChannelExtraObject();
        channelExtraObject.messageId = messageId;
        channelExtraObject.signature = protoChannelExtra.getSignature();
        channelExtraObject.viewsLabel = protoChannelExtra.getViewsLabel();
        channelExtraObject.thumbsUp = protoChannelExtra.getThumbsUpLabel();
        channelExtraObject.thumbsDown = protoChannelExtra.getThumbsDownLabel();
        return channelExtraObject;

    }




}
