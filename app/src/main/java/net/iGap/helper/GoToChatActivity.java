package net.iGap.helper;

import android.os.Bundle;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;

public class GoToChatActivity {

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

    public void startActivity() {

        if (FragmentChat.mForwardMessages != null) {
            Realm realm = Realm.getDefaultInstance();

            RealmRoom _realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomid).findFirst();

            if (_realmRoom != null && _realmRoom.getReadOnly()) {

                if (G.currentActivity != null) {

                    new MaterialDialog.Builder(G.currentActivity).title(R.string.dialog_readonly_chat).positiveText(R.string.ok).show();
                }

                realm.close();
                return;
            }

            realm.close();
        }

        FragmentChat fragmentChat = new FragmentChat();
        fragmentChat.setArguments(getBundle());
        new HelperFragment(fragmentChat).setReplace(false).load();
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
}
