/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.network;

import android.util.Log;
import android.util.SparseArray;

import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.fragments.FragmentShowImage;
import net.iGap.helper.FileLog;

import static net.iGap.G.forcePriorityActionId;
import static net.iGap.G.generalImmovableClasses;
import static net.iGap.G.ignoreErrorCodes;
import static net.iGap.G.lookupMap;
import static net.iGap.G.priorityActionId;
import static net.iGap.G.unLogin;
import static net.iGap.G.unSecure;
import static net.iGap.G.unSecureResponseActionId;
import static net.iGap.G.waitingActionIds;

public class LookUpClass {

    private static LookUpClass instance;
    private SparseArray<Class<?>> classes;

    public static LookUpClass getInstance() {
        if (instance == null) {
            instance = new LookUpClass();
        }
        return instance;
    }

    public LookUpClass() {
        classes = new SparseArray<>();
        classes.put(IG_RPC.Error.actionId, IG_RPC.Error.class);
        classes.put(IG_RPC.Res_Channel_Create.actionId, IG_RPC.Res_Channel_Create.class);
        classes.put(IG_RPC.Res_Channel_Delete.actionId, IG_RPC.Res_Channel_Delete.class);
        classes.put(IG_RPC.Res_Group_Create.actionId, IG_RPC.Res_Group_Create.class);
        classes.put(IG_RPC.Res_Channel_Avatar.actionId, IG_RPC.Res_Channel_Avatar.class);

        classes.put(IG_RPC.Res_Info_Config.actionId, IG_RPC.Res_Info_Config.class);
    }

    public boolean validObject(int actionId) {
        return classes.get(actionId) != null;
    }

