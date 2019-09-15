package net.iGap.interfaces;

import net.iGap.proto.ProtoClientGetPromote;

public interface Ipromote {
    void onGetPromoteResponse(ProtoClientGetPromote.ClientGetPromoteResponse.Builder builder);

}
