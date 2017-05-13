package net.iGap.interfaces;

/**
 * Created by android3 on 5/8/2017.
 */

public interface ISignalingOffer {

    void onOffer(long called_userId, net.iGap.proto.ProtoSignalingOffer.SignalingOffer.Type type, String callerSdp);
}
