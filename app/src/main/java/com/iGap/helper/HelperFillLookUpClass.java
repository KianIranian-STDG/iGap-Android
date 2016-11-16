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
        lookupMap.put(605, "ClientSearchRoomHistory");
        lookupMap.put(30605, "ProtoClientSearchRoomHistory.ClientSearchRoomHistoryResponse");

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
    }
}
