package com.iGap.helper;

import com.iGap.G;

public class HelperFillLookUpClass {

    /**
     * fill static hashMap with actionId and proto class name
     */

    public static void fillLookUpClassArray() {

        G.lookupMap.put(0, "ProtoError.ErrorResponse");
        G.lookupMap.put(30001, "ProtoConnectionSecuring.ConnectionSecuringResponse");
        G.lookupMap.put(2, "Connection.Symmetric.Key");
        G.lookupMap.put(30002, "ProtoConnectionSecuring.ConnectionSymmetricKeyResponse");

        // User 1xx , 301xx
        G.lookupMap.put(100, "User.Register");
        G.lookupMap.put(30100, "ProtoUserRegister.UserRegisterResponse");
        G.lookupMap.put(101, "User.Verify");
        G.lookupMap.put(30101, "ProtoUserVerify.UserVerifyResponse");
        G.lookupMap.put(102, "User.Login");
        G.lookupMap.put(30102, "ProtoUserLogin.UserLoginResponse");
        G.lookupMap.put(103, "User.Profile.SetEmail");
        G.lookupMap.put(30103, "ProtoUserProfileEmail.UserProfileSetEmailResponse");
        G.lookupMap.put(104, "User.Profile.SetGender");
        G.lookupMap.put(30104, "ProtoUserProfileGender.UserProfileSetGenderResponse");
        G.lookupMap.put(105, "User.Profile.SetNickname");
        G.lookupMap.put(30105, "ProtoUserProfileNickname.UserProfileSetNicknameResponse");
        G.lookupMap.put(106, "UserContactsImport");
        G.lookupMap.put(30106, "ProtoUserContactsImport.UserContactsImportResponse");
        G.lookupMap.put(107, "UserContactsGetList");
        G.lookupMap.put(30107, "ProtoUserContactsGetList.UserContactsGetListResponse");
        G.lookupMap.put(108, "UserContactsDelete");
        G.lookupMap.put(30108, "ProtoUserContactsDelete.UserContactsDeleteResponse");
        G.lookupMap.put(109, "UserContactsEdit");
        G.lookupMap.put(30109, "ProtoUserContactsEdit.UserContactsEditResponse");
        G.lookupMap.put(110, "UserProfileGetEmail");
        G.lookupMap.put(30110, "ProtoUserProfileGetEmail.UserProfileGetEmailResponse");
        G.lookupMap.put(111, "UserProfileGetGender");
        G.lookupMap.put(30111, "ProtoUserProfileGetGender.UserProfileGetGenderResponse");
        G.lookupMap.put(112, "UserProfileGetNickname");
        G.lookupMap.put(30112, "ProtoUserProfileGetNickname.UserProfileGetNicknameResponse");
        G.lookupMap.put(113, "UserUsernameToId");
        G.lookupMap.put(30113, "ProtoUserUsernameToId.UserUsernameToIdResponse");
        G.lookupMap.put(114, "UserAvatarAdd");
        G.lookupMap.put(30114, "ProtoUserAvatarAdd.UserAvatarAddResponse");
        G.lookupMap.put(115, "UserAvatarDelete");
        G.lookupMap.put(30115, "ProtoUserAvatarDelete.UserAvatarDeleteResponse");
        G.lookupMap.put(116, "UserAvatarGetList");
        G.lookupMap.put(30116, "ProtoUserAvatarGetList.UserAvatarGetListResponse");
        G.lookupMap.put(117, "UserInfo");
        G.lookupMap.put(30117, "ProtoUserInfo.UserInfoResponse");
        G.lookupMap.put(118, "UserGetDeleteToken");
        G.lookupMap.put(30118, "ProtoUserGetDeleteToken.UserGetDeleteTokenResponse");
        G.lookupMap.put(119, "UserDelete");
        G.lookupMap.put(30119, "ProtoUserDelete.UserDeleteResponse");
        G.lookupMap.put(120, "UserProfileSetSelfRemove");
        G.lookupMap.put(30120, "ProtoUserProfileSetSelfRemove.UserProfileSetSelfRemoveResponse");
        G.lookupMap.put(121, "UserProfileGetSelfRemove");
        G.lookupMap.put(30121, "ProtoUserProfileGetSelfRemove.UserProfileGetSelfRemoveResponse");
        G.lookupMap.put(122, "UserProfileCheckUsername");
        G.lookupMap.put(30122, "ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse");
        G.lookupMap.put(123, "UserProfileUpdateUsername");
        G.lookupMap.put(30123, "ProtoUserProfileUpdateUsername.UserProfileUpdateUsernameResponse");

        // Chat 2xx , 302xx
        G.lookupMap.put(200, "Chat.GetRoom");
        G.lookupMap.put(30200, "ProtoChatGetRoom.ChatGetRoomResponse");
        G.lookupMap.put(201, "Chat.SendMessage");
        G.lookupMap.put(30201, "ProtoChatSendMessage.ChatSendMessageResponse");
        G.lookupMap.put(202, "Chat.UpdateStatus");
        G.lookupMap.put(30202, "ProtoChatUpdateStatus.ChatUpdateStatusResponse");
        G.lookupMap.put(203, "Chat.EditMessage");
        G.lookupMap.put(30203, "ProtoChatEditMessage.ChatEditMessageResponse");
        G.lookupMap.put(204, "Chat.DeleteMessage");
        G.lookupMap.put(30204, "ProtoChatDeleteMessage.ChatDeleteMessageResponse");
        G.lookupMap.put(205, "Chat.ClearMessage");
        G.lookupMap.put(30205, "ProtoChatClearMessage.ChatClearMessageResponse");
        G.lookupMap.put(206, "ChatDelete");
        G.lookupMap.put(30206, "ProtoChatDelete.ChatDeleteResponse");
        G.lookupMap.put(207, "ChatUpdateDraft");
        G.lookupMap.put(30207, "ProtoChatUpdateDraft.ChatUpdateDraftResponse");
        G.lookupMap.put(208, "ChatGetDraft");
        G.lookupMap.put(30208, "ProtoChatGetDraft.ChatGetDraftResponse");

        // Group 3xx , 303xx
        G.lookupMap.put(300, "GroupCreate");
        G.lookupMap.put(30300, "ProtoGroupCreate.GroupCreateResponse");
        G.lookupMap.put(301, "GroupAddMember");
        G.lookupMap.put(30301, "ProtoGroupAddMember.GroupAddMemberResponse");
        G.lookupMap.put(302, "GroupAddAdmin");
        G.lookupMap.put(30302, "ProtoGroupAddAdmin.GroupAddAdminResponse");
        G.lookupMap.put(303, "GroupAddModerator");
        G.lookupMap.put(30303, "ProtoGroupAddModerator.GroupAddModeratorResponse");
        G.lookupMap.put(304, "GroupClearMessage");
        G.lookupMap.put(30304, "ProtoGroupClearMessage.GroupClearMessageResponse");
        G.lookupMap.put(305, "GroupEdit");
        G.lookupMap.put(30305, "ProtoGroupEdit.GroupEditResponse");
        G.lookupMap.put(306, "GroupKickAdmin");
        G.lookupMap.put(30306, "ProtoGroupKickAdmin.GroupKickAdminResponse");
        G.lookupMap.put(307, "GroupKickMember");
        G.lookupMap.put(30307, "ProtoGroupKickMember.GroupKickMemberResponse");
        G.lookupMap.put(308, "GroupKickModerator");
        G.lookupMap.put(30308, "ProtoGroupKickModerator.GroupKickModeratorResponse");
        G.lookupMap.put(309, "GroupLeft");
        G.lookupMap.put(30309, "ProtoGroupLeft.GroupLeftResponse");
        G.lookupMap.put(310, "GroupSendMessage");
        G.lookupMap.put(30310, "ProtoGroupSendMessage.GroupSendMessageResponse");
        G.lookupMap.put(311, "GroupUpdateStatus");
        G.lookupMap.put(30311, "ProtoGroupUpdateStatus.GroupUpdateStatusResponse");
        G.lookupMap.put(312, "GroupAvatarAdd");
        G.lookupMap.put(30312, "ProtoGroupAvatarAdd.GroupAvatarAddResponse");
        G.lookupMap.put(313, "GroupAvatarDelete");
        G.lookupMap.put(30313, "ProtoGroupAvatarDelete.GroupAvatarDeleteResponse");
        G.lookupMap.put(314, "GroupAvatarGetList");
        G.lookupMap.put(30314, "ProtoGroupAvatarGetList.GroupAvatarGetListResponse");
        G.lookupMap.put(315, "GroupUpdateDraft");
        G.lookupMap.put(30315, "ProtoGroupUpdateDraft.GroupUpdateDraftResponse");
        G.lookupMap.put(316, "GroupGetDraft");
        G.lookupMap.put(30316, "ProtoGroupGetDraft.GroupGetDraftResponse");
        G.lookupMap.put(317, "GroupGetMemberList");
        G.lookupMap.put(30317, "ProtoGroupGetMemberList.GroupGetMemberListResponse");
        G.lookupMap.put(318, "GroupDelete");
        G.lookupMap.put(30318, "ProtoGroupDelete.GroupDeleteResponse");

        // Info 5xx , 305xx
        G.lookupMap.put(500, "Info.Location");
        G.lookupMap.put(30500, "ProtoInfoLocation.InfoLocationResponse");
        G.lookupMap.put(501, "Info.Country");
        G.lookupMap.put(30501, "ProtoInfoCountry.InfoCountryResponse");
        G.lookupMap.put(502, "Info.Time");
        G.lookupMap.put(30502, "ProtoInfoTime.InfoTimeResponse");
        G.lookupMap.put(503, "Info.Page");
        G.lookupMap.put(30503, "ProtoInfoPage.InfoPageResponse");

        // Client 6xx , 306xx
        G.lookupMap.put(600, "Client.Condition");
        G.lookupMap.put(30600, "ProtoClientCondition.ClientConditionResponse");
        G.lookupMap.put(601, "Client.GetRoomList");
        G.lookupMap.put(30601, "ProtoClientGetRoomList.ClientGetRoomListResponse");
        G.lookupMap.put(602, "Client.GetRoom");
        G.lookupMap.put(30602, "ProtoClientGetRoom.ClientGetRoomResponse");
        G.lookupMap.put(603, "ClientGetRoomHistory");
        G.lookupMap.put(30603, "ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse");

        // FileUpload 7xx , 307xx
        G.lookupMap.put(700, "FileUploadOption");
        G.lookupMap.put(30700, "ProtoFileUploadOption.FileUploadOptionResponse");
        G.lookupMap.put(701, "FileUploadInit");
        G.lookupMap.put(30701, "ProtoFileUploadInit.FileUploadInitResponse");
        G.lookupMap.put(702, "FileUpload");
        G.lookupMap.put(30702, "ProtoFileUpload.FileUploadResponse");
        G.lookupMap.put(703, "FileUploadStatus");
        G.lookupMap.put(30703, "ProtoFileUploadStatus.FileUploadStatusResponse");
        G.lookupMap.put(704, "FileInfo");
        G.lookupMap.put(30704, "ProtoFileInfo.FileInfoResponse");
        G.lookupMap.put(705, "FileDownload");
        G.lookupMap.put(30705, "ProtoFileDownload.FileDownloadResponse");
    }
}
