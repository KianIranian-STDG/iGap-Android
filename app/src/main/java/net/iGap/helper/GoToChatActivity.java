package net.iGap.helper;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.FragmentChat;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomAccess;

import java.util.List;

public class GoToChatActivity {

    private final String TAG = this.getClass().getName();
    private long roomid = 0;
    private long peerID = 0;

    private boolean fromUserLink = false;
    private boolean isNotJoin = false;
    private String userName = "";
    private long messageId = 0;

    public GoToChatActivity(long roomid) {
        this.roomid = roomid;

    }

    public GoToChatActivity setPeerID(long peerID) {
        this.peerID = peerID;
        return this;
    }


    public GoToChatActivity setfromUserLink(boolean fromUserLink) {
        this.fromUserLink = fromUserLink;
        return this;
    }

    public GoToChatActivity setisNotJoin(boolean isNotJoin) {
        this.isNotJoin = isNotJoin;
        return this;
    }

    public GoToChatActivity setuserName(String userName) {
        this.userName = userName;
        return this;
    }

    public GoToChatActivity setMessageID(long messageId) {
        this.messageId = messageId;
        return this;
    }

    public void startActivity(FragmentActivity activity) {

        String roomName = "";

        if (FragmentChat.mForwardMessages != null || HelperGetDataFromOtherApp.hasSharedData || FragmentChat.structIGSticker != null) {
            roomName = DbManager.getInstance().doRealmTask(realm -> {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomid).findFirst();

                if (realmRoom != null) {
                    if (realmRoom.getReadOnly() || hasSendMessagePermission() || (realmRoom.getType() != ProtoGlobal.Room.Type.CHAT && FragmentChat.structIGSticker != null) || (realmRoom.getType() == ProtoGlobal.Room.Type.CHAT && realmRoom.getChatRoom() != null && RealmRoom.isBot(realmRoom.getChatRoom().getPeerId()))) {
                        if (activity != null && !(activity).isFinishing()) {
                            new MaterialDialog.Builder(activity).title(R.string.dialog_readonly_chat).positiveText(R.string.ok).show();
                        }
                        return null;
                    }
                    return realmRoom.getTitle();
                } else if (peerID > 0) {
                    RealmRegisteredInfo _RegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, peerID);

                    if (_RegisteredInfo != null) {
                        return _RegisteredInfo.getDisplayName();
                    }
                }

                return "";
            });

            if (roomName == null) {
                return;
            }
        }

//        Log.e(TAG, "startActivity: activity ->" + activity.getClass().getName());

        if (HelperGetDataFromOtherApp.hasSharedData) {

            String message = G.context.getString(R.string.send_message_to) + " " + roomName;

            MaterialDialog.Builder mDialog = new MaterialDialog.Builder(activity).title(message).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    FragmentChat fragmentChat = new FragmentChat();
                    fragmentChat.setArguments(getBundle());
                    fragmentChatBackStack(activity);
                    new HelperFragment(activity.getSupportFragmentManager(), fragmentChat).setReplace(true).load();
                }
            }).onNegative((dialog, which) -> {
                HelperGetDataFromOtherApp.hasSharedData = false;
                //revert main rooms list from share mode
                if (activity instanceof ActivityMain) {
                    ((ActivityMain) activity).checkHasSharedData(false);
                }
            }).neutralText(R.string.another_room).onNeutral((dialog, which) -> dialog.dismiss());
            if (!activity.isFinishing()) {
                mDialog.show();
            }
        } else if (FragmentChat.structIGSticker != null) {
            String message = G.context.getString(R.string.send_message_to) + " " + roomName;

            MaterialDialog.Builder mDialog = new MaterialDialog.Builder(activity).title(message).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive((dialog, which) -> {
                FragmentChat fragmentChat = new FragmentChat();
                fragmentChat.setArguments(getBundle());
                fragmentChatBackStack(activity);
                new HelperFragment(activity.getSupportFragmentManager(), fragmentChat).setReplace(true).load();
            }).onNegative((dialog, which) -> {
                FragmentChat.structIGSticker = null;

                if (activity instanceof ActivityMain) {
                    ((ActivityMain) activity).checkHasSharedData(false);
                }

            }).neutralText(R.string.another_room).onNeutral((dialog, which) -> dialog.dismiss());

            if (!activity.isFinishing()) {
                mDialog.show();
            }

        } else if (FragmentChat.mForwardMessages != null) {

            String message = G.context.getString(R.string.send_forward_to) + " " + roomName + "?";

            MaterialDialog.Builder mDialog = new MaterialDialog.Builder(activity).title(message).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    loadChatFragment(activity);
                }
            }).onNegative((dialog, which) -> {
                disableForwardMessage(activity);
                FragmentChat.mForwardMessages = null;
            }).neutralText(R.string.another_room).onNeutral((dialog, which) -> dialog.dismiss());
            if (!(activity).isFinishing()) {
                mDialog.show();
            }
        } else {
            loadChatFragment(activity);
        }

    }

    private void disableForwardMessage(FragmentActivity activity) {
        if (activity instanceof ActivityMain) {
            ((ActivityMain) activity).setForwardMessage(false);
        }
    }

    private void loadChatFragment(FragmentActivity activity) {
        FragmentChat fragmentChat = new FragmentChat();
        fragmentChat.setArguments(getBundle());
        fragmentChatBackStack(activity);
        if (G.twoPaneMode) {
            if (activity instanceof ActivityMain) {
                ((ActivityMain) activity).goToChatPage(fragmentChat);
            } else {
                Log.wtf(this.getClass().getName(), "loadChatFragment");
            }
        } else {
            new HelperFragment(activity.getSupportFragmentManager(), fragmentChat).setReplace(true).load();
        }
    }

    public Bundle getBundle() {

        if (roomid == 0) {
            return null;
        }

        Bundle bundle = new Bundle();

        bundle.putLong("RoomId", roomid);

        if (peerID > 0) {
            bundle.putLong("peerId", peerID);
        }

        if (fromUserLink) {
            bundle.putBoolean("GoingFromUserLink", true);
        }

        if (isNotJoin) {
            bundle.putBoolean("ISNotJoin", true);
        }

        if (userName.length() > 0) {
            bundle.putString("UserName", userName);
        }

        if (messageId > 0) {
            bundle.putLong("MessageId", messageId);
        }

        return bundle;
    }

    private void fragmentChatBackStack(FragmentActivity activity) {
        if (activity != null) {
            for (int i = 0; i < activity.getSupportFragmentManager().getFragments().size(); i++) {
                List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();
                if (fragments.get(i) instanceof FragmentChat) {
                    FragmentChat fragmentChat = (FragmentChat) fragments.get(i);
                    if (fragmentChat.getRoomId() == roomid) {
                        activity.getSupportFragmentManager().beginTransaction().remove(fragmentChat).commit();
                    }
                }
            }
        }
    }

    private boolean hasSendMessagePermission() {
        return DbManager.getInstance().doRealmTask(realm -> {
            RealmRoomAccess realmRoomAccess = realm.where(RealmRoomAccess.class).equalTo("id", roomid + "_" + AccountManager.getInstance().getCurrentUser().getId()).findFirst();
            return realmRoomAccess != null && !realmRoomAccess.isCanPostMessage();
        });
    }
}
