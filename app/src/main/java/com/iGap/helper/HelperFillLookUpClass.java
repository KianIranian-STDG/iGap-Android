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
        G.lookupMap.put(103, "User.Profile.Email");
        G.lookupMap.put(30103, "ProtoUserProfileEmail.UserProfileEmailResponse");
        G.lookupMap.put(104, "User.Profile.Gender");
        G.lookupMap.put(30104, "ProtoUserProfileGender.UserProfileGenderResponse");
        G.lookupMap.put(105, "User.Profile.Nickname");
        G.lookupMap.put(30105, "ProtoUserProfileNickname.UserProfileNicknameResponse");
        G.lookupMap.put(106, "UserContactsImport");
        G.lookupMap.put(30106, "ProtoUserContactsImport.UserContactsImportResponse");
        G.lookupMap.put(107, "UserContactsGetList");
        G.lookupMap.put(30107, "ProtoUserContactsGetList.UserContactsGetListResponse");
        G.lookupMap.put(108, "UserContactsDelete");
        G.lookupMap.put(30108, "ProtoUserContactsDelete.UserContactsDeleteResponse");
        G.lookupMap.put(109, "UserContactsEdit");
        G.lookupMap.put(30109, "ProtoUserContactsEdit.UserContactsEditResponse");

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
    }

}
