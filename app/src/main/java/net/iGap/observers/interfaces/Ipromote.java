package net.iGap.observers.interfaces;

import net.iGap.proto.ProtoClientGetPromote;

public interface Ipromote {
    void onGetPromoteResponse(ProtoClientGetPromote.ClientGetPromoteResponse.Builder builder);

}
