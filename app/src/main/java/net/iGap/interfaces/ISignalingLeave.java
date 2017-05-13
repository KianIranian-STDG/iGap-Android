package net.iGap.interfaces;

/**
 * Created by android3 on 5/8/2017.
 */

public interface ISignalingLeave {

    void onLeave(net.iGap.proto.ProtoSignalingLeave.SignalingLeaveResponse.Type type);
}
