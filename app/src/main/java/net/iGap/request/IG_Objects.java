package net.iGap.request;

import net.iGap.helper.FileLog;
import net.iGap.proto.ProtoChannelAvatarAdd;
import net.iGap.proto.ProtoChannelCreate;
import net.iGap.proto.ProtoChannelDelete;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupCreate;

public class IG_Objects {

    public static class Error extends AbstractObject {
        public static int actionId = 0;
        public int minor;
        public int major;

        @Override
        public void readParams(Object message) {
            ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;

            minor = errorResponse.getMinorCode();
            major = errorResponse.getMajorCode();

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

        public AbstractObject deserializeResponse(int constructor, Object protoObject) {
            return Res_Channel_Avatar.deserializeObject(constructor, protoObject);
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

        public static Res_Channel_Avatar deserializeObject(int constructor, Object protoObject) {
            if (constructor != actionId || protoObject == null) {
                return null;
            }

            Res_Channel_Avatar object = new Res_Channel_Avatar();
            object.readParams(protoObject);

            return object;
        }

        @Override
        public void readParams(Object message) {
            try {
                ProtoChannelAvatarAdd.ChannelAvatarAddResponse.Builder builder = (ProtoChannelAvatarAdd.ChannelAvatarAddResponse.Builder) message;

                roomId = builder.getRoomId();
                avatar = builder.getAvatar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Group_Create extends AbstractObject {
        public static int actionId = 300;
        public String name;
        public String description;

        public Res_Group_Create deserializeResponse(int constructor, Object protoObject) {
            return Res_Group_Create.deserializeObject(constructor, protoObject);
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

        public static Res_Group_Create deserializeObject(int constructor, Object protoObject) {
            if (constructor != actionId || protoObject == null) {
                return null;
            }

            Res_Group_Create object = new Res_Group_Create();
            object.readParams(protoObject);

            return object;
        }

        @Override
        public void readParams(Object message) {
            try {
                ProtoGroupCreate.GroupCreateResponse.Builder builder = (ProtoGroupCreate.GroupCreateResponse.Builder) message;
                inviteLink = builder.getInviteLink();
                roomId = builder.getRoomId();
            } catch (Exception e) {
                e.printStackTrace();
            }

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

        public Res_Channel_Create deserializeResponse(int constructor, Object protoObject) {
            return Res_Channel_Create.deserializeObject(constructor, protoObject);
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

        public static Res_Channel_Create deserializeObject(int constructor, Object protoObject) {
            if (constructor != actionId || protoObject == null) {
                return null;
            }

            Res_Channel_Create object = new Res_Channel_Create();
            object.readParams(protoObject);

            return object;
        }

        @Override
        public void readParams(Object message) {
            try {
                ProtoChannelCreate.ChannelCreateResponse.Builder builder = (ProtoChannelCreate.ChannelCreateResponse.Builder) message;
                inviteLink = builder.getInviteLink();
                roomId = builder.getRoomId();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Channel_Delete extends AbstractObject {
        public static int actionId = 404;

        public long roomId;

        public Res_Channel_Delete deserializeResponse(int constructor, Object protoObject) {
            return Res_Channel_Delete.deserializeObject(constructor, protoObject);
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

        public static Res_Channel_Delete deserializeObject(int constructor, Object protoObject) {
            if (constructor != actionId || protoObject == null) {
                return null;
            }

            Res_Channel_Delete object = new Res_Channel_Delete();
            object.readParams(protoObject);

            return object;
        }

        @Override
        public void readParams(Object message) {
            try {
                ProtoChannelDelete.ChannelDeleteResponse.Builder builder = (ProtoChannelDelete.ChannelDeleteResponse.Builder) message;
                roomId = builder.getRoomId();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

}
