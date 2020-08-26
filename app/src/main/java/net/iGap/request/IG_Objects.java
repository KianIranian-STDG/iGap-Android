package net.iGap.request;

import net.iGap.proto.ProtoChannelAvatarAdd;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGlobal;

public class IG_Objects {

    public static class Error extends AbstractObject {
        public static int actionId = 0;
        int minor;
        int major;

        @Override
        public void readParams(Object message) {
            ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;

            minor = errorResponse.getMinorCode();
            major = errorResponse.getMajorCode();

        }
    }

    public static class ChannelAddAvatar extends AbstractObject {
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

    public static class ChannelAvatar extends AbstractObject {
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

    public static class AddAvatar extends AbstractObject {
        @Override
        public AbstractObject deserializeResponse(int constructor, Object protoObject) {
            if (protoObject == null) {
                return null;
            }

            AbstractObject object = null;
            if (constructor == 30412) {
                object = new ChannelAvatar();
                object.readParams(protoObject);
            }

            return object;
        }
    }
}
