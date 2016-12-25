package com.iGap.helper;

import static com.iGap.G.lookupMap;

public class HelperFillLookUpClass {

    /**
     * fill static hashMap with actionId and proto class name
     */

    public static void fillLookUpClassArray() {

        lookupMap.put(0, "ProtoError.ErrorResponse");
        lookupMap.put(30001, "ProtoConnectionSecuring.ConnectionSecuringResponse");
        lookupMap.put(2, "Connection.Symmetric.Key");
        lookupMap.put(30002, "ProtoConnectionSecuring.ConnectionSymmetricKeyResponse");
        lookupMap.put(3, "Heartbeat");
        lookupMap.put(30003, "ProtoHeartbeat.HeartbeatResponse");

        // User 1xx , 301xx
        lookupMap.put(30100, "ProtoUserRegister.UserRegisterResponse");
        lookupMap.put(30101, "ProtoUserVerify.UserVerifyResponse");
        lookupMap.put(30102, "ProtoUserLogin.UserLoginResponse");
        lookupMap.put(30103, "ProtoUserProfileEmail.UserProfileSetEmailResponse");
        lookupMap.put(30104, "ProtoUserProfileGender.UserProfileSetGenderResponse");
        lookupMap.put(30105, "ProtoUserProfileNickname.UserProfileSetNicknameResponse");
        lookupMap.put(30106, "ProtoUserContactsImport.UserContactsImportResponse");
        lookupMap.put(30107, "ProtoUserContactsGetList.UserContactsGetListResponse");
        lookupMap.put(30108, "ProtoUserContactsDelete.UserContactsDeleteResponse");
        lookupMap.put(30109, "ProtoUserContactsEdit.UserContactsEditResponse");
        lookupMap.put(30110, "ProtoUserProfileGetEmail.UserProfileGetEmailResponse");
        lookupMap.put(30111, "ProtoUserProfileGetGender.UserProfileGetGenderResponse");
        lookupMap.put(30112, "ProtoUserProfileGetNickname.UserProfileGetNicknameResponse");
        lookupMap.put(30113, "ProtoUserUsernameToId.UserUsernameToIdResponse");
        lookupMap.put(30114, "ProtoUserAvatarAdd.UserAvatarAddResponse");
        lookupMap.put(30115, "ProtoUserAvatarDelete.UserAvatarDeleteResponse");
        lookupMap.put(30116, "ProtoUserAvatarGetList.UserAvatarGetListResponse");
        lookupMap.put(30117, "ProtoUserInfo.UserInfoResponse");
        lookupMap.put(30118, "ProtoUserGetDeleteToken.UserGetDeleteTokenResponse");
        lookupMap.put(30119, "ProtoUserDelete.UserDeleteResponse");
        lookupMap.put(30120, "ProtoUserProfileSetSelfRemove.UserProfileSetSelfRemoveResponse");
        lookupMap.put(30121, "ProtoUserProfileGetSelfRemove.UserProfileGetSelfRemoveResponse");
        lookupMap.put(30122, "ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse");
        lookupMap.put(30123, "ProtoUserProfileUpdateUsername.UserProfileUpdateUsernameResponse");
        lookupMap.put(30124, "ProtoUserUpdateStatus.UserUpdateStatusResponse");
        lookupMap.put(30125, "ProtoUserSessionGetActiveList.UserSessionGetActiveListResponse");
        lookupMap.put(30126, "ProtoUserSessionTerminate.UserSessionTerminateResponse");
        lookupMap.put(30127, "ProtoUserSessionLogout.UserSessionLogoutResponse");
        lookupMap.put(30128, "ProtoUserContactsBlock.UserContactsBlockResponse");
        lookupMap.put(30129, "ProtoUserContactsUnblock.UserContactsUnblockResponse");
        lookupMap.put(30130, "ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse");

        // Chat 2xx , 302xx
        lookupMap.put(30200, "ProtoChatGetRoom.ChatGetRoomResponse");
        lookupMap.put(30201, "ProtoChatSendMessage.ChatSendMessageResponse");
        lookupMap.put(30202, "ProtoChatUpdateStatus.ChatUpdateStatusResponse");
        lookupMap.put(30203, "ProtoChatEditMessage.ChatEditMessageResponse");
        lookupMap.put(30204, "ProtoChatDeleteMessage.ChatDeleteMessageResponse");
        lookupMap.put(30205, "ProtoChatClearMessage.ChatClearMessageResponse");
        lookupMap.put(30206, "ProtoChatDelete.ChatDeleteResponse");
        lookupMap.put(30207, "ProtoChatUpdateDraft.ChatUpdateDraftResponse");
        lookupMap.put(30208, "ProtoChatGetDraft.ChatGetDraftResponse");
        lookupMap.put(30209, "ProtoChatConvertToGroup.ChatConvertToGroupResponse");
        lookupMap.put(30210, "ProtoChatSetAction.ChatSetActionResponse");

        // Group 3xx , 303xx
        lookupMap.put(30300, "ProtoGroupCreate.GroupCreateResponse");
        lookupMap.put(30301, "ProtoGroupAddMember.GroupAddMemberResponse");
        lookupMap.put(30302, "ProtoGroupAddAdmin.GroupAddAdminResponse");
        lookupMap.put(30303, "ProtoGroupAddModerator.GroupAddModeratorResponse");
        lookupMap.put(30304, "ProtoGroupClearMessage.GroupClearMessageResponse");
        lookupMap.put(30305, "ProtoGroupEdit.GroupEditResponse");
        lookupMap.put(30306, "ProtoGroupKickAdmin.GroupKickAdminResponse");
        lookupMap.put(30307, "ProtoGroupKickMember.GroupKickMemberResponse");
        lookupMap.put(30308, "ProtoGroupKickModerator.GroupKickModeratorResponse");
        lookupMap.put(30309, "ProtoGroupLeft.GroupLeftResponse");
        lookupMap.put(30310, "ProtoGroupSendMessage.GroupSendMessageResponse");
        lookupMap.put(30311, "ProtoGroupUpdateStatus.GroupUpdateStatusResponse");
        lookupMap.put(30312, "ProtoGroupAvatarAdd.GroupAvatarAddResponse");
        lookupMap.put(30313, "ProtoGroupAvatarDelete.GroupAvatarDeleteResponse");
        lookupMap.put(30314, "ProtoGroupAvatarGetList.GroupAvatarGetListResponse");
        lookupMap.put(30315, "ProtoGroupUpdateDraft.GroupUpdateDraftResponse");
        lookupMap.put(30316, "ProtoGroupGetDraft.GroupGetDraftResponse");
        lookupMap.put(30317, "ProtoGroupGetMemberList.GroupGetMemberListResponse");
        lookupMap.put(30318, "ProtoGroupDelete.GroupDeleteResponse");
        lookupMap.put(30319, "ProtoGroupSetAction.GroupSetActionResponse");
        lookupMap.put(30320, "ProtoGroupDeleteMessage.GroupDeleteMessageResponse");
        lookupMap.put(30321, "ProtoGroupCheckUsername.GroupCheckUsernameResponse");
        lookupMap.put(30322, "ProtoGroupUpdateUsername.GroupUpdateUsernameResponse");
        lookupMap.put(30323, "ProtoGroupRemoveUsername.GroupRemoveUsernameResponse");
        lookupMap.put(30324, "ProtoGroupRevokeLink.GroupRevokeLinkResponse");
        lookupMap.put(30325, "ProtoGroupEditMessage.GroupEditMessageResponse");

        // Channel 4xx , 304xx
        lookupMap.put(30400, "ProtoChannelCreate.ChannelCreateResponse");
        lookupMap.put(30401, "ProtoChannelAddMember.ChannelAddMemberResponse");
        lookupMap.put(30402, "ProtoChannelAddAdmin.ChannelAddAdminResponse");
        lookupMap.put(30403, "ProtoChannelAddModerator.ChannelAddModeratorResponse");
        lookupMap.put(30404, "ProtoChannelDelete.ChannelDeleteResponse");
        lookupMap.put(30405, "ProtoChannelEdit.ChannelEditResponse");
        lookupMap.put(30406, "ProtoChannelKickAdmin.ChannelKickAdminResponse");
        lookupMap.put(30407, "ProtoChannelKickMember.ChannelKickMemberResponse");
        lookupMap.put(30408, "ProtoChannelKickModerator.ChannelKickModeratorResponse");
        lookupMap.put(30409, "ProtoChannelLeft.ChannelLeftResponse");
        lookupMap.put(30410, "ProtoChannelSendMessage.ChannelSendMessageResponse");
        lookupMap.put(30411, "ProtoChannelDeleteMessage.ChannelDeleteMessageResponse");
        lookupMap.put(30412, "ProtoChannelAvatarAdd.ChannelAvatarAddResponse");
        lookupMap.put(30413, "ProtoChannelAvatarDelete.ChannelAvatarDeleteResponse");
        lookupMap.put(30414, "ProtoChannelAvatarGetList.ChannelAvatarGetListResponse");
        lookupMap.put(30415, "ProtoChannelUpdateDraft.ChannelUpdateDraftResponse");
        lookupMap.put(30416, "ProtoChannelGetDraft.ChannelGetDraftResponse");
        lookupMap.put(30417, "ProtoChannelGetMemberList.ChannelGetMemberListResponse");
        lookupMap.put(30418, "ProtoChannelCheckUsername.ChannelCheckUsernameResponse");
        lookupMap.put(30419, "ProtoChannelUpdateUsername.ChannelUpdateUsernameResponse");
        lookupMap.put(30420, "ProtoChannelRemoveUsername.ChannelRemoveUsernameResponse");
        lookupMap.put(30421, "ProtoChannelRevokeLink.ChannelRevokeLinkResponse");
        lookupMap.put(30422, "ProtoChannelUpdateSignature.ChannelUpdateSignatureResponse");
        lookupMap.put(30423, "ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse");
        lookupMap.put(30424, "ProtoChannelAddMessageReaction.ChannelAddMessageReactionResponse");
        lookupMap.put(30425, "ProtoChannelEditMessage.ChannelEditMessageResponse");

        // Info 5xx , 305xx
        lookupMap.put(30500, "ProtoInfoLocation.InfoLocationResponse");
        lookupMap.put(30501, "ProtoInfoCountry.InfoCountryResponse");
        lookupMap.put(30502, "ProtoInfoTime.InfoTimeResponse");
        lookupMap.put(30503, "ProtoInfoPage.InfoPageResponse");

        // Client 6xx , 306xx
        lookupMap.put(30600, "ProtoClientCondition.ClientConditionResponse");
        lookupMap.put(30601, "ProtoClientGetRoomList.ClientGetRoomListResponse");
        lookupMap.put(30602, "ProtoClientGetRoom.ClientGetRoomResponse");
        lookupMap.put(30603, "ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse");
        lookupMap.put(30604, "ProtoClientGetRoomMessage.ClientGetRoomMessageResponse");
        lookupMap.put(30605, "ProtoClientSearchRoomHistory.ClientSearchRoomHistoryResponse");
        lookupMap.put(30606, "ProtoClientResolveUsername.ClientResolveUsernameResponse");
        lookupMap.put(30607, "ProtoClientCheckInviteLink.ClientCheckInviteLinkResponse");
        lookupMap.put(30608, "ProtoClientJoinByInviteLink.ClientJoinByInviteLinkResponse");
        lookupMap.put(30609, "ProtoClientJoinByUsername.ClientJoinByUsernameResponse");
        lookupMap.put(30610, "ProtoClientSubscribeToRoom.ClientSubscribeToRoomResponse");
        lookupMap.put(30611, "ProtoClientUnsubscribeFromRoom.ClientUnsubscribeFromRoomResponse");

        // FileUpload 7xx , 307xx
        lookupMap.put(30700, "ProtoFileUploadOption.FileUploadOptionResponse");
        lookupMap.put(30701, "ProtoFileUploadInit.FileUploadInitResponse");
        lookupMap.put(30702, "ProtoFileUpload.FileUploadResponse");
        lookupMap.put(30703, "ProtoFileUploadStatus.FileUploadStatusResponse");
        lookupMap.put(30704, "ProtoFileInfo.FileInfoResponse");
        lookupMap.put(30705, "ProtoFileDownload.FileDownloadResponse");
    }

