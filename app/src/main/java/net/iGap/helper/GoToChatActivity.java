package net.iGap.helper;

import android.content.Context;
import android.content.Intent;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityChat;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;

public class GoToChatActivity {

    private long roomid = 0;
    private long peerID = 0;
    private Context context = null;
    private boolean AddNewTask = false;
    private boolean fromCall = false;

    private boolean fromUserLink = false;
    private boolean isNotJoin = false;
    private String userName = "";
    private long messageId = 0;

    public GoToChatActivity(long roomid) {
        context = G.context;
        this.roomid = roomid;
    }

    public GoToChatActivity setPeerID(long peerID) {
        this.peerID = peerID;
        return this;
    }

    public GoToChatActivity setContext(Context context) {
        this.context = context;
        return this;
    }

    public GoToChatActivity setNewTask(boolean AddNewTask) {
        this.AddNewTask = AddNewTask;
        return this;
    }

    public GoToChatActivity setFromCall(boolean fromCall) {
        this.fromCall = fromCall;
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

        if (ActivityChat.mForwardMessages != null) {
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

        context.startActivity(getIntent());
    }

    public Intent getIntent() {

        if (roomid == 0) {
            return null;
        }

        Intent intent = new Intent(context, ActivityChat.class);
        intent.putExtra("RoomId", roomid);

        if (peerID > 0) {
            intent.putExtra("peerId", peerID);
        }

        if (fromCall) {
            intent.putExtra("FROM_CALL_Main", true);
        }

        if (AddNewTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        if (fromUserLink) {
            intent.putExtra("GoingFromUserLink", true);
        }

        if (isNotJoin) {
            intent.putExtra("ISNotJoin", true);
        }

        if (userName.length() > 0) {
            intent.putExtra("UserName", userName);
        }

        if (messageId > 0) {
            intent.putExtra("MessageId", messageId);
        }

        return intent;
    }
}
