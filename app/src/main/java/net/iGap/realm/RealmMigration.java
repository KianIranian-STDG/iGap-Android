/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.realm;

import net.iGap.G;
import net.iGap.model.PassCode;
import net.iGap.module.accountManager.AccountManager;

import javax.activation.MimetypesFileTypeMap;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmResults;
import io.realm.RealmSchema;

import static net.iGap.Config.REALM_SCHEMA_VERSION;

public class RealmMigration implements io.realm.RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 1) {
            RealmObjectSchema roomSchema = schema.get("RealmRoom");
            if (roomSchema != null) {
                roomSchema.addField("keepRoom", boolean.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmRoomMessageSchema = schema.get("RealmRoomMessage");
            if (realmRoomMessageSchema != null) {
                realmRoomMessageSchema.addField("authorRoomId", long.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 2) {
            RealmObjectSchema roomSchema = schema.get("RealmRoom");
            if (roomSchema != null) {
                roomSchema.addField("actionStateUserId", long.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmChannelRoomSchema = schema.get("RealmChannelRoom");
            if (realmChannelRoomSchema != null) {
                realmChannelRoomSchema.addField("seenId", long.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 3) {
            schema.create(RealmWallpaper.class.getSimpleName()).addField("lastTimeGetList", long.class, FieldAttribute.REQUIRED).addField("wallPaperList", byte[].class).addField("localList", byte[].class);
            oldVersion++;
        }

        if (oldVersion == 4) {
            schema.create(RealmPrivacy.class.getSimpleName()).addField("whoCanSeeMyAvatar", String.class).addField("whoCanInviteMeToChannel", String.class).addField("whoCanInviteMeToGroup", String.class).addField("whoCanSeeMyLastSeen", String.class);
            oldVersion++;
        }

        if (oldVersion == 5) {
            RealmObjectSchema realmRoomMessageSchema = schema.get(RealmRoomMessage.class.getSimpleName());
            if (realmRoomMessageSchema != null) {
                realmRoomMessageSchema.addField("previousMessageId", long.class, FieldAttribute.REQUIRED);
                realmRoomMessageSchema.addField("showTime", boolean.class, FieldAttribute.REQUIRED);
                realmRoomMessageSchema.addField("hasEmojiInText", boolean.class, FieldAttribute.REQUIRED);
                realmRoomMessageSchema.addField("linkInfo", String.class);
            }
            oldVersion++;
        }

        if (oldVersion == 6) {
            RealmObjectSchema realmRoomSchema = schema.get(RealmRoom.class.getSimpleName());
            if (realmRoomSchema != null) {
                realmRoomSchema.addField("lastScrollPositionMessageId", long.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 7) {
            RealmObjectSchema realmPhoneContacts = schema.create(RealmPhoneContacts.class.getSimpleName()).addField("phone", String.class).addField("firstName", String.class).addField("lastName", String.class);
            realmPhoneContacts.addPrimaryKey("phone");
            oldVersion++;
        }

        if (oldVersion == 8) {
            RealmObjectSchema roomSchema = schema.get(RealmRoom.class.getSimpleName());
            RealmObjectSchema realmRoomMessageSchema = schema.get(RealmRoomMessage.class.getSimpleName());
            if (roomSchema != null) {
                roomSchema.addRealmObjectField("firstUnreadMessage", realmRoomMessageSchema);
            }

            if (realmRoomMessageSchema != null) {
                realmRoomMessageSchema.addField("futureMessageId", long.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 9) {
            schema.create(RealmCallConfig.class.getSimpleName()).addField("voice_calling", boolean.class, FieldAttribute.REQUIRED).addField("video_calling", boolean.class, FieldAttribute.REQUIRED).addField("screen_sharing", boolean.class, FieldAttribute.REQUIRED).addField("IceServer", byte[].class);

            RealmObjectSchema realmCallLog = schema.create(RealmCallLog.class.getSimpleName()).addField("id", long.class, FieldAttribute.REQUIRED).addField("name", String.class).addField("time", long.class, FieldAttribute.REQUIRED).addField("logProto", byte[].class);
            realmCallLog.addPrimaryKey("id");
            oldVersion++;
        }

        if (oldVersion == 10) {
            RealmObjectSchema realmPrivacySchema = schema.get(RealmPrivacy.class.getSimpleName());
            if (realmPrivacySchema != null) {
                realmPrivacySchema.addField("whoCanVoiceCallToMe", String.class);
            }

            RealmObjectSchema realmGroupSchema = schema.get(RealmGroupRoom.class.getSimpleName());
            if (realmGroupSchema != null) {
                realmGroupSchema.addField("participants_count", int.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmChannelSchema = schema.get(RealmChannelRoom.class.getSimpleName());
            if (realmChannelSchema != null) {
                realmChannelSchema.addField("participants_count", int.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == 11) {
            RealmObjectSchema realmClientCondition = schema.get(RealmClientCondition.class.getSimpleName());

            RealmObjectSchema realmOfflineListen = schema.create(RealmOfflineListen.class.getSimpleName()).addField("id", long.class, FieldAttribute.REQUIRED).addField("offlineListen", long.class);
            realmOfflineListen.addPrimaryKey("id");

            if (realmClientCondition != null) {
                realmClientCondition.addRealmListField("offlineListen", realmOfflineListen);
            }

            RealmObjectSchema realmAvatar = schema.get(RealmAvatar.class.getSimpleName());
            if (realmAvatar != null) {
                realmAvatar.addIndex("ownerId");
            }

            RealmObjectSchema realmRoom = schema.get(RealmRoom.class.getSimpleName());
            if (realmRoom != null) {
                realmRoom.addField("isPinned", boolean.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == 12) {
            RealmObjectSchema realmGeoNearbyDistance = schema.create(RealmGeoNearbyDistance.class.getSimpleName()).addField("userId", long.class).addField("hasComment", boolean.class).addField("distance", int.class).addField("comment", String.class);
            realmGeoNearbyDistance.addPrimaryKey("userId");

            schema.create(RealmGeoGetConfiguration.class.getSimpleName()).addField("mapCache", String.class);
            oldVersion++;
        }

        if (oldVersion == 13) {
            RealmObjectSchema realmUserInfo = schema.get(RealmUserInfo.class.getSimpleName());
            if (realmUserInfo != null) {
                realmUserInfo.addField("isPassCode", boolean.class, FieldAttribute.REQUIRED)
                        .addField("isFingerPrint", boolean.class, FieldAttribute.REQUIRED)
                        .addField("kindPassCode", int.class, FieldAttribute.REQUIRED)
                        .addField("passCode", String.class);
            }
            oldVersion++;
        }

        if (oldVersion == 14) {
            RealmObjectSchema realmOfflineDelete = schema.get(RealmOfflineDelete.class.getSimpleName());
            if (realmOfflineDelete != null) {
                realmOfflineDelete.addField("both", boolean.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmRoom = schema.get(RealmRoom.class.getSimpleName());
            if (realmRoom != null) {
                realmRoom.addField("pinId", long.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema registeredInfo = schema.get(RealmRegisteredInfo.class.getSimpleName());
            if (registeredInfo != null) {
                registeredInfo.addField("bio", String.class);
            }
            oldVersion++;
        }

        if (oldVersion == 15) {
            RealmObjectSchema realmChannelRoom = schema.get(RealmChannelRoom.class.getSimpleName());
            if (realmChannelRoom != null) {
                realmChannelRoom.addField("verified", boolean.class, FieldAttribute.REQUIRED);
                realmChannelRoom.addField("reactionStatus", boolean.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == 16) {
            RealmObjectSchema realmUserInfo = schema.get(RealmUserInfo.class.getSimpleName());
            if (realmUserInfo != null) {
                realmUserInfo.addField("importContactLimit", boolean.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmAttachment = schema.get(RealmAttachment.class.getSimpleName());
            if (realmAttachment != null) {
                realmAttachment.addField("url", String.class);
            }

            RealmObjectSchema realmRoom = schema.get(RealmRoom.class.getSimpleName());
            if (realmRoom != null) {
                realmRoom.addField("lastScrollPositionOffset", int.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == 17) {
            RealmObjectSchema realmRoom = schema.get(RealmRoom.class.getSimpleName());
            if (realmRoom != null) {
                realmRoom.addField("pinMessageId", long.class, FieldAttribute.REQUIRED);
                realmRoom.addField("pinMessageIdDeleted", long.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmRoomMessage = schema.get(RealmRoomMessage.class.getSimpleName());
            if (realmRoomMessage != null) {
                if (realmRoomMessage.hasField("log")) {
                    realmRoomMessage.removeField("log");
                }

                if (realmRoomMessage.hasField("logMessage")) {
                    realmRoomMessage.removeField("logMessage");
                }

                realmRoomMessage.addField("Logs", byte[].class);
            }

            RealmObjectSchema realmRegisteredInfo = schema.get(RealmRegisteredInfo.class.getSimpleName());
            if (realmRegisteredInfo != null) {
                realmRegisteredInfo.addField("verified", boolean.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmContact = schema.get(RealmContacts.class.getSimpleName());
            if (realmContact != null) {
                realmContact.addField("verified", boolean.class, FieldAttribute.REQUIRED);
                realmContact.addField("mutual", boolean.class, FieldAttribute.REQUIRED);
                realmContact.addField("bio", String.class);
            }

            if (schema.contains("RealmRoomMessageLog")) {
                schema.remove("RealmRoomMessageLog");
            }

            oldVersion++;
        }

        if (oldVersion == 18) {
            RealmObjectSchema realmUserInfo = schema.get(RealmUserInfo.class.getSimpleName());
            if (realmUserInfo != null) {
                realmUserInfo.addField("pushNotificationToken", String.class);
            }

            oldVersion++;
        }

        if (oldVersion == 19) {

            RealmObjectSchema realmRoomMessageWallet = schema.create(RealmRoomMessageWallet.class.getSimpleName())
                    .addField("id", long.class, FieldAttribute.REQUIRED)
                    .addPrimaryKey("id")
                    .addField("fromUserId", long.class, FieldAttribute.REQUIRED)
                    .addField("toUserId", long.class, FieldAttribute.REQUIRED)
                    .addField("amount", long.class, FieldAttribute.REQUIRED)
                    .addField("traceNumber", long.class, FieldAttribute.REQUIRED)
                    .addField("invoiceNumber", long.class, FieldAttribute.REQUIRED)
                    .addField("payTime", int.class, FieldAttribute.REQUIRED)
                    .addField("type", String.class)
                    .addField("description", String.class);

            RealmObjectSchema realmRoomMessage = schema.get(RealmRoomMessage.class.getSimpleName());
            if (realmRoomMessage != null) {
                realmRoomMessage.addRealmObjectField("roomMessageWallet", realmRoomMessageWallet);
            }

            oldVersion++;
        }

        if (oldVersion == 20) {

            RealmObjectSchema realmIceServer = schema.create(RealmIceServer.class.getSimpleName()).addField("url", String.class).addField("username", String.class).addField("credential", String.class);

            RealmObjectSchema realmWallpaperProto = schema.create(RealmWallpaperProto.class.getSimpleName()).addRealmObjectField("file", schema.get(RealmAttachment.class.getSimpleName())).addField("color", String.class);

            RealmObjectSchema realmCallConfig = schema.get(RealmCallConfig.class.getSimpleName());
            if (realmCallConfig != null) {
                realmCallConfig.addRealmListField("realmIceServer", realmIceServer);

                if (realmCallConfig.hasField("IceServer")) {
                    realmCallConfig.removeField("IceServer");
                }
            }

            RealmObjectSchema realmWallpaper = schema.get(RealmWallpaper.class.getSimpleName());
            if (realmWallpaper != null) {
                realmWallpaper.addRealmListField("realmWallpaperProto", realmWallpaperProto);

                if (realmWallpaper.hasField("wallPaperList")) {
                    realmWallpaper.removeField("wallPaperList");
                }
            }

            oldVersion++;
        }

        if (oldVersion == 21) {

            schema.create(RealmDataUsage.class.getSimpleName()).addField("type", String.class)
                    .addField("downloadSize", long.class, FieldAttribute.REQUIRED)
                    .addField("uploadSize", long.class, FieldAttribute.REQUIRED)
                    .addField("connectivityType", boolean.class, FieldAttribute.REQUIRED)
                    .addField("numUploadedFiles", int.class, FieldAttribute.REQUIRED)
                    .addField("numDownloadedFile", int.class, FieldAttribute.REQUIRED);

            oldVersion++;
        }

        if (oldVersion == 22) {

            RealmObjectSchema realmUserInfo = schema.get(RealmUserInfo.class.getSimpleName());
            if (realmUserInfo != null) {
                realmUserInfo.addField("isPattern", boolean.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == 23) {

            RealmObjectSchema realmRegisteredInfo = schema.get(RealmRegisteredInfo.class.getSimpleName());
            if (realmRegisteredInfo != null) {
                realmRegisteredInfo.addField("isBot", boolean.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == 24) {

            RealmObjectSchema realmRoom = schema.get(RealmRoom.class.getSimpleName());
            if (realmRoom != null) {
                realmRoom.addField("priority", int.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == 25) {

            RealmObjectSchema realmRoom = schema.get(RealmRoom.class.getSimpleName());
            if (realmRoom != null) {
                realmRoom.addField("isFromPromote", boolean.class, FieldAttribute.REQUIRED);
                realmRoom.addField("promoteId", long.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmPrivacy = schema.get(RealmPrivacy.class.getSimpleName());
            if (realmPrivacy != null) {
                realmPrivacy.addField("whoCanVideoCallToMe", String.class);
            }

            oldVersion++;
        }


        if (oldVersion == 26) {

            RealmObjectSchema realmAdditional = schema.create(RealmAdditional.class.getSimpleName())
                    .addField("id", long.class, FieldAttribute.REQUIRED)
                    .addField("additionalData", String.class)
                    .addField("AdditionalType", int.class, FieldAttribute.REQUIRED)
                    .addPrimaryKey("id");

            RealmObjectSchema realmRoomMessageSchema = schema.get(RealmRoomMessage.class.getSimpleName());
            if (realmRoomMessageSchema != null) {
                realmRoomMessageSchema.addRealmObjectField("realmAdditional", realmAdditional);
            }

            oldVersion++;
        }

        if (oldVersion == 27) {

            RealmObjectSchema realmStickerDetails = schema.create("RealmStickersDetails")
                    .addField("id", long.class, FieldAttribute.REQUIRED)
                    .addField("refId", long.class, FieldAttribute.REQUIRED)
                    .addField("fileSize", long.class, FieldAttribute.REQUIRED)
                    .addField("st_id", String.class)
                    .addField("name", String.class)
                    .addField("token", String.class)
                    .addField("uri", String.class)
                    .addField("fileName", String.class)
                    .addField("groupId", String.class)
                    .addField("sort", int.class, FieldAttribute.REQUIRED);

            schema.create("RealmStickers")
                    .addField("id", long.class, FieldAttribute.REQUIRED)
                    .addField("createdAt", long.class, FieldAttribute.REQUIRED)
                    .addField("refId", long.class, FieldAttribute.REQUIRED)
                    .addField("avatarSize", long.class, FieldAttribute.REQUIRED)
                    .addField("createdBy", long.class, FieldAttribute.REQUIRED)
                    .addField("price", long.class, FieldAttribute.REQUIRED)
                    .addField("st_id", String.class)
                    .addField("name", String.class)
                    .addField("avatarToken", String.class)
                    .addField("uri", String.class)
                    .addField("avatarName", String.class)
                    .addField("isVip", boolean.class, FieldAttribute.REQUIRED)
                    .addField("approved", boolean.class, FieldAttribute.REQUIRED)
                    .addField("isFavorite", boolean.class, FieldAttribute.REQUIRED)
                    .addField("sort", int.class, FieldAttribute.REQUIRED)
                    .addRealmListField("realmStickersDetails", realmStickerDetails);

            oldVersion++;
        }

        if (oldVersion == 28) {
            RealmObjectSchema realmRoomDraft = schema.get(RealmRoomDraft.class.getSimpleName());
            if (realmRoomDraft != null) {
                realmRoomDraft.addField("draftTime", long.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmAttachment = schema.get(RealmAttachment.class.getSimpleName());
            if (realmAttachment != null) {
                realmAttachment.addIndex("cacheId");
            }
            oldVersion++;
        }

        if (oldVersion == 29) {
            RealmObjectSchema realmUserInfo = schema.get(RealmUserInfo.class.getSimpleName());
            if (realmUserInfo != null) {
                realmUserInfo.addField("representPhoneNumber", String.class);
            }
            oldVersion++;
        }

        if (oldVersion == 30) {
            RealmObjectSchema realmAttachment = schema.get(RealmAttachment.class.getSimpleName());
            if (realmAttachment != null) {
                realmAttachment.addIndex("token");
            }

            RealmObjectSchema realmStickers = schema.get("RealmStickers");
            if (realmStickers.hasField("id")) {
                realmStickers.removeField("id");
            }

            RealmObjectSchema realmStickerDetails = schema.get("RealmStickersDetails");
            if (realmStickerDetails.hasField("id")) {
                realmStickerDetails.removeField("id");
            }

            oldVersion++;
        }

        if (oldVersion == 31) {

            RealmObjectSchema realmRoomMessageWalletCardToCard = schema.create(RealmRoomMessageWalletCardToCard.class.getSimpleName())
                    .addField("fromUserId", long.class, FieldAttribute.REQUIRED)
                    .addField("toUserId", long.class, FieldAttribute.REQUIRED)
                    .addField("amount", long.class, FieldAttribute.REQUIRED)
                    .addField("bankName", String.class)
                    .addField("destBankName", String.class)
                    .addField("cardOwnerName", String.class)
                    .addField("orderId", long.class, FieldAttribute.REQUIRED)
                    .addField("traceNumber", long.class, FieldAttribute.REQUIRED)
                    .addField("token", String.class)
                    .addField("status", boolean.class, FieldAttribute.REQUIRED)
                    .addField("sourceCardNumber", String.class)
                    .addField("destCardNumber", String.class)
                    .addField("rrn", String.class)
                    .addField("requestTime", int.class, FieldAttribute.REQUIRED);


            RealmObjectSchema realmRoomMessageWalletMoneyTransfer = schema.create(RealmRoomMessageWalletMoneyTransfer.class.getSimpleName())
                    .addField("fromUserId", long.class, FieldAttribute.REQUIRED)
                    .addField("toUserId", long.class, FieldAttribute.REQUIRED)
                    .addField("amount", long.class, FieldAttribute.REQUIRED)
                    .addField("traceNumber", long.class, FieldAttribute.REQUIRED)
                    .addField("invoiceNumber", long.class, FieldAttribute.REQUIRED)
                    .addField("payTime", int.class, FieldAttribute.REQUIRED)
                    .addField("description", String.class)
                    .addField("cardNumber", String.class)
                    .addField("rrn", long.class, FieldAttribute.REQUIRED);

            RealmObjectSchema realmRoomMessageWalletPayment = schema.create(RealmRoomMessageWalletPayment.class.getSimpleName())
                    .addField("fromUserId", long.class, FieldAttribute.REQUIRED)
                    .addField("toUserId", long.class, FieldAttribute.REQUIRED)
                    .addField("amount", long.class, FieldAttribute.REQUIRED)
                    .addField("traceNumber", long.class, FieldAttribute.REQUIRED)
                    .addField("invoiceNumber", long.class, FieldAttribute.REQUIRED)
                    .addField("payTime", int.class, FieldAttribute.REQUIRED)
                    .addField("description", String.class)
                    .addField("cardNumber", String.class)
                    .addField("rrn", long.class, FieldAttribute.REQUIRED);


            RealmObjectSchema realmRoomMessageWallet = schema.get(RealmRoomMessageWallet.class.getSimpleName());
            if (realmRoomMessageWallet.hasField("fromUserId")) {
                realmRoomMessageWallet.removeField("fromUserId");
            }
            if (realmRoomMessageWallet.hasField("toUserId")) {
                realmRoomMessageWallet.removeField("toUserId");
            }
            if (realmRoomMessageWallet.hasField("amount")) {
                realmRoomMessageWallet.removeField("amount");
            }
            if (realmRoomMessageWallet.hasField("traceNumber")) {
                realmRoomMessageWallet.removeField("traceNumber");
            }
            if (realmRoomMessageWallet.hasField("invoiceNumber")) {
                realmRoomMessageWallet.removeField("invoiceNumber");
            }
            if (realmRoomMessageWallet.hasField("payTime")) {
                realmRoomMessageWallet.removeField("payTime");
            }
            if (realmRoomMessageWallet.hasField("description")) {
                realmRoomMessageWallet.removeField("description");
            }
            realmRoomMessageWallet.addRealmObjectField("realmRoomMessageWalletCardToCard", realmRoomMessageWalletCardToCard);
            realmRoomMessageWallet.addRealmObjectField("realmRoomMessageWalletPayment", realmRoomMessageWalletPayment);
            realmRoomMessageWallet.addRealmObjectField("realmRoomMessageWalletMoneyTransfer", realmRoomMessageWalletMoneyTransfer);

            oldVersion++;
        }

        if (oldVersion == 32) {
            RealmObjectSchema realmGroupRoom = schema.get(RealmGroupRoom.class.getSimpleName());
            if (realmGroupRoom != null) {
                realmGroupRoom.addField("startFrom", int.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 33) {

            RealmResults<DynamicRealmObject> realmCallLogs = realm.where("RealmCallLog").findAll();
            realmCallLogs.deleteAllFromRealm();

            RealmObjectSchema realmCallLog = schema.get(RealmCallLog.class.getSimpleName());
            if (realmCallLog != null) {
                if (realmCallLog.hasField("name")) {
                    realmCallLog.removeField("name");
                }
                if (realmCallLog.hasField("time")) {
                    realmCallLog.removeField("time");
                }
                if (realmCallLog.hasField("logProto")) {
                    realmCallLog.removeField("logProto");
                }

                RealmObjectSchema realmRegisteredInfoSchema = schema.get(RealmRegisteredInfo.class.getSimpleName());

                realmCallLog.addField("type", String.class).
                        addField("status", String.class).
                        addRealmObjectField("user", realmRegisteredInfoSchema).
                        addField("offerTime", int.class, FieldAttribute.REQUIRED).
                        addField("duration", int.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 34) {
            RealmObjectSchema realmCallLog = schema.get(RealmCallLog.class.getSimpleName());
            if (realmCallLog != null) {
                realmCallLog.addField("logId", long.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == 35) {

            RealmObjectSchema realmNotificationRoomMessage = schema.create(RealmNotificationRoomMessage.class.getSimpleName())
                    .addField("roomId", long.class, FieldAttribute.REQUIRED)
                    .addField("messageId", long.class, FieldAttribute.REQUIRED)
                    .addField("createTime", long.class, FieldAttribute.REQUIRED);

            realmNotificationRoomMessage.addPrimaryKey("messageId");

            oldVersion++;
        }

        if (oldVersion == 36) {

            RealmObjectSchema realmUserInfo = schema.get(RealmUserInfo.class.getSimpleName());
            if (realmUserInfo != null) {
                realmUserInfo.addField("accessToken", String.class);
            }

            RealmObjectSchema realmDownloadSong = schema.create(RealmDownloadSong.class.getSimpleName())
                    .addField("id", long.class, FieldAttribute.REQUIRED)
                    .addField("path", String.class)
                    .addField("displayName", String.class)
                    .addField("englishDisplayName", String.class)
                    .addField("savedName", String.class)
                    .addField("artistId", long.class, FieldAttribute.REQUIRED)
                    .addField("albumId", long.class, FieldAttribute.REQUIRED)
                    .addField("isFavorite", boolean.class, FieldAttribute.REQUIRED);

            realmDownloadSong.addPrimaryKey("id");

            oldVersion++;
        }

        if (oldVersion == 37) {

            RealmObjectSchema realmWallpaper = schema.get(RealmWallpaper.class.getSimpleName());
            if (realmWallpaper != null) {
                realmWallpaper.addField("type", int.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }


        if (oldVersion == 38) { // REALM_LATEST_MIGRATION_VERSION = 38

            RealmObjectSchema realmKuknos = schema.create(RealmKuknos.class.getSimpleName())
                    .addField("kuknosSeedKey", String.class)
                    .addField("kuknosPublicKey", String.class)
                    .addField("kuknosPIN", String.class)
                    .addField("kuknosMnemonic", String.class);

            RealmObjectSchema realmUserInfo = schema.get(RealmUserInfo.class.getSimpleName());
            if (realmUserInfo != null) {
                realmUserInfo.addRealmObjectField("kuknosM", realmKuknos);
            }

            oldVersion++;
        }

        if (oldVersion == 39) {
            DynamicRealmObject realmUserInfo = realm.where("RealmUserInfo").findFirst();
            if (realmUserInfo != null) {
                DynamicRealmObject userInfo = realmUserInfo.getObject("userInfo");
                if (userInfo != null) {
                    long userId = userInfo.getLong("id");
                    AccountManager.getInstance().addNewUser(userId, userInfo.getString("phoneNumber"), userInfo.getString("displayName"));
                }
            }

            oldVersion++;
        }

        if (oldVersion == 40) {
            DynamicRealmObject realmUserInfo = realm.where("RealmUserInfo").findFirst();
            boolean isPassCode = false;
            boolean isPattern = false;
            boolean isFingerPrint = false;
            String passCode = null;
            int kindPassCode = 0;

            if (realmUserInfo != null) {
                isPassCode = realmUserInfo.getBoolean("isPassCode");
                isPattern = realmUserInfo.getBoolean("isPattern");
                isFingerPrint = realmUserInfo.getBoolean("isFingerPrint");
                passCode = realmUserInfo.getString("passCode");
                kindPassCode = realmUserInfo.getInt("kindPassCode");
            }
            PassCode.initPassCode(G.context, isPassCode, isPattern, isFingerPrint, passCode, kindPassCode);

            RealmObjectSchema realmUserInfoShem = schema.get(RealmUserInfo.class.getSimpleName());
            if (realmUserInfoShem != null) {
                if (realmUserInfoShem.hasField("importContactLimit")) {
                    realmUserInfoShem.removeField("importContactLimit");
                }
                if (realmUserInfoShem.hasField("isPassCode")) {
                    realmUserInfoShem.removeField("isPassCode");
                }
                if (realmUserInfoShem.hasField("isPattern")) {
                    realmUserInfoShem.removeField("isPattern");
                }
                if (realmUserInfoShem.hasField("isFingerPrint")) {
                    realmUserInfoShem.removeField("isFingerPrint");
                }
                if (realmUserInfoShem.hasField("passCode")) {
                    realmUserInfoShem.removeField("passCode");
                }
                if (realmUserInfoShem.hasField("kindPassCode")) {
                    realmUserInfoShem.removeField("kindPassCode");
                }
            }

            oldVersion++;
        }

        if (oldVersion == 41) {
            RealmObjectSchema realmUser = schema.get(RealmUserInfo.class.getSimpleName());
            if (realmUser != null) {
                realmUser.addField("isWalletRegister", boolean.class, FieldAttribute.REQUIRED);
                realmUser.addField("isWalletActive", boolean.class, FieldAttribute.REQUIRED);
                realmUser.addField("isMplActive", boolean.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }

        if (oldVersion == 42) {
            RealmObjectSchema realmUser = schema.get(RealmUserInfo.class.getSimpleName());
            if (realmUser != null) {
                realmUser.addField("walletAmount", long.class, FieldAttribute.REQUIRED);
                realmUser.addField("ivandScore", long.class, FieldAttribute.REQUIRED);
            }

            oldVersion++;
        }


        if (oldVersion == 43) {

            RealmObjectSchema realmMB = schema.create(RealmMobileBankCards.class.getSimpleName())
                    .addField("cardName", String.class)
                    .addField("cardNumber", String.class)
                    .addField("bankName", String.class)
                    .addField("expireDate", String.class)
                    .addField("isOrigin", boolean.class, FieldAttribute.REQUIRED);

            realmMB.addPrimaryKey("cardNumber");

            oldVersion++;
        }

        if (oldVersion == 44) {
            RealmObjectSchema realmBill = schema.create(RealmRoomMessageWalletBill.class.getSimpleName())
                    .addField("orderId", long.class, FieldAttribute.REQUIRED)
                    .addField("fromUserId", long.class, FieldAttribute.REQUIRED)
                    .addField("myToken", String.class)
                    .addField("token", long.class, FieldAttribute.REQUIRED)
                    .addField("amount", long.class, FieldAttribute.REQUIRED)
                    .addField("payId", String.class)
                    .addField("billId", String.class)
                    .addField("billType", String.class)
                    .addField("cardNumber", String.class)
                    .addField("merchantName", String.class)
                    .addField("terminalNo", long.class, FieldAttribute.REQUIRED)
                    .addField("rrn", long.class, FieldAttribute.REQUIRED)
                    .addField("traceNumber", long.class, FieldAttribute.REQUIRED)
                    .addField("requestTime", int.class, FieldAttribute.REQUIRED)
                    .addField("status", boolean.class, FieldAttribute.REQUIRED);

            realmBill.addPrimaryKey("orderId");

            RealmObjectSchema realmTopup = schema.create(RealmRoomMessageWalletTopup.class.getSimpleName())
                    .addField("orderId", long.class, FieldAttribute.REQUIRED)
                    .addField("fromUserId", long.class, FieldAttribute.REQUIRED)
                    .addField("myToken", String.class)
                    .addField("token", long.class, FieldAttribute.REQUIRED)
                    .addField("amount", long.class, FieldAttribute.REQUIRED)
                    .addField("requestMobileNumber", String.class)
                    .addField("chargeMobileNumber", String.class)
                    .addField("topupType", int.class, FieldAttribute.REQUIRED)
                    .addField("cardNumber", String.class)
                    .addField("merchantName", String.class)
                    .addField("terminalNo", long.class, FieldAttribute.REQUIRED)
                    .addField("rrn", long.class, FieldAttribute.REQUIRED)
                    .addField("traceNumber", long.class, FieldAttribute.REQUIRED)
                    .addField("requestTime", int.class, FieldAttribute.REQUIRED)
                    .addField("status", boolean.class, FieldAttribute.REQUIRED);

            realmTopup.addPrimaryKey("orderId");

            RealmObjectSchema realmRoomMessageWallet = schema.get(RealmRoomMessageWallet.class.getSimpleName());
            if (realmRoomMessageWallet != null) {
                realmRoomMessageWallet.addRealmObjectField("realmRoomMessageWalletTopup", realmTopup);
                realmRoomMessageWallet.addRealmObjectField("realmRoomMessageWalletBill", realmBill);
            }

            oldVersion++;
        }


        if (oldVersion == 45) {

            RealmObjectSchema realmMB = schema.create(RealmMobileBankAccounts.class.getSimpleName())
                    .addField("accountNumber", String.class)
                    .addField("accountName", String.class);
            realmMB.addPrimaryKey("accountNumber");

            RealmObjectSchema realmUser = schema.get(RealmUserInfo.class.getSimpleName());
            if (realmUser != null) {
                realmUser.addField("nationalCode", String.class);
            }

            if (schema.contains("RealmStickers")) {
                schema.remove("RealmStickers");
            }

            if (schema.contains("RealmStickersDetails")) {
                schema.remove("RealmStickersDetails");
            }

            RealmObjectSchema realmStickerItem = schema.create(RealmStickerItem.class.getSimpleName())
                    .addField("id", String.class)
                    .addField("fileName", String.class)
                    .addField("groupId", String.class)
                    .addField("name", String.class)
                    .addField("token", String.class)
                    .addField("isFavorite", boolean.class)
                    .addField("recentTime", long.class)
                    .addField("fileSize", long.class);

            schema.create(RealmStickerGroup.class.getSimpleName())
                    .addField("id", String.class)
                    .addField("name", String.class)
                    .addField("type", String.class)
                    .addField("avatarName", String.class)
                    .addField("avatarToken", String.class)
                    .addField("categoryId", String.class)
                    .addField("isGiftable", boolean.class)
                    .addField("avatarSize", long.class)
                    .addRealmListField("stickerItems", realmStickerItem);

            oldVersion++;
        }

        if (oldVersion == 46) {

            RealmObjectSchema realmMbCards = schema.get(RealmMobileBankCards.class.getSimpleName());
            if (realmMbCards != null) {
                realmMbCards.addField("status", String.class);
            }

            RealmObjectSchema realmMbDeposits = schema.get(RealmMobileBankAccounts.class.getSimpleName());
            if (realmMbDeposits != null) {
                realmMbDeposits.addField("status", String.class);
            }

            oldVersion++;
        }

        if (oldVersion == 47) {

            RealmObjectSchema realmPostMessageRights = schema.create(RealmPostMessageRights.class.getSimpleName())
                    .addField("canSendGif", boolean.class)
                    .addField("canSendLink", boolean.class)
                    .addField("canSendMedia", boolean.class)
                    .addField("canSendSticker", boolean.class)
                    .addField("canSendText", boolean.class);

            schema.create(RealmRoomAccess.class.getSimpleName())
                    .addField("id", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("userId", long.class)
                    .addField("roomId", long.class)
                    .addField("canAddNewAdmin", boolean.class)
                    .addField("canAddNewMember", boolean.class)
                    .addField("canBanMember", boolean.class)
                    .addField("canDeleteMessage", boolean.class)
                    .addField("canEditMessage", boolean.class)
                    .addField("canGetMemberList", boolean.class)
                    .addField("canModifyRoom", boolean.class)
                    .addField("canPinMessage", boolean.class)
                    .addRealmObjectField("realmPostMessageRights", realmPostMessageRights);

            oldVersion++;
        }

        if (oldVersion == 48) {
            RealmObjectSchema realmAttachment = schema.get(RealmAttachment.class.getSimpleName());
            if (realmAttachment != null) {
                realmAttachment.addField("mimeType", String.class);

                RealmResults<DynamicRealmObject> realmAttachments = realm.where(RealmAttachment.class.getSimpleName()).findAll();
                for (DynamicRealmObject attachment : realmAttachments) {
                    if (attachment != null) {
                        String fileName = attachment.getString("name");
                        if (fileName != null) {
                            String mimeType = new MimetypesFileTypeMap().getContentType(fileName);
                            attachment.setString("mimeType", mimeType);
                        }
                    }
                }
            }

            oldVersion++;
        }

        if (oldVersion == 49) {
            RealmObjectSchema realmMbCards = schema.get(RealmKuknos.class.getSimpleName());
            if (realmMbCards != null) {
                realmMbCards.addField("iban", String.class);
            }

            oldVersion++;
        }

        if (oldVersion == 50) {
            RealmObjectSchema realmAttachmentSchema = schema.get(RealmAttachment.class.getSimpleName());

            RealmObjectSchema realmStoryProto = schema.create(RealmStoryProto.class.getSimpleName())
                    .addField("caption", String.class)
                    .addField("fileToken", String.class)
                    .addField("imagePath", String.class)
                    .addRealmObjectField("file", realmAttachmentSchema)
                    .addField("createdAt", long.class)
                    .addField("userId", long.class)
                    .addField("storyId", long.class)
                    .addField("id", long.class)
                    .addField("isSeen", boolean.class)
                    .addField("viewCount", int.class)
                    .addField("status", int.class);


            RealmObjectSchema realmStorySchema = schema.create(RealmStory.class.getSimpleName())
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("userId", long.class)
                    .addField("isSeenAll", boolean.class)
                    .addField("isSentAll", boolean.class)
                    .addField("isUploadedAll", boolean.class)
                    .addField("indexOfSeen", int.class)
                    .addRealmObjectField("realmStoryProtos", realmStoryProto);

            oldVersion++;
        }
    }

    @Override
    public int hashCode() {
        return REALM_SCHEMA_VERSION;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof RealmMigration);
    }
}