    public AbstractObject deserializeObject(int actionId, byte[] message) {
        Class<?> resClass = classes.get(actionId);
        if (resClass != null) {
            try {
                AbstractObject object = (AbstractObject) resClass.newInstance();
                object.readParams(message);

                return object;
            } catch (IllegalAccessException e) {
                FileLog.e(e);
            } catch (InstantiationException e) {
                FileLog.e(e);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        Log.e(getClass().getSimpleName(), "IllegalAccessException getClassInstance: " + actionId);
        return null;
    }

    public AbstractObject getClassInstance(int actionId) {
        Class<?> objClass = classes.get(actionId);
        if (objClass != null) {
            AbstractObject response;
            try {
                response = (AbstractObject) objClass.newInstance();
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
            return response;
        }

        Log.e(getClass().getSimpleName(), "getClassInstance: " + actionId);

        return null;
    }

    public static void fillArrays() {
        LookUpClass.fillLookUpClassArray();
        LookUpClass.fillUnSecureList();
        LookUpClass.fillUnSecureServerActionId();
        LookUpClass.fillUnLoginList();
        LookUpClass.fillImmovableClasses();
        LookUpClass.fillWaitingRequestActionIdAllowed();
        LookUpClass.fillPriorityActionId();
        LookUpClass.fillForcePriorityActionId();
        LookUpClass.fillIgnoreErrorCodes();
    }

    /**
     * fill static hashMap with actionId and proto class name
     */
    private static void fillLookUpClassArray() {

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
        lookupMap.put(30131, "ProtoUserTwoStepVerificationGetPasswordDetail.UserTwoStepVerificationGetPasswordDetailResponse");
        lookupMap.put(30132, "ProtoUserTwoStepVerificationVerifyPassword.UserTwoStepVerificationVerifyPasswordResponse");
        lookupMap.put(30133, "ProtoUserTwoStepVerificationSetPassword.UserTwoStepVerificationSetPasswordResponse");
        lookupMap.put(30134, "ProtoUserTwoStepVerificationUnsetPassword.UserTwoStepVerificationUnsetPasswordResponse");
        lookupMap.put(30135, "ProtoUserTwoStepVerificationCheckPassword.UserTwoStepVerificationCheckPasswordResponse");
        lookupMap.put(30136, "ProtoUserTwoStepVerificationVerifyRecoveryEmail.UserTwoStepVerificationVerifyRecoveryEmailResponse");
        lookupMap.put(30137, "ProtoUserTwoStepVerificationChangeRecoveryEmail.UserTwoStepVerificationChangeRecoveryEmailResponse");
        lookupMap.put(30138, "ProtoUserTwoStepVerificationRequestRecoveryToken.UserTwoStepVerificationRequestRecoveryTokenResponse");
        lookupMap.put(30139, "ProtoUserTwoStepVerificationRecoverPasswordByToken.UserTwoStepVerificationRecoverPasswordByTokenResponse");
        lookupMap.put(30140, "ProtoUserTwoStepVerificationRecoverPasswordByAnswers.UserTwoStepVerificationRecoverPasswordByAnswersResponse");
        lookupMap.put(30141, "ProtoUserTwoStepVerificationChangeRecoveryQuestion.UserTwoStepVerificationChangeRecoveryQuestionResponse");
        lookupMap.put(30142, "ProtoUserTwoStepVerificationChangeHint.UserTwoStepVerificationChangeHintResponse");
        lookupMap.put(30143, "ProtoUserPrivacyGetRule.UserPrivacyGetRuleResponse");
        lookupMap.put(30144, "ProtoUserPrivacySetRule.UserPrivacySetRuleResponse");
        lookupMap.put(30145, "ProtoUserVerifyNewDevice.UserVerifyNewDeviceResponse");
        lookupMap.put(30146, "ProtoUserTwoStepVerificationResendVerifyEmail.UserTwoStepVerificationResendVerifyEmailResponse");
        lookupMap.put(30147, "ProtoUserProfileBio.UserProfileSetBioResponse");
        lookupMap.put(30148, "ProtoUserProfileGetBio.UserProfileGetBioResponse");
        lookupMap.put(30149, "ProtoUserReport.UserReportResponse");
        lookupMap.put(30150, "ProtoUserSetBot.UserSetBotResponse");
        lookupMap.put(30151, "ProtoUserProfileGetRepresentative.UserProfileGetRepresentativeResponse");
        lookupMap.put(30152, "ProtoUserProfileRepresentative.UserProfileSetRepresentativeResponse");
        lookupMap.put(30153, "ProtoUserIVandGetActivities.UserIVandGetActivitiesResponse");
        lookupMap.put(30154, "ProtoUserIVandGetScore.UserIVandGetScoreResponse");
        lookupMap.put(30155, "ProtoUserIVandSetActivity.UserIVandSetActivityResponse");
        lookupMap.put(30156, "ProtoUserRefreshToken.UserRefreshTokenResponse");

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
        lookupMap.put(30326, "ProtoGroupPinMessage.GroupPinMessageResponse");
        lookupMap.put(30327, "ProtoGroupChangeMemberRights.GroupChangeMemberRightsResponse");

        // Channel 4xx , 304xx
        lookupMap.put(30401, "ProtoChannelAddMember.ChannelAddMemberResponse");
        lookupMap.put(30402, "ProtoChannelAddAdmin.ChannelAddAdminResponse");
        lookupMap.put(30403, "ProtoChannelAddModerator.ChannelAddModeratorResponse");
        lookupMap.put(30405, "ProtoChannelEdit.ChannelEditResponse");
        lookupMap.put(30406, "ProtoChannelKickAdmin.ChannelKickAdminResponse");
        lookupMap.put(30407, "ProtoChannelKickMember.ChannelKickMemberResponse");
        lookupMap.put(30408, "ProtoChannelKickModerator.ChannelKickModeratorResponse");
        lookupMap.put(30409, "ProtoChannelLeft.ChannelLeftResponse");
        lookupMap.put(30410, "ProtoChannelSendMessage.ChannelSendMessageResponse");
        lookupMap.put(30411, "ProtoChannelDeleteMessage.ChannelDeleteMessageResponse");
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
        lookupMap.put(30426, "ProtoChannelUpdateReactionStatus.ChannelUpdateReactionStatusResponse");
        lookupMap.put(30427, "ProtoChannelPinMessage.ChannelPinMessageResponse");

        // Info 5xx , 305xx
        lookupMap.put(30500, "ProtoInfoLocation.InfoLocationResponse");
        lookupMap.put(30501, "ProtoInfoCountry.InfoCountryResponse");
        lookupMap.put(30502, "ProtoInfoTime.InfoTimeResponse");
        lookupMap.put(30503, "ProtoInfoPage.InfoPageResponse");
        lookupMap.put(30504, "ProtoInfoWallpaper.InfoWallpaperResponse");
        lookupMap.put(30505, "ProtoInfoUpdate.InfoUpdateResponse");

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
        lookupMap.put(30612, "ProtoClientSearchUsername.ClientSearchUsernameResponse");
        lookupMap.put(30613, "ProtoClientCountRoomHistory.ClientCountRoomHistoryResponse");
        lookupMap.put(30614, "ProtoClientMuteRoom.ClientMuteRoomResponse");
        lookupMap.put(30615, "ProtoClientPinRoom.ClientPinRoomResponse");
        lookupMap.put(30616, "ProtoClientRoomReport.ClientRoomReportResponse");
        lookupMap.put(30617, "ProtoClientRegisterDevice.ClientRegisterDeviceResponse");
        lookupMap.put(30618, "ProtoClientGetPromote.ClientGetPromoteResponse");
        lookupMap.put(30619, "ProtoClientGetFavoriteMenu.ClientGetFavoriteMenuResponse");
        lookupMap.put(30620, "ProtoClientGetDiscovery.ClientGetDiscoveryResponse");
        lookupMap.put(30621, "ProtoClientSetDiscoveryItemClick.ClientSetDiscoveryItemClickResponse");
        lookupMap.put(30623, "ProtoClientSetDiscoveryItemAgreement.ClientSetDiscoveryItemAgreementResponse");
        lookupMap.put(30624, "ProtoClientGetPoll.ClientGetPollResponse");
        lookupMap.put(30625, "ProtoClientSetPollItemClick.ClientSetPollItemClickResponse");

        // FileUpload,Download 7xx , 307xx
        lookupMap.put(30700, "ProtoFileUploadOption.FileUploadOptionResponse");
        lookupMap.put(30701, "ProtoFileUploadInit.FileUploadInitResponse");
        lookupMap.put(30702, "ProtoFileUpload.FileUploadResponse");
        lookupMap.put(30703, "ProtoFileUploadStatus.FileUploadStatusResponse");
        lookupMap.put(30704, "ProtoFileInfo.FileInfoResponse");
        lookupMap.put(30705, "ProtoFileDownload.FileDownloadResponse");

        // QR Code 8xx , 806xx
        lookupMap.put(30800, "ProtoQrCodeJoin.QrCodeJoinResponse");
        lookupMap.put(30801, "ProtoQrCodeResolve.QrCodeResolveResponse");
        lookupMap.put(30802, "ProtoQrCodeNewDevice.QrCodeNewDeviceResponse");
        lookupMap.put(30803, "ProtoQrCodeAddContact.QrCodeAddContactResponse");
        lookupMap.put(30804, "ProtoQrCodeAddMe.QrCodeAddMeResponse");

        // Signaling 9xx , 309xx
        lookupMap.put(30900, "ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse");
        lookupMap.put(30901, "ProtoSignalingOffer.SignalingOfferResponse");
        lookupMap.put(30902, "ProtoSignalingRinging.SignalingRingingResponse");
        lookupMap.put(30903, "ProtoSignalingAccept.SignalingAcceptResponse");
        lookupMap.put(30904, "ProtoSignalingCandidate.SignalingCandidateResponse");
        lookupMap.put(30905, "ProtoSignalingLeave.SignalingLeaveResponse");
        lookupMap.put(30906, "ProtoSignalingSessionHold.SignalingSessionHoldResponse");
        lookupMap.put(30907, "ProtoSignalingGetLog.SignalingGetLogResponse");
        lookupMap.put(30908, "ProtoSignalingClearLog.SignalingClearLogResponse");
        lookupMap.put(30909, "ProtoSignalingRate.SignalingRateResponse");

        // Geo 10xx , 310xx
        lookupMap.put(31000, "ProtoGeoGetRegisterStatus.GeoGetRegisterStatusResponse");
        lookupMap.put(31001, "ProtoGeoRegister.GeoRegisterResponse");
        lookupMap.put(31002, "ProtoGeoUpdatePosition.GeoUpdatePositionResponse");
        lookupMap.put(31003, "ProtoGeoGetComment.GeoGetCommentResponse");
        lookupMap.put(31004, "ProtoGeoUpdateComment.GeoUpdateCommentResponse");
        lookupMap.put(31005, "ProtoGeoGetNearbyDistance.GeoGetNearbyDistanceResponse");
        lookupMap.put(31006, "ProtoGeoGetNearbyCoordinate.GeoGetNearbyCoordinateResponse");
        lookupMap.put(31007, "ProtoGeoGetConfiguration.GeoGetConfigurationResponse");

        //Wallet 90xx
        lookupMap.put(39000, "ProtoWalletGetAccessToken.WalletGetAccessTokenResponse");
        lookupMap.put(39001, "ProtoWalletPaymentInit.WalletPaymentInitResponse");
        lookupMap.put(39002, "ProtoWalletRegister.WalletRegisterResponse");
        lookupMap.put(39003, "ProtoWalletIdMapping.WalletIdMappingResponse");

        //Mpl 90xx
        lookupMap.put(39100, "ProtoMplGetBillToken.MplGetBillTokenResponse");
        lookupMap.put(39101, "ProtoMplGetTopupToken.MplGetTopupTokenResponse");
        lookupMap.put(39102, "ProtoMplGetSalesToken.MplGetSalesTokenResponse");
        lookupMap.put(39103, "ProtoMplSetSalesResult.MplSetSalesResultResponse");
        lookupMap.put(39106, "ProtoMplGetCardToCardToken.MplGetCardToCardTokenResponse");
        lookupMap.put(39108, "ProtoMplSetCardToCardResult.MplSetCardToCardResultResponse");
        lookupMap.put(39109, "ProtoMplTransactionList.MplTransactionListResponse");
        lookupMap.put(39110, "ProtoMplTransactionInfo.MplTransactionInfoResponse");
        lookupMap.put(39200, "ProtoBillInquiryMci.BillInquiryMciResponse");
        lookupMap.put(39201, "ProtoBillInquiryTelecom.BillInquiryTelecomResponse");

        // Push 600xx
        lookupMap.put(60000, "ProtoPushLoginToken.PushLoginTokenResponse");
        lookupMap.put(60001, "ProtoPushTwoStepVerification.PushTwoStepVerificationResponse");
        lookupMap.put(60002, "ProtoPushUserInfoExpired.PushUserInfoExpiredResponse");
        lookupMap.put(60003, "ProtoPushRateSignaling.PushRateSignalingResponse");
        lookupMap.put(60004, "ProtoPushWalletPaymentVerified.PushWalletPaymentVerifiedResponse");
    }

    /**
     * list of actionId that can be doing without secure
     * (for send request)
     */
    private static void fillUnSecureList() {
        unSecure.add("2");
    }

    /**
     * list of actionIds that allowed continue processing even communication is not secure
     * (for receive response)
     */
    private static void fillUnSecureServerActionId() {
        unSecureResponseActionId.add("30001");
        unSecureResponseActionId.add("30002");
        unSecureResponseActionId.add("30003");
    }

    /**
     * list of actionId that can be doing without login
     * (for send request)
     */
    private static void fillUnLoginList() {
        unLogin.add("100");
        unLogin.add("101");
        unLogin.add("102");
        unLogin.add("500");
        unLogin.add("501");
        unLogin.add("502");
        unLogin.add("503");
        unLogin.add("131");
        unLogin.add("132");
        unLogin.add("138");
        unLogin.add("139");
        unLogin.add("140");
        unLogin.add("802");
        unLogin.add("506");
    }

    /**
     * list off classes(fragments) that don't have any animations for open and close state
     */
    private static void fillImmovableClasses() {
        generalImmovableClasses.add(FragmentShowAvatars.class.getName());
        generalImmovableClasses.add(FragmentShowImage.class.getName());
    }

    /**
     * list of actionId that will be storing in waitingActionIds list
     * and after that user login send this request again
     * (for send request)
     */
    private static void fillWaitingRequestActionIdAllowed() {
        waitingActionIds.add("201");
        waitingActionIds.add("310");
        waitingActionIds.add("410");
        //waitingActionIds.add("700");
        //waitingActionIds.add("701");
        //waitingActionIds.add("702");
        //waitingActionIds.add("703");
        //waitingActionIds.add("705");
    }


    private static void fillPriorityActionId() {
        priorityActionId.put(700, 50);
        priorityActionId.put(701, 50);
        priorityActionId.put(702, 50);
        priorityActionId.put(703, 50);
        priorityActionId.put(704, 50);
    }

    private static void fillForcePriorityActionId() {
        forcePriorityActionId.add(210);
        forcePriorityActionId.add(319);
    }

    private static void fillIgnoreErrorCodes() {
        ignoreErrorCodes.add(5); // timeout
        ignoreErrorCodes.add(617); // get room history not found
    }
}
