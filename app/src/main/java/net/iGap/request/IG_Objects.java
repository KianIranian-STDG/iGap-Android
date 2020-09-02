package net.iGap.request;

import androidx.annotation.Nullable;

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

    public static class Req_Channel_AddAvatar extends AbstractObject {
        public static int actionId = 412;
        public long roomId;
        public String attachment;

        @Override
        public int getActionId() {
            return actionId;
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

    public static class IG_AddAvatar extends AbstractObject {
        @Override
        public AbstractObject deserializeResponse(int constructor, Object protoObject) {
            if (protoObject == null) {
                return null;
            }

            AbstractObject object = null;
            if (constructor == 30412) {
                object = new Res_Channel_Avatar();
                object.readParams(protoObject);
            }

            return object;
        }
    }

    public static class Req_Group_Create extends AbstractObject {
        public static int actionId = 300;
        public String name;
        public String description;

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

    public static class IG_CreateRoomResponse extends AbstractObject {
        @Nullable
        @Override
        public AbstractObject deserializeResponse(int constructor, Object protoObject) {

            AbstractObject object = null;

            if (constructor == Res_Group_Create.actionId) {
                object = new Res_Group_Create();
                object.readParams(protoObject);
            } else if (constructor == Res_Channel_Create.actionId) {
                object = new Res_Channel_Create();
                object.readParams(protoObject);
            }

            return object;
        }
    }

    public static class Req_Channel_Create extends AbstractObject {
        public static int actionId = 400;
        public String name;
        public String description;

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

    public static class Req_Channel_Delete extends AbstractObject {
        public static int actionId = 404;

        public long roomId;

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

    public static class IG_DeleteRoomResponse extends AbstractObject {
        @Nullable
        @Override
        public AbstractObject deserializeResponse(int constructor, Object protoObject) {
            AbstractObject object = null;

            if (constructor == 30404) {
                object = new Res_Channel_Delete();
                object.readParams(protoObject);
            }

            return object;
        }
    }

}
