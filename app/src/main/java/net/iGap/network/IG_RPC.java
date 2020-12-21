package net.iGap.network;

import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.helper.FileLog;
import net.iGap.proto.ProtoChannelAvatarAdd;
import net.iGap.proto.ProtoChannelCreate;
import net.iGap.proto.ProtoChannelDelete;
import net.iGap.proto.ProtoChannelEditMessage;
import net.iGap.proto.ProtoChatEditMessage;
import net.iGap.proto.ProtoClientGetDiscovery;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupCreate;
import net.iGap.proto.ProtoGroupEditMessage;
import net.iGap.proto.ProtoInfoConfig;

import java.util.ArrayList;
import java.util.HashMap;

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

}
