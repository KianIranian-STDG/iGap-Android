package net.iGap.network;

import net.iGap.helper.FileLog;
import net.iGap.proto.ProtoChannelAvatarAdd;
import net.iGap.proto.ProtoChannelCreate;
import net.iGap.proto.ProtoChannelDelete;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupCreate;

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

}