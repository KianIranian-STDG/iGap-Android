package net.iGap.network;

import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.helper.FileLog;
import net.iGap.proto.ProtoChannelAddMessageReaction;
import net.iGap.proto.ProtoChannelAvatarAdd;
import net.iGap.proto.ProtoChannelCreate;
import net.iGap.proto.ProtoChannelDelete;
import net.iGap.proto.ProtoChannelDeleteMessage;
import net.iGap.proto.ProtoChannelEditMessage;
import net.iGap.proto.ProtoChannelGetMessagesStats;
import net.iGap.proto.ProtoChannelPinMessage;
import net.iGap.proto.ProtoChannelUpdateReactionStatus;
import net.iGap.proto.ProtoChannelUpdateSignature;
import net.iGap.proto.ProtoChatClearMessage;
import net.iGap.proto.ProtoChatDelete;
import net.iGap.proto.ProtoChatDeleteMessage;
import net.iGap.proto.ProtoChatEditMessage;
import net.iGap.proto.ProtoChatUpdateStatus;
import net.iGap.proto.ProtoClientGetDiscovery;
import net.iGap.proto.ProtoClientMuteRoom;
import net.iGap.proto.ProtoClientPinRoom;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupClearMessage;
import net.iGap.proto.ProtoGroupCreate;
import net.iGap.proto.ProtoGroupDelete;
import net.iGap.proto.ProtoGroupDeleteMessage;
import net.iGap.proto.ProtoGroupEditMessage;
import net.iGap.proto.ProtoGroupLeft;
import net.iGap.proto.ProtoGroupPinMessage;
import net.iGap.proto.ProtoGroupUpdateStatus;
import net.iGap.proto.ProtoInfoConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class IG_RPC {

    public static class Error extends AbstractObject {
        public static int actionId = 0;
        public int minor;
        public int major;

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoError.ErrorResponse response = ProtoError.ErrorResponse.parseFrom(message);
            resId = response.getResponse().getId();
            minor = response.getMinorCode();
            major = response.getMajorCode();
        }
    }

    public static class Group_Update_Status extends AbstractObject {
        public static int actionId = 311;
        public long roomId;
        public long messageId;
        public ProtoGlobal.RoomMessageStatus roomMessageStatus;

        @Override

        public int getActionId() {
            return actionId;
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return Res_Group_Update_Status.deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoGroupUpdateStatus.GroupUpdateStatus.Builder builder = ProtoGroupUpdateStatus.GroupUpdateStatus.newBuilder();
            builder.setRoomId(roomId);
            builder.setMessageId(messageId);
            builder.setStatus(roomMessageStatus);
            return builder;
        }
    }

    public static class Res_Group_Update_Status extends AbstractObject {
        public static int actionId = 30311;
        public String updaterAuthorHash;
        public long roomId;
        public long messageId;
        public long statusVersion;
        public ProtoGlobal.RoomMessageStatus statusValue;

        public static Res_Group_Update_Status deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Group_Update_Status object = null;
            try {
                object = new Res_Group_Update_Status();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoGroupUpdateStatus.GroupUpdateStatusResponse response = ProtoGroupUpdateStatus.GroupUpdateStatusResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
            messageId = response.getMessageId();
            updaterAuthorHash = response.getUpdaterAuthorHash();
            statusValue = response.getStatus();
            statusVersion = response.getStatusVersion();
        }
    }

    public static class Chat_Update_Status extends AbstractObject {
        public static int actionId = 202;
        public long roomId;
        public long messageId;
        public ProtoGlobal.RoomMessageStatus roomMessageStatus;

        @Override

        public int getActionId() {
            return actionId;
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return Res_Chat_Update_Status.deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoChatUpdateStatus.ChatUpdateStatus.Builder builder = ProtoChatUpdateStatus.ChatUpdateStatus.newBuilder();
            builder.setRoomId(roomId);
            builder.setMessageId(messageId);
            builder.setStatus(roomMessageStatus);
            return builder;
        }
    }

    public static class Res_Chat_Update_Status extends AbstractObject {
        public static int actionId = 30202;
        public String updaterAuthorHash;
        public long roomId;
        public long messageId;
        public long statusVersion;
        public ProtoGlobal.RoomMessageStatus statusValue;

        public static Res_Chat_Update_Status deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Chat_Update_Status object = null;
            try {
                object = new Res_Chat_Update_Status();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoChatUpdateStatus.ChatUpdateStatusResponse response = ProtoChatUpdateStatus.ChatUpdateStatusResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
            messageId = response.getMessageId();
            updaterAuthorHash = response.getUpdaterAuthorHash();
            statusValue = response.getStatus();
            statusVersion = response.getStatusVersion();
        }
    }

    public static class Group_Clear_History extends AbstractObject {
        public static int actionId = 304;
        public long roomId;
        public long lastMessageId;

        @Override
        public int getActionId() {
            return actionId;
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return Res_Group_Clear_History.deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoGroupClearMessage.GroupClearMessage.Builder builder = ProtoGroupClearMessage.GroupClearMessage.newBuilder();
            builder.setRoomId(roomId);
            builder.setClearId(lastMessageId);

            return builder;
        }
    }

    public static class Res_Group_Clear_History extends AbstractObject {
        public static int actionId = 30304;
        public long roomId;
        public long clearId;


        public static Res_Group_Clear_History deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Group_Clear_History object = null;
            try {
                object = new Res_Group_Clear_History();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoGroupClearMessage.GroupClearMessageResponse response = ProtoGroupClearMessage.GroupClearMessageResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
            clearId = response.getClearId();
        }
    }

    public static class Chat_Clear_History extends AbstractObject {
        public static int actionId = 205;
        public long roomId;
        public long lastMessageId;

        @Override
        public int getActionId() {
            return actionId;
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return Res_Chat_Clear_History.deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoChatClearMessage.ChatClearMessage.Builder builder = ProtoChatClearMessage.ChatClearMessage.newBuilder();
            builder.setRoomId(roomId);
            builder.setClearId(lastMessageId);

            return builder;
        }
    }

    public static class Res_Chat_Clear_History extends AbstractObject {
        public static int actionId = 30205;
        public long roomId;
        public long clearId;


        public static Res_Chat_Clear_History deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Chat_Clear_History object = null;
            try {
                object = new Res_Chat_Clear_History();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoChatClearMessage.ChatClearMessageResponse response = ProtoChatClearMessage.ChatClearMessageResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
            clearId = response.getClearId();
        }
    }

    public static class Channel_AddAvatar extends AbstractObject {
        public static int actionId = 412;
        public long roomId;
        public String attachment;

        @Override
        public int getActionId() {
            return actionId;
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return Res_Channel_Avatar.deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoChannelAvatarAdd.ChannelAvatarAdd.Builder builder = ProtoChannelAvatarAdd.ChannelAvatarAdd.newBuilder();

            builder.setRoomId(roomId);
            builder.setAttachment(attachment);

            return builder;
        }
    }

    public static class Res_Channel_Avatar extends AbstractObject {
        public static int actionId = 30412;
        public long roomId;
        public ProtoGlobal.Avatar avatar;


        public static Res_Channel_Avatar deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Channel_Avatar object = null;
            try {
                object = new Res_Channel_Avatar();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoChannelAvatarAdd.ChannelAvatarAddResponse response = ProtoChannelAvatarAdd.ChannelAvatarAddResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
            avatar = response.getAvatar();
        }
    }

    public static class Group_Create extends AbstractObject {
        public static int actionId = 300;
        public String name;
        public String description;

        @Override
        public Res_Group_Create deserializeResponse(int constructor, byte[] message) {
            return Res_Group_Create.deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoGroupCreate.GroupCreate.Builder builder = ProtoGroupCreate.GroupCreate.newBuilder();

            builder.setName(name);
            builder.setDescription(description.trim());

            return builder;
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Res_Group_Create extends AbstractObject {
        public static int actionId = 30300;

        public String inviteLink;
        public long roomId;

        public static Res_Group_Create deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }
            Res_Group_Create object = null;
            try {
                object = new Res_Group_Create();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoGroupCreate.GroupCreateResponse response = ProtoGroupCreate.GroupCreateResponse.parseFrom(message);
            resId = response.getResponse().getId();
            inviteLink = response.getInviteLink();
            roomId = response.getRoomId();
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Channel_Create extends AbstractObject {
        public static int actionId = 400;
        public String name;
        public String description;

        @Override
        public Res_Channel_Create deserializeResponse(int constructor, byte[] message) {
            return Res_Channel_Create.deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoChannelCreate.ChannelCreate.Builder builder = ProtoChannelCreate.ChannelCreate.newBuilder();

            builder.setName(name);
            builder.setDescription(description.trim());

            return builder;
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Res_Channel_Create extends AbstractObject {
        public static int actionId = 30400;

        public String inviteLink;
        public long roomId;

        public static Res_Channel_Create deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Channel_Create object = null;
            try {
                object = new Res_Channel_Create();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoChannelCreate.ChannelCreateResponse response = ProtoChannelCreate.ChannelCreateResponse.parseFrom(message);
            resId = response.getResponse().getId();
            inviteLink = response.getInviteLink();
            roomId = response.getRoomId();
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Channel_Delete extends AbstractObject {
        public static int actionId = 404;

        public long roomId;

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return Res_Channel_Delete.deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoChannelDelete.ChannelDelete.Builder builder = ProtoChannelDelete.ChannelDelete.newBuilder();
            builder.setRoomId(roomId);

            return builder;
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Res_Channel_Delete extends AbstractObject {
        public static int actionId = 30404;
        public long roomId;

        public static AbstractObject deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Channel_Delete object = null;
            try {
                object = new Res_Channel_Delete();
                object.readParams(message);
            } catch (Exception e) {
                FileLog.e(e);
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoChannelDelete.ChannelDeleteResponse response = ProtoChannelDelete.ChannelDeleteResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
        }

        @Override
        public int getActionId() {
            return actionId;
        }

    }

    public static class InfoConfig extends AbstractObject {
        public static final int actionId = 506;

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return Res_Info_Config.deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            return ProtoInfoConfig.InfoConfig.newBuilder();
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Res_Info_Config extends AbstractObject {
        public static final int actionId = 30506;

        public long captionLengthMax;
        public long channelAddMemberLimit;
        public boolean debugMode;
        public long defaultTimeout;
        public long groupAddMemberLimit;
        public long maxFileSize;
        public long messageLengthMax;
        public boolean optimizeMode;
        public String servicesBaseUrl;
        public int fileGateway;
        public boolean showAdvertisement;
        public int defaultTab;
        public HashMap<String, Integer> microServices = new HashMap<>();

        public static Res_Info_Config deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Info_Config object = null;
            try {
                object = new Res_Info_Config();
                object.readParams(message);
            } catch (Exception e) {
                FileLog.e(e);
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoInfoConfig.InfoConfigResponse response = ProtoInfoConfig.InfoConfigResponse.parseFrom(message);

            resId = response.getResponse().getId();
            servicesBaseUrl = response.getBaseUrl();
            captionLengthMax = response.getCaptionLengthMax();
            channelAddMemberLimit = response.getChannelAddMemberLimit();
            debugMode = response.getDebugMode();
            defaultTimeout = response.getDefaultTimeout();
            maxFileSize = response.getMaxFileSize();
            optimizeMode = response.getOptimizeMode();
            showAdvertisement = response.getShowAdvertising();
            defaultTab = response.getDefaultTabValue();

            for (int i = 0; i < response.getMicroServiceCount(); i++) {
                ProtoInfoConfig.MicroService microService = response.getMicroServiceList().get(i);
                microServices.put(microService.getName(), microService.getTypeValue());
            }

            Integer file = microServices.get("file");
            if (file != null) {
                fileGateway = file;
            }
        }
    }

    public static class Client_Get_Discovery extends AbstractObject {
        public static final int actionId = 620;

        public int itemId;

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Client_Get_Discovery().deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoClientGetDiscovery.ClientGetDiscovery.Builder builder = ProtoClientGetDiscovery.ClientGetDiscovery.newBuilder();
            builder.setItemId(itemId);
            return builder;
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Res_Client_Get_Discovery extends AbstractObject {
        public static final int actionId = 30620;

        public ArrayList<DiscoveryItem> items = new ArrayList<>();


        public Res_Client_Get_Discovery deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Client_Get_Discovery object = null;
            try {
                object = new Res_Client_Get_Discovery();
                object.readParams(message);
            } catch (Exception e) {
                FileLog.e(e);
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoClientGetDiscovery.ClientGetDiscoveryResponse response = ProtoClientGetDiscovery.ClientGetDiscoveryResponse.parseFrom(message);
            resId = response.getResponse().getId();

            for (ProtoGlobal.Discovery discovery : response.getDiscoveriesList()) {
                items.add(new DiscoveryItem(discovery));
            }

        }
    }

    public static class Chat_edit_message extends AbstractObject {
        public static final int actionId = 203;

        public long roomId;
        public long messageId;
        public String message;

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Chat_Edit_Message().deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoChatEditMessage.ChatEditMessage.Builder builder = ProtoChatEditMessage.ChatEditMessage.newBuilder();
            builder.setMessage(message);
            builder.setMessageId(messageId);
            builder.setRoomId(roomId);
            return builder;
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Res_Chat_Edit_Message extends AbstractObject {
        public static final int actionId = 30203;

        public String newMessage;
        public long messageId;
        public int messageType;
        public long messageVersion;
        public long roomId;

        public Res_Chat_Edit_Message deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Chat_Edit_Message object = null;
            try {
                object = new Res_Chat_Edit_Message();
                object.readParams(message);
            } catch (Exception e) {
                FileLog.e(e);
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoChatEditMessage.ChatEditMessageResponse response = ProtoChatEditMessage.ChatEditMessageResponse.parseFrom(message);
            resId = response.getResponse().getId();

            newMessage = response.getMessage();
            messageId = response.getMessageId();
            messageType = response.getMessageTypeValue();
            messageVersion = response.getMessageVersion();
            roomId = response.getRoomId();
        }
    }


    public static class Group_edit_message extends AbstractObject {
        public static final int actionId = 325;

        public long roomId;
        public long messageId;
        public String message;

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Group_Edit_Message().deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoGroupEditMessage.GroupEditMessage.Builder builder = ProtoGroupEditMessage.GroupEditMessage.newBuilder();
            builder.setMessage(message);
            builder.setMessageId(messageId);
            builder.setRoomId(roomId);
            return builder;
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Res_Group_Edit_Message extends AbstractObject {
        public static final int actionId = 30325;

        public String newMessage;
        public long messageId;
        public int messageType;
        public long messageVersion;
        public long roomId;

        public Res_Group_Edit_Message deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Group_Edit_Message object = null;
            try {
                object = new Res_Group_Edit_Message();
                object.readParams(message);
            } catch (Exception e) {
                FileLog.e(e);
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoGroupEditMessage.GroupEditMessageResponse response = ProtoGroupEditMessage.GroupEditMessageResponse.parseFrom(message);
            resId = response.getResponse().getId();

            newMessage = response.getMessage();
            messageId = response.getMessageId();
            messageType = response.getMessageTypeValue();
            messageVersion = response.getMessageVersion();
            roomId = response.getRoomId();
        }
    }

    public static class Channel_edit_message extends AbstractObject {
        public static final int actionId = 425;

        public long roomId;
        public long messageId;
        public String message;

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Channel_Edit_Message().deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoChatEditMessage.ChatEditMessage.Builder builder = ProtoChatEditMessage.ChatEditMessage.newBuilder();
            builder.setMessage(message);
            builder.setMessageId(messageId);
            builder.setRoomId(roomId);
            return builder;
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Res_Channel_Edit_Message extends AbstractObject {
        public static final int actionId = 30425;

        public String newMessage;
        public long messageId;
        public int messageType;
        public long messageVersion;
        public long roomId;

        public Res_Channel_Edit_Message deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Channel_Edit_Message object = null;
            try {
                object = new Res_Channel_Edit_Message();
                object.readParams(message);
            } catch (Exception e) {
                FileLog.e(e);
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoChannelEditMessage.ChannelEditMessageResponse response = ProtoChannelEditMessage.ChannelEditMessageResponse.parseFrom(message);
            resId = response.getResponse().getId();

            newMessage = response.getMessage();
            messageId = response.getMessageId();
            messageType = response.getMessageTypeValue();
            messageVersion = response.getMessageVersion();
            roomId = response.getRoomId();
        }
    }

    public static class Group_pin_message extends AbstractObject {
        public static final int actionId = 326;

        public long roomId;
        public long messageId;

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Group_pin_message_response().deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoGroupPinMessage.GroupPinMessage.Builder builder = ProtoGroupPinMessage.GroupPinMessage.newBuilder();
            builder.setRoomId(roomId);
            builder.setMessageId(messageId);
            return builder;
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Group_pin_message_response extends AbstractObject {
        public static final int actionId = 30326;

        public ProtoGlobal.RoomMessage pinnedMessage;
        public long roomId;

        public Group_pin_message_response deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Group_pin_message_response object = null;
            try {
                object = new Group_pin_message_response();
                object.readParams(message);
            } catch (Exception e) {
                FileLog.e(e);
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoGroupPinMessage.GroupPinMessageResponse response = ProtoGroupPinMessage.GroupPinMessageResponse.parseFrom(message);
            resId = response.getResponse().getId();

            pinnedMessage = response.getPinnedMessage();
            roomId = response.getRoomId();
        }

    }

    public static class Channel_pin_message extends AbstractObject {
        public static final int actionId = 427;

        public long roomId;
        public long messageId;

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Channel_pin_message_response().deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoChannelPinMessage.ChannelPinMessage.Builder builder = ProtoChannelPinMessage.ChannelPinMessage.newBuilder();
            builder.setRoomId(roomId);
            builder.setMessageId(messageId);
            return builder;
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Channel_pin_message_response extends AbstractObject {
        public static final int actionId = 30427;

        public ProtoGlobal.RoomMessage pinnedMessage;
        public long roomId;

        public Channel_pin_message_response deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Channel_pin_message_response object = null;
            try {
                object = new Channel_pin_message_response();
                object.readParams(message);
            } catch (Exception e) {
                FileLog.e(e);
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoChannelPinMessage.ChannelPinMessageResponse response = ProtoChannelPinMessage.ChannelPinMessageResponse.parseFrom(message);
            resId = response.getResponse().getId();

            pinnedMessage = response.getPinnedMessage();
            roomId = response.getRoomId();
        }
    }

    public static class Channel_Delete_Message extends AbstractObject {

        public static int actionId = 411;
        public long roomId;
        public long messageId;

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Channel_Delete_Message().deserializeResponse(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoChannelDeleteMessage.ChannelDeleteMessage.Builder builder = ProtoChannelDeleteMessage.ChannelDeleteMessage.newBuilder();
            builder.setRoomId(roomId);
            builder.setMessageId(messageId);
            return builder;
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Res_Channel_Delete_Message extends AbstractObject {

        public static int actionId = 30411;
        public long roomId;
        public long messageId;
        public long deleteVersion;

        @Override

        public AbstractObject deserializeResponse(int constructor, byte[] message) {

            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Channel_Delete_Message object = null;
            try {
                object = new Res_Channel_Delete_Message();
                object.readParams(message);
            } catch (Exception e) {
                FileLog.e(e);
            }
            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoChannelDeleteMessage.ChannelDeleteMessageResponse response = ProtoChannelDeleteMessage.ChannelDeleteMessageResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
            messageId = response.getMessageId();
            deleteVersion = response.getDeleteVersion();
        }
    }

    public static class Chat_Delete_Message extends AbstractObject {

        public static final int actionId = 204;
        public long roomId;
        public long messageId;
        public boolean both;

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Chat_Delete_Message().deserializeResponse(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoChatDeleteMessage.ChatDeleteMessage.Builder builder = ProtoChatDeleteMessage.ChatDeleteMessage.newBuilder();
            builder.setRoomId(roomId);
            builder.setMessageId(messageId);
            builder.setBoth(both);
            return builder;
        }

        @Override
        public int getActionId() {
            return actionId;
        }

    }

    public static class Res_Chat_Delete_Message extends AbstractObject {
        public static int actionId = 30204;
        public long roomId;
        public long messageId;
        public long deleteVersion;

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {

            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Chat_Delete_Message object = null;
            try {
                object = new Res_Chat_Delete_Message();
                object.readParams(message);
            } catch (Exception e) {
                FileLog.e(e);
            }
            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoChatDeleteMessage.ChatDeleteMessageResponse response = ProtoChatDeleteMessage.ChatDeleteMessageResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
            messageId = response.getMessageId();
            deleteVersion = response.getDeleteVersion();
        }
    }

    public static class Group_Delete_Message extends AbstractObject {

        public static int actionId = 320;
        public long roomId;
        public long messageId;

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Group_Delete_Message().deserializeResponse(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoGroupDeleteMessage.GroupDeleteMessage.Builder builder = ProtoGroupDeleteMessage.GroupDeleteMessage.newBuilder();
            builder.setRoomId(roomId);
            builder.setMessageId(messageId);
            return builder;
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Res_Group_Delete_Message extends AbstractObject {

        public static int actionId = 30320;
        public long roomId;
        public long messageId;
        public long deleteVersion;

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Group_Delete_Message object = null;
            try {
                object = new Res_Group_Delete_Message();
                object.readParams(message);
            } catch (Exception e) {
                FileLog.e(e);
            }
            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoGroupDeleteMessage.GroupDeleteMessageResponse response = ProtoGroupDeleteMessage.GroupDeleteMessageResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
            messageId = response.getMessageId();
            deleteVersion = response.getDeleteVersion();
        }
    }

    public static class Channel_Add_Message_Reaction extends AbstractObject {

        public static int actionId = 424;
        public long roomId;
        public long messageId;
        public ProtoGlobal.RoomMessageReaction reaction;


        @Override
        public Object getProtoObject() {
            ProtoChannelAddMessageReaction.ChannelAddMessageReaction.Builder builder = ProtoChannelAddMessageReaction.ChannelAddMessageReaction.newBuilder();
            builder.setRoomId(roomId);
            builder.setMessageId(messageId);
            builder.setReaction(reaction);
            return builder;
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Channel_Add_Message_Reaction().deserializeResponse(constructor, message);
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Res_Channel_Add_Message_Reaction extends AbstractObject {

        public static int actionId = 30424;
        public String reactionCounter;

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoChannelAddMessageReaction.ChannelAddMessageReactionResponse response = ProtoChannelAddMessageReaction.ChannelAddMessageReactionResponse.parseFrom(message);
            resId = response.getResponse().getId();
            reactionCounter = response.getReactionCounterLabel();
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {

            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Channel_Add_Message_Reaction object = null;
            try {
                object = new Res_Channel_Add_Message_Reaction();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return object;
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Channel_Get_Message_Reaction extends AbstractObject {

        public static int actionId = 423;
        public long roomId;
        public HashSet<Long> messageIds;

        @Override
        public Object getProtoObject() {
            ProtoChannelGetMessagesStats.ChannelGetMessagesStats.Builder builder = ProtoChannelGetMessagesStats.ChannelGetMessagesStats.newBuilder();
            builder.setRoomId(roomId);
            builder.addAllMessageId(messageIds);
            return builder;
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Channel_Get_Message_Reaction().deserializeResponse(constructor, message);
        }

        @Override
        public int getActionId() {
            return actionId;
        }

    }

    public static class Res_Channel_Get_Message_Reaction extends AbstractObject {

        public static int actionId;
        public List<ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Stats> states;

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;


            }

            Res_Channel_Get_Message_Reaction object = null;
            try {
                object = new Res_Channel_Get_Message_Reaction();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();

            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse response = ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.parseFrom(message);
            resId = response.getResponse().getId();
            states = response.getStatsList();
        }

        @Override
        public int getActionId() {
            return actionId;
        }

    }

    public static class Channel_Update_Reaction_Status extends AbstractObject {
        public int actionId = 426;
        public long roomId;
        public boolean reactionStatus;

        @Override
        public Object getProtoObject() {
            ProtoChannelUpdateReactionStatus.ChannelUpdateReactionStatus.Builder builder = ProtoChannelUpdateReactionStatus.ChannelUpdateReactionStatus.newBuilder();
            builder.setRoomId(roomId);
            builder.setReactionStatus(reactionStatus);
            return builder;
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Channel_Update_Reaction_Status().deserializeResponse(constructor, message);
        }


        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Res_Channel_Update_Reaction_Status extends AbstractObject {

        public static int actionId = 30426;
        public long roomId;
        public boolean reactionStatus;

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoChannelUpdateReactionStatus.ChannelUpdateReactionStatusResponse response = ProtoChannelUpdateReactionStatus.ChannelUpdateReactionStatusResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
            reactionStatus = response.getReactionStatus();
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Channel_Update_Reaction_Status object = null;
            try {
                object = new Res_Channel_Update_Reaction_Status();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();

            }

            return object;
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Channel_Update_Signature extends AbstractObject {

        public int actionId = 422;
        public long roomId;
        public boolean signature;

        @Override
        public Object getProtoObject() {
            ProtoChannelUpdateSignature.ChannelUpdateSignature.Builder builder = ProtoChannelUpdateSignature.ChannelUpdateSignature.newBuilder();
            builder.setRoomId(roomId);
            builder.setSignature(signature);
            return builder;
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Channel_Update_Signature().deserializeResponse(constructor, message);
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Res_Channel_Update_Signature extends AbstractObject {

        public static int actionId = 30422;
        public long roomId;
        public boolean signature;

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoChannelUpdateSignature.ChannelUpdateSignatureResponse response = ProtoChannelUpdateSignature.ChannelUpdateSignatureResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
            signature = response.getSignature();
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Channel_Update_Signature object = null;
            try {
                object = new Res_Channel_Update_Signature();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();

            }

            return object;
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Client_Pin_Room extends AbstractObject {

        public int actionId = 615;
        public long roomId;
        public boolean pin;

        @Override
        public Object getProtoObject() {
            ProtoClientPinRoom.ClientPinRoom.Builder builder = ProtoClientPinRoom.ClientPinRoom.newBuilder();
            builder.setRoomId(roomId);
            builder.setPin(pin);
            return builder;
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Client_Pin_Room().deserializeResponse(constructor, message);
        }

        @Override
        public int getActionId() {
            return actionId;
        }

    }

    public static class Res_Client_Pin_Room extends AbstractObject {

        public static int actionId = 30615;
        public long roomId;
        public long pinId;

        @Override
        public int getActionId() {
            return actionId;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoClientPinRoom.ClientPinRoomResponse response = ProtoClientPinRoom.ClientPinRoomResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
            pinId = response.getPinId();
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {

            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Client_Pin_Room object = null;
            try {
                object = new Res_Client_Pin_Room();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return object;
        }
    }

    public static class Client_Mute_Room extends AbstractObject {

        public int actionId = 614;
        public long roomId;
        public ProtoGlobal.RoomMute roomMute;

        @Override
        public Object getProtoObject() {
            ProtoClientMuteRoom.ClientMuteRoom.Builder builder = ProtoClientMuteRoom.ClientMuteRoom.newBuilder();
            builder.setRoomId(roomId);
            builder.setRoomMute(roomMute);
            return builder;
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Client_Mute_Room().deserializeResponse(constructor, message);
        }

        @Override
        public int getActionId() {
            return actionId;
        }

    }

    public static class Res_Client_Mute_Room extends AbstractObject {

        public static int actionId = 30614;
        public long roomId;
        public ProtoGlobal.RoomMute roomMute;

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoClientMuteRoom.ClientMuteRoomResponse response = ProtoClientMuteRoom.ClientMuteRoomResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
            roomMute = response.getRoomMute();
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {

            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Client_Mute_Room object = null;

            try {
                object = new Res_Client_Mute_Room();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return object;
        }

        @Override
        public int getActionId() {
            return actionId;
        }

    }

    public static class Chat_Delete_Room extends AbstractObject {

        public int actionId = 206;
        public long roomId;

        @Override
        public Object getProtoObject() {
            ProtoChatDelete.ChatDelete.Builder builder = ProtoChatDelete.ChatDelete.newBuilder();
            builder.setRoomId(roomId);
            return builder;
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Chat_Delete_Room().deserializeResponse(constructor, message);
        }

        @Override
        public int getActionId() {
            return actionId;
        }

    }

    public static class Res_Chat_Delete_Room extends AbstractObject {

        public static int actionId = 30206;
        public long roomId;

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoChatDelete.ChatDeleteResponse response = ProtoChatDelete.ChatDeleteResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {

            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Chat_Delete_Room object = null;

            try {
                object = new Res_Chat_Delete_Room();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return object;
        }

        @Override
        public int getActionId() {
            return actionId;
        }

    }

    public static class Group_Delete_Room extends AbstractObject {

        public int actionId = 318;
        public long roomId;

        @Override
        public Object getProtoObject() {
            ProtoGroupDelete.GroupDelete.Builder builder = ProtoGroupDelete.GroupDelete.newBuilder();
            builder.setRoomId(roomId);
            return builder;
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Group_Delete_Room().deserializeResponse(constructor, message);
        }

        @Override
        public int getActionId() {
            return actionId;
        }

    }

    public static class Res_Group_Delete_Room extends AbstractObject {

        public static int actionId = 30318;
        public long roomId;

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoGroupDelete.GroupDeleteResponse response = ProtoGroupDelete.GroupDeleteResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {

            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Group_Delete_Room object = null;
            try {
                object = new Res_Group_Delete_Room();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return object;
        }

        @Override
        public int getActionId() {
            return actionId;
        }

    }

    public static class Group_Left extends AbstractObject {

        public int actionId = 309;
        public long roomId;

        @Override
        public Object getProtoObject() {
            ProtoGroupLeft.GroupLeft.Builder builder = ProtoGroupLeft.GroupLeft.newBuilder();
            builder.setRoomId(roomId);
            return builder;
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return new Res_Group_Left().deserializeResponse(constructor, message);
        }

        @Override
        public int getActionId() {
            return actionId;
        }

    }

    public static class Res_Group_Left extends AbstractObject {

        public static int actionId = 30309;
        public long roomId;
        public long memberId;

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoGroupLeft.GroupLeftResponse response = ProtoGroupLeft.GroupLeftResponse.parseFrom(message);
            resId = response.getResponse().getId();
            roomId = response.getRoomId();
            memberId = response.getMemberId();
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_Group_Left object = null;
            try {
                object = new Res_Group_Left();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return object;
        }

        @Override
        public int getActionId() {
            return actionId;
        }

    }
}
