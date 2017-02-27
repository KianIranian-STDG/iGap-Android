package com.iGap.interfaces;

import com.iGap.proto.ProtoClientSearchUsername;

/**
 * Created by android3 on 2/27/2017.
 */

public interface IClientSearchUserName {

    void OnGetList(ProtoClientSearchUsername.ClientSearchUsernameResponse.Builder builderList);
}
