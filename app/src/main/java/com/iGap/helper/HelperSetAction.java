package com.iGap.helper;

import android.util.Log;

import com.iGap.Config;
import com.iGap.G;
import com.iGap.proto.ProtoGlobal;
import com.iGap.request.RequestChatSetAction;

import java.util.ArrayList;

public class HelperSetAction {

    private static ArrayList<StructAction> structActions = new ArrayList<>();

    /**
     * set action for show in audience chat.
     *
     * @param roomId roomId that send action from that
     * @param action action that doing
     */

    public static void setAction(final long roomId, final ProtoGlobal.ClientAction action) {

        if (!checkExistAction(roomId, action)) {
            int randomNumber = HelperNumerical.generateRandomNumber(8);

            final StructAction structAction = new StructAction();
            structAction.roomId = roomId;
            structAction.currentTime = System.currentTimeMillis();
            structAction.randomKey = randomNumber;
            structAction.action = action;

            structActions.add(structAction);
            Log.i("WWW", "setAction");
            new RequestChatSetAction().chatSetAction(roomId, action, randomNumber);

            if (action == ProtoGlobal.ClientAction.TYPING) {
                timeOutChecking(structAction);
            }
        }
    }

    private static void timeOutChecking(final StructAction structAction) {
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sendCancel(structAction.currentTime)) {
                    removeStruct(structAction.randomKey);
                    new RequestChatSetAction().chatSetAction(structAction.roomId, ProtoGlobal.ClientAction.CANCEL, structAction.randomKey);
                } else {
                    timeOutChecking(structAction);
                }
            }
        }, Config.ACTION_CHECKING);
    }

    /**
     * send cancel for files
     *
     * @param roomId roomId that send action from that
     * @param action action that doing
     */

    public static void sendCancel(long roomId, ProtoGlobal.ClientAction action) {
        for (StructAction struct : structActions) {
            if (struct.roomId == roomId && struct.action == action) {
                removeStruct(struct.randomKey);
                new RequestChatSetAction().chatSetAction(struct.roomId, ProtoGlobal.ClientAction.CANCEL, struct.randomKey);
            }
        }
    }

    /**
     * check difference time from latest set action .
     * if time difference was more than Config.ACTION_TIME_OUT second
     * return true for send cancel request for that action
     *
     * @param startActionTime currentTimeMillis for start action or update action(when repeat it)
     * @return
     */

    private static boolean sendCancel(long startActionTime) {

        long difference;

        long currentTime = System.currentTimeMillis();
        difference = (currentTime - startActionTime);

        if (difference >= Config.ACTION_TIME_OUT) {
            return true;
        }

        return false;
    }


    private static class StructAction {

        public long roomId;
        public long currentTime;
        public int randomKey;
        public ProtoGlobal.ClientAction action;
    }

    /**
     * check that action with same roomId is exist or not.
     * return true if exist and update time otherwise just return false.
     *
     * @param roomId roomId that send action from that
     * @param action action that send
     * @return
     */
    private static boolean checkExistAction(long roomId, ProtoGlobal.ClientAction action) {
        for (StructAction struct : structActions) {
            if (struct.roomId == roomId && struct.action == action) {
                Log.i("WWW", "ExistAction");
                struct.currentTime = System.currentTimeMillis();
                return true;
            }
        }
        return false;
    }

    /**
     * remove item from structActions array with randomKey.
     *
     * @param randomKey randomKey that should remove struct with that
     */
    private static void removeStruct(int randomKey) {
        for (int i = 0; i < structActions.size(); i++) {
            if (structActions.get(i).randomKey == randomKey) {
                Log.i("WWW", "removeStruct");
                structActions.remove(i);
                break;
            }
        }
    }
}
