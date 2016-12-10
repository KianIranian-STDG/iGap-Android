package com.iGap.interfaces;

import com.iGap.proto.ProtoChannelGetMemberList;

import java.util.List;

public interface OnChannelGetMemberList {

    void onChannelGetMemberList(List<ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member> members);

    void onError(int majorCode, int minorCode);

    void onTimeOut();
}
