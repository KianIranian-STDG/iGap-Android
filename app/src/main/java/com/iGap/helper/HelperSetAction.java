package com.iGap.helper;

import com.iGap.Config;
import com.iGap.G;
import com.iGap.proto.ProtoGlobal;
import com.iGap.request.RequestChatSetAction;

import java.util.ArrayList;

public class HelperSetAction {

    private static ArrayList<StructAction> structActions = new ArrayList<>();

    /**
     * set action for showing audience typing.
     *
     * @param roomId roomId that send action from that
     */

    public static void setActionTyping(final long roomId) {

        if (!checkExistAction(roomId, ProtoGlobal.ClientAction.TYPING)) {
            int randomNumber = HelperNumerical.generateRandomNumber(8);

            final StructAction structAction = new StructAction();
            structAction.roomId = roomId;
            structAction.currentTime = System.currentTimeMillis();
            structAction.randomKey = randomNumber;
            structAction.action = ProtoGlobal.ClientAction.TYPING;

            structActions.add(structAction);
            new RequestChatSetAction().chatSetAction(roomId, ProtoGlobal.ClientAction.TYPING, randomNumber);

            timeOutChecking(structAction);
        }
    }

    /**
     * set action for showing audience action
     *
     * @param roomId    roomId that send action from that
     * @param messageId unique number that we have in start upload and end of that
     * @param action    action that doing
     */

    public static void setActionFiles(long roomId, long messageId, ProtoGlobal.ClientAction action) {
        if (!checkExistAction(roomId, action)) {
            int randomNumber = HelperNumerical.generateRandomNumber(8);

            final StructAction structAction = new StructAction();
            structAction.roomId = roomId;
            structAction.currentTime = System.currentTimeMillis();
            structAction.randomKey = randomNumber;
            structAction.action = action;
            structAction.messageId = messageId;

            structActions.add(structAction);
            new RequestChatSetAction().chatSetAction(roomId, action, randomNumber);
        }
    }

    private static void timeOutChecking(final StructAction structAction) {
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (autoCancel(structAction.currentTime)) {
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
     * @param messageId unique number that we have in start upload and end of that
     */

    public static void sendCancel(long messageId) {
        for (StructAction struct : structActions) {
            if (struct.messageId == messageId) {
                new RequestChatSetAction().chatSetAction(struct.roomId, ProtoGlobal.ClientAction.CANCEL, struct.randomKey);
                removeStruct(struct.randomKey);
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

    private static boolean autoCancel(long startActionTime) {

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
        public long messageId; // messageId is a unique number that we have it in start and end of upload
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
                structActions.remove(i);
                break;
            }
        }
    }
}