    /*public static void fillLookUpClassArray() {

        lookupMap.put(0, "ProtoError.ErrorResponse");
        lookupMap.put(30001, "ProtoConnectionSecuring.ConnectionSecuringResponse");
        lookupMap.put(2, "Connection.Symmetric.Key");
        lookupMap.put(30002, "ProtoConnectionSecuring.ConnectionSymmetricKeyResponse");
        lookupMap.put(3, "Heartbeat");
        lookupMap.put(30003, "ProtoHeartbeat.HeartbeatResponse");

        // User 1xx , 301xx
        lookupMap.put(100, "User.Register");
        lookupMap.put(30100, "ProtoUserRegister.UserRegisterResponse");
        lookupMap.put(101, "User.Verify");
        lookupMap.put(30101, "ProtoUserVerify.UserVerifyResponse");
        lookupMap.put(102, "User.Login");
        lookupMap.put(30102, "ProtoUserLogin.UserLoginResponse");
        lookupMap.put(103, "User.Profile.SetEmail");
        lookupMap.put(30103, "ProtoUserProfileEmail.UserProfileSetEmailResponse");
        lookupMap.put(104, "User.Profile.SetGender");
        lookupMap.put(30104, "ProtoUserProfileGender.UserProfileSetGenderResponse");
        lookupMap.put(105, "User.Profile.SetNickname");
        lookupMap.put(30105, "ProtoUserProfileNickname.UserProfileSetNicknameResponse");
        lookupMap.put(106, "UserContactsImport");
        lookupMap.put(30106, "ProtoUserContactsImport.UserContactsImportResponse");
        lookupMap.put(107, "UserContactsGetList");
        lookupMap.put(30107, "ProtoUserContactsGetList.UserContactsGetListResponse");
        lookupMap.put(108, "UserContactsDelete");
        lookupMap.put(30108, "ProtoUserContactsDelete.UserContactsDeleteResponse");
        lookupMap.put(109, "UserContactsEdit");
        lookupMap.put(30109, "ProtoUserContactsEdit.UserContactsEditResponse");
        lookupMap.put(110, "UserProfileGetEmail");
        lookupMap.put(30110, "ProtoUserProfileGetEmail.UserProfileGetEmailResponse");
        lookupMap.put(111, "UserProfileGetGender");
        lookupMap.put(30111, "ProtoUserProfileGetGender.UserProfileGetGenderResponse");
        lookupMap.put(112, "UserProfileGetNickname");
        lookupMap.put(30112, "ProtoUserProfileGetNickname.UserProfileGetNicknameResponse");
        lookupMap.put(113, "UserUsernameToId");
        lookupMap.put(30113, "ProtoUserUsernameToId.UserUsernameToIdResponse");
        lookupMap.put(114, "UserAvatarAdd");
        lookupMap.put(30114, "ProtoUserAvatarAdd.UserAvatarAddResponse");
        lookupMap.put(115, "UserAvatarDelete");
        lookupMap.put(30115, "ProtoUserAvatarDelete.UserAvatarDeleteResponse");
        lookupMap.put(116, "UserAvatarGetList");
        lookupMap.put(30116, "ProtoUserAvatarGetList.UserAvatarGetListResponse");
        lookupMap.put(117, "UserInfo");
        lookupMap.put(30117, "ProtoUserInfo.UserInfoResponse");
        lookupMap.put(118, "UserGetDeleteToken");
        lookupMap.put(30118, "ProtoUserGetDeleteToken.UserGetDeleteTokenResponse");
        lookupMap.put(119, "UserDelete");
        lookupMap.put(30119, "ProtoUserDelete.UserDeleteResponse");
        lookupMap.put(120, "UserProfileSetSelfRemove");
        lookupMap.put(30120, "ProtoUserProfileSetSelfRemove.UserProfileSetSelfRemoveResponse");
        lookupMap.put(121, "UserProfileGetSelfRemove");
        lookupMap.put(30121, "ProtoUserProfileGetSelfRemove.UserProfileGetSelfRemoveResponse");
        lookupMap.put(122, "UserProfileCheckUsername");
        lookupMap.put(30122, "ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse");
        lookupMap.put(123, "UserProfileUpdateUsername");
        lookupMap.put(30123, "ProtoUserProfileUpdateUsername.UserProfileUpdateUsernameResponse");
        lookupMap.put(124, "UserUpdateStatus");
        lookupMap.put(30124, "ProtoUserUpdateStatus.UserUpdateStatusResponse");
        lookupMap.put(125, "UserSessionGetActiveList");
        lookupMap.put(30125, "ProtoUserSessionGetActiveList.UserSessionGetActiveListResponse");
        lookupMap.put(126, "UserSessionTerminate");
        lookupMap.put(30126, "ProtoUserSessionTerminate.UserSessionTerminateResponse");
        lookupMap.put(127, "UserSessionLogout");
        lookupMap.put(30127, "ProtoUserSessionLogout.UserSessionLogoutResponse");
        lookupMap.put(128, "UserContactsBlock");
        lookupMap.put(30128, "ProtoUserContactsBlock.UserContactsBlockResponse");
        lookupMap.put(129, "UserContactsUnblock");
        lookupMap.put(30129, "ProtoUserContactsUnblock.UserContactsUnblockResponse");
        lookupMap.put(130, "UserContactsGetBlockedList");
        lookupMap.put(30130, "ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse");


        // Chat 2xx , 302xx
        lookupMap.put(200, "Chat.GetRoom");
        lookupMap.put(30200, "ProtoChatGetRoom.ChatGetRoomResponse");
        lookupMap.put(201, "Chat.SendMessage");
        lookupMap.put(30201, "ProtoChatSendMessage.ChatSendMessageResponse");
        lookupMap.put(202, "Chat.UpdateStatus");
        lookupMap.put(30202, "ProtoChatUpdateStatus.ChatUpdateStatusResponse");
        lookupMap.put(203, "Chat.EditMessage");
        lookupMap.put(30203, "ProtoChatEditMessage.ChatEditMessageResponse");
        lookupMap.put(204, "Chat.DeleteMessage");
        lookupMap.put(30204, "ProtoChatDeleteMessage.ChatDeleteMessageResponse");
        lookupMap.put(205, "Chat.ClearMessage");
        lookupMap.put(30205, "ProtoChatClearMessage.ChatClearMessageResponse");
        lookupMap.put(206, "ChatDelete");
        lookupMap.put(30206, "ProtoChatDelete.ChatDeleteResponse");
        lookupMap.put(207, "ChatUpdateDraft");
        lookupMap.put(30207, "ProtoChatUpdateDraft.ChatUpdateDraftResponse");
        lookupMap.put(208, "ChatGetDraft");
        lookupMap.put(30208, "ProtoChatGetDraft.ChatGetDraftResponse");
        lookupMap.put(209, "ChatConvertToGroup");
        lookupMap.put(30209, "ProtoChatConvertToGroup.ChatConvertToGroupResponse");
        lookupMap.put(210, "ChatSetAction");
        lookupMap.put(30210, "ProtoChatSetAction.ChatSetActionResponse");

        // Group 3xx , 303xx
        lookupMap.put(300, "GroupCreate");
        lookupMap.put(30300, "ProtoGroupCreate.GroupCreateResponse");
        lookupMap.put(301, "GroupAddMember");
        lookupMap.put(30301, "ProtoGroupAddMember.GroupAddMemberResponse");
        lookupMap.put(302, "GroupAddAdmin");
        lookupMap.put(30302, "ProtoGroupAddAdmin.GroupAddAdminResponse");
        lookupMap.put(303, "GroupAddModerator");
        lookupMap.put(30303, "ProtoGroupAddModerator.GroupAddModeratorResponse");
        lookupMap.put(304, "GroupClearMessage");
        lookupMap.put(30304, "ProtoGroupClearMessage.GroupClearMessageResponse");
        lookupMap.put(305, "GroupEdit");
        lookupMap.put(30305, "ProtoGroupEdit.GroupEditResponse");
        lookupMap.put(306, "GroupKickAdmin");
        lookupMap.put(30306, "ProtoGroupKickAdmin.GroupKickAdminResponse");
        lookupMap.put(307, "GroupKickMember");
        lookupMap.put(30307, "ProtoGroupKickMember.GroupKickMemberResponse");
        lookupMap.put(308, "GroupKickModerator");
        lookupMap.put(30308, "ProtoGroupKickModerator.GroupKickModeratorResponse");
        lookupMap.put(309, "GroupLeft");
        lookupMap.put(30309, "ProtoGroupLeft.GroupLeftResponse");
        lookupMap.put(310, "GroupSendMessage");
        lookupMap.put(30310, "ProtoGroupSendMessage.GroupSendMessageResponse");
        lookupMap.put(311, "GroupUpdateStatus");
        lookupMap.put(30311, "ProtoGroupUpdateStatus.GroupUpdateStatusResponse");
        lookupMap.put(312, "GroupAvatarAdd");
        lookupMap.put(30312, "ProtoGroupAvatarAdd.GroupAvatarAddResponse");
        lookupMap.put(313, "GroupAvatarDelete");
        lookupMap.put(30313, "ProtoGroupAvatarDelete.GroupAvatarDeleteResponse");
        lookupMap.put(314, "GroupAvatarGetList");
        lookupMap.put(30314, "ProtoGroupAvatarGetList.GroupAvatarGetListResponse");
        lookupMap.put(315, "GroupUpdateDraft");
        lookupMap.put(30315, "ProtoGroupUpdateDraft.GroupUpdateDraftResponse");
        lookupMap.put(316, "GroupGetDraft");
        lookupMap.put(30316, "ProtoGroupGetDraft.GroupGetDraftResponse");
        lookupMap.put(317, "GroupGetMemberList");
        lookupMap.put(30317, "ProtoGroupGetMemberList.GroupGetMemberListResponse");
        lookupMap.put(318, "GroupDelete");
        lookupMap.put(30318, "ProtoGroupDelete.GroupDeleteResponse");
        lookupMap.put(319, "GroupSetAction");
        lookupMap.put(30319, "ProtoGroupSetAction.GroupSetActionResponse");
        lookupMap.put(320, "GroupDeleteMessage");
        lookupMap.put(30320, "ProtoGroupDeleteMessage.GroupDeleteMessageResponse");
        lookupMap.put(321, "GroupCheckUsername");
        lookupMap.put(30321, "ProtoGroupCheckUsername.GroupCheckUsernameResponse");
        lookupMap.put(322, "GroupUpdateUsername");
        lookupMap.put(30322, "ProtoGroupUpdateUsername.GroupUpdateUsernameResponse");
        lookupMap.put(323, "GroupRemoveUsername");
        lookupMap.put(30323, "ProtoGroupRemoveUsername.GroupRemoveUsernameResponse");
        lookupMap.put(324, "GroupRevokeLink");
        lookupMap.put(30324, "ProtoGroupRevokeLink.GroupRevokeLinkResponse");
        lookupMap.put(325, "GroupEditMessage");
        lookupMap.put(30325, "ProtoGroupEditMessage.GroupEditMessageResponse");

        // Channel 4xx , 304xx
        lookupMap.put(400, "ChannelCreate");
        lookupMap.put(30400, "ProtoChannelCreate.ChannelCreateResponse");
        lookupMap.put(401, "ChannelAddMember");
        lookupMap.put(30401, "ProtoChannelAddMember.ChannelAddMemberResponse");
        lookupMap.put(402, "ChannelAddAdmin");
        lookupMap.put(30402, "ProtoChannelAddAdmin.ChannelAddAdminResponse");
        lookupMap.put(403, "ChannelAddModerator");
        lookupMap.put(30403, "ProtoChannelAddModerator.ChannelAddModeratorResponse");
        lookupMap.put(404, "ChannelDelete");
        lookupMap.put(30404, "ProtoChannelDelete.ChannelDeleteResponse");
        lookupMap.put(405, "ChannelEdit");
        lookupMap.put(30405, "ProtoChannelEdit.ChannelEditResponse");
        lookupMap.put(406, "ChannelKickAdmin");
        lookupMap.put(30406, "ProtoChannelKickAdmin.ChannelKickAdminResponse");
        lookupMap.put(407, "ChannelKickMember");
        lookupMap.put(30407, "ProtoChannelKickMember.ChannelKickMemberResponse");
        lookupMap.put(408, "ChannelKickModerator");
        lookupMap.put(30408, "ProtoChannelKickModerator.ChannelKickModeratorResponse");
        lookupMap.put(409, "ChannelLeft");
        lookupMap.put(30409, "ProtoChannelLeft.ChannelLeftResponse");
        lookupMap.put(410, "ChannelSendMessage");
        lookupMap.put(30410, "ProtoChannelSendMessage.ChannelSendMessageResponse");
        lookupMap.put(411, "ChannelDeleteMessage");
        lookupMap.put(30411, "ProtoChannelDeleteMessage.ChannelDeleteMessageResponse");
        lookupMap.put(412, "ChannelAvatarAdd");
        lookupMap.put(30412, "ProtoChannelAvatarAdd.ChannelAvatarAddResponse");
        lookupMap.put(413, "ChannelAvatarDelete");
        lookupMap.put(30413, "ProtoChannelAvatarDelete.ChannelAvatarDeleteResponse");
        lookupMap.put(414, "ChannelAvatarGetList");
        lookupMap.put(30414, "ProtoChannelAvatarGetList.ChannelAvatarGetListResponse");
        lookupMap.put(415, "ChannelUpdateDraft");
        lookupMap.put(30415, "ProtoChannelUpdateDraft.ChannelUpdateDraftResponse");
        lookupMap.put(416, "ChannelGetDraft");
        lookupMap.put(30416, "ProtoChannelGetDraft.ChannelGetDraftResponse");
        lookupMap.put(417, "ChannelGetMemberList");
        lookupMap.put(30417, "ProtoChannelGetMemberList.ChannelGetMemberListResponse");
        lookupMap.put(418, "ChannelCheckUsername");
        lookupMap.put(30418, "ProtoChannelCheckUsername.ChannelCheckUsernameResponse");
        lookupMap.put(419, "ChannelUpdateUsername");
        lookupMap.put(30419, "ProtoChannelUpdateUsername.ChannelUpdateUsernameResponse");
        lookupMap.put(420, "ChannelRemoveUsername");
        lookupMap.put(30420, "ProtoChannelRemoveUsername.ChannelRemoveUsernameResponse");
        lookupMap.put(421, "ChannelRevokeLink");
        lookupMap.put(30421, "ProtoChannelRevokeLink.ChannelRevokeLinkResponse");
        lookupMap.put(422, "ChannelUpdateSignature");
        lookupMap.put(30422, "ProtoChannelUpdateSignature.ChannelUpdateSignatureResponse");
        lookupMap.put(423, "ChannelGetMessagesStats");
        lookupMap.put(30423, "ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse");
        lookupMap.put(424, "ChannelAddMessageReaction");
        lookupMap.put(30424, "ProtoChannelAddMessageReaction.ChannelAddMessageReactionResponse");
        lookupMap.put(425, "ChannelEditMessage");
        lookupMap.put(30425, "ProtoChannelEditMessage.ChannelEditMessageResponse");

        // Info 5xx , 305xx
        lookupMap.put(500, "Info.Location");
        lookupMap.put(30500, "ProtoInfoLocation.InfoLocationResponse");
        lookupMap.put(501, "Info.Country");
        lookupMap.put(30501, "ProtoInfoCountry.InfoCountryResponse");
        lookupMap.put(502, "Info.Time");
        lookupMap.put(30502, "ProtoInfoTime.InfoTimeResponse");
        lookupMap.put(503, "Info.Page");
        lookupMap.put(30503, "ProtoInfoPage.InfoPageResponse");

        // Client 6xx , 306xx
        lookupMap.put(600, "Client.Condition");
        lookupMap.put(30600, "ProtoClientCondition.ClientConditionResponse");
        lookupMap.put(601, "Client.GetRoomList");
        lookupMap.put(30601, "ProtoClientGetRoomList.ClientGetRoomListResponse");
        lookupMap.put(602, "Client.GetRoom");
        lookupMap.put(30602, "ProtoClientGetRoom.ClientGetRoomResponse");
        lookupMap.put(603, "ClientGetRoomHistory");
        lookupMap.put(30603, "ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse");
        lookupMap.put(604, "ClientGetRoomMessage");
        lookupMap.put(30604, "ProtoClientGetRoomMessage.ClientGetRoomMessageResponse");
        lookupMap.put(605, "ClientSearchRoomHistory");
        lookupMap.put(30605, "ProtoClientSearchRoomHistory.ClientSearchRoomHistoryResponse");
        lookupMap.put(606, "ClientResolveUsername");
        lookupMap.put(30606, "ProtoClientResolveUsername.ClientResolveUsernameResponse");
        lookupMap.put(607, "ClientCheckInviteLink");
        lookupMap.put(30607, "ProtoClientCheckInviteLink.ClientCheckInviteLinkResponse");
        lookupMap.put(608, "ClientJoinByInviteLink");
        lookupMap.put(30608, "ProtoClientJoinByInviteLink.ClientJoinByInviteLinkResponse");
        lookupMap.put(609, "ClientJoinByUsername");
        lookupMap.put(30609, "ProtoClientJoinByUsername.ClientJoinByUsernameResponse");
        lookupMap.put(610, "ClientSubscribeToRoom");
        lookupMap.put(30610, "ProtoClientSubscribeToRoom.ClientSubscribeToRoomResponse");
        lookupMap.put(611, "ClientUnsubscribeFromRoom");
        lookupMap.put(30611, "ProtoClientUnsubscribeFromRoom.ClientUnsubscribeFromRoomResponse");

        // FileUpload 7xx , 307xx
        lookupMap.put(700, "FileUploadOption");
        lookupMap.put(30700, "ProtoFileUploadOption.FileUploadOptionResponse");
        lookupMap.put(701, "FileUploadInit");
        lookupMap.put(30701, "ProtoFileUploadInit.FileUploadInitResponse");
        lookupMap.put(702, "FileUpload");
        lookupMap.put(30702, "ProtoFileUpload.FileUploadResponse");
        lookupMap.put(703, "FileUploadStatus");
        lookupMap.put(30703, "ProtoFileUploadStatus.FileUploadStatusResponse");
        lookupMap.put(704, "FileInfo");
        lookupMap.put(30704, "ProtoFileInfo.FileInfoResponse");
        lookupMap.put(705, "FileDownload");
        lookupMap.put(30705, "ProtoFileDownload.FileDownloadResponse");
    }*/
}
