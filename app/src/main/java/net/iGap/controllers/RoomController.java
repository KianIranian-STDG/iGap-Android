package net.iGap.controllers;

import net.iGap.G;
import net.iGap.helper.FileLog;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.network.IG_RPC;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRoom;

public class RoomController extends BaseController {

    private static volatile RoomController[] instance = new RoomController[AccountManager.MAX_ACCOUNT_COUNT];
    private String TAG = getClass().getSimpleName();

    public static RoomController getInstance(int account) {
        RoomController localInstance = instance[account];
        if (localInstance == null) {
            synchronized (RoomController.class) {
                localInstance = instance[account];
                if (localInstance == null) {
                    instance[account] = localInstance = new RoomController(account);
                }
            }
        }
        return localInstance;
    }

    public RoomController(int currentAccount) {
        super(currentAccount);
    }

    public void channelUpdateReactionStatus(long roomId, boolean status) {

        IG_RPC.Channel_Update_Reaction_Status req = new IG_RPC.Channel_Update_Reaction_Status();
        req.roomId = roomId;
        req.reactionStatus = status;

        getRequestManager().sendRequest(req, (response, error) -> {
            if (response != null) {
                IG_RPC.Res_Channel_Update_Reaction_Status res = (IG_RPC.Res_Channel_Update_Reaction_Status) response;
                RealmChannelRoom.updateReactionStatus(res.roomId, res.reactionStatus);
                getEventManager().postEvent(EventManager.CHANNEL_UPDATE_VOTE, res.roomId, res.reactionStatus);

            } else {
                IG_RPC.Error e = new IG_RPC.Error();
                FileLog.e("Channel Update Reaction Status -> Major" + e.major + "Minor" + e.minor);
            }
        });
    }

    public void channelUpdateSignature(long roomId, boolean signature) {

        IG_RPC.Channel_Update_Signature req = new IG_RPC.Channel_Update_Signature();
        req.roomId = roomId;
        req.signature = signature;

        getRequestManager().sendRequest(req, (response, error) -> {
            if (response != null) {
                IG_RPC.Res_Channel_Update_Signature res = (IG_RPC.Res_Channel_Update_Signature) response;
                RealmRoom.updateSignature(res.roomId, res.signature);
                getEventManager().postEvent(EventManager.CHANNEL_UPDATE_SIGNATURE, res.roomId, res.signature);

            } else {
                IG_RPC.Error e = new IG_RPC.Error();
                FileLog.e("Channel Update Signature -> Major" + e.major + "Minor" + e.minor);
            }
        });
    }

    public void clientPinRoom(long roomId, boolean pin) {

        IG_RPC.Client_Pin_Room req = new IG_RPC.Client_Pin_Room();
        req.roomId = roomId;
        req.pin = pin;

        getRequestManager().sendRequest(req, (response, error) -> {
            if (response != null) {
                IG_RPC.Res_Client_Pin_Room res = (IG_RPC.Res_Client_Pin_Room) response;
                if (res.pinId > 0) {
                    RealmRoom.updatePin(res.roomId, true, res.pinId);
                } else {
                    RealmRoom.updatePin(res.roomId, false, res.pinId);
                }
            } else {
                IG_RPC.Error e = new IG_RPC.Error();
                FileLog.e("Client Pin Room -> Major" + e.major + "Minor" + e.minor);
            }
        });

    }

    public void clientMuteRoom(long roomId, boolean roomMute) {

        IG_RPC.Client_Mute_Room req = new IG_RPC.Client_Mute_Room();
        req.roomId = roomId;
        req.roomMute = roomMute ? ProtoGlobal.RoomMute.MUTE : ProtoGlobal.RoomMute.UNMUTE;

        getRequestManager().sendRequest(req, (response, error) -> {
            if (response != null) {
                IG_RPC.Res_Client_Mute_Room res = (IG_RPC.Res_Client_Mute_Room) response;
                DbManager.getInstance().doRealmTransaction(realm -> {
                    RealmRoom room = realm.where(RealmRoom.class).equalTo("id", res.roomId).findFirst();
                    if (room != null) {
                        room.setMute(res.roomMute);
                    }
                });
            } else {
                IG_RPC.Error e = new IG_RPC.Error();
                FileLog.e("Client Mute Room -> Major" + e.major + "Minor" + e.minor);
            }

        });
    }

    public void chatDeleteRoom(long roomId) {

        IG_RPC.Chat_Delete_Room req = new IG_RPC.Chat_Delete_Room();
        req.roomId = roomId;

        getRequestManager().sendRequest(req, (response, error) -> {
            if (response != null) {
                IG_RPC.Res_Chat_Delete_Room res = (IG_RPC.Res_Chat_Delete_Room) response;
                RealmRoom.deleteRoom(res.roomId);
                if (G.onChatDelete != null) {
                    G.onChatDelete.onChatDelete(res.roomId);
                }

                if (G.onChatDeleteInRoomList != null) {
                    G.onChatDeleteInRoomList.onChatDelete(res.roomId);
                }
            } else {
                IG_RPC.Error e = new IG_RPC.Error();
                FileLog.e("Chat Delete Room -> Major" + e.major + "Minor" + e.minor);
            }
        });

    }

    public void groupDeleteRoom(long roomId) {

        IG_RPC.Group_Delete_Room req = new IG_RPC.Group_Delete_Room();
        req.roomId = roomId;

        getRequestManager().sendRequest(req, (response, error) -> {
            if (response != null) {
                IG_RPC.Res_Group_Delete_Room res = (IG_RPC.Res_Group_Delete_Room) response;
                RealmRoom.deleteRoom(res.roomId);
                if (G.onGroupDelete != null) {
                    G.onGroupDelete.onGroupDelete(res.roomId);
                }
                if (G.onGroupDeleteInRoomList != null) {
                    G.onGroupDeleteInRoomList.onGroupDelete(res.roomId);
                }

            } else {
                IG_RPC.Error e = new IG_RPC.Error();
                FileLog.e("Group Delete Room -> Major" + e.major + "Minor" + e.minor);
            }
        });
    }

    public void groupLeft(long roomId) {

        IG_RPC.Group_Left req = new IG_RPC.Group_Left();
        req.roomId = roomId;

        getRequestManager().sendRequest(req, (response, error) -> {
            if (response != null) {
                IG_RPC.Res_Group_Left res = (IG_RPC.Res_Group_Left) response;

                if (AccountManager.getInstance().getCurrentUser().getId() == res.memberId) {
                    RealmRoom.deleteRoom(roomId);

                    if (G.onGroupLeft != null) {
                        G.onGroupLeft.onGroupLeft(roomId, res.memberId);
                    }
                    if (G.onGroupDeleteInRoomList != null) {
                        G.onGroupDeleteInRoomList.onGroupDelete(roomId);
                    }
                } else {
                    RealmMember.kickMember(res.roomId, res.memberId);
                }
            } else {
                IG_RPC.Error e = new IG_RPC.Error();
                FileLog.e("Group Left -> Major" + e.major + "Minor" + e.minor);
            }
        });
    }
}
