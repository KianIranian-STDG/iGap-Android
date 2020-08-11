/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.helper;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.iGap.helper.HelperConvertEnumToString.convertActionEnum;

public class HelperGetAction {

    private static CopyOnWriteArrayList<StructAction> structActions = new CopyOnWriteArrayList<>();
    // this array contains all of NOT canceled user and their rooms
    private static CopyOnWriteArrayList<StructAction> activeActions = new CopyOnWriteArrayList<>();
    // this var indicates that function for checking the user action is running.
    private static Boolean handler = false;

    public static String getAction(long roomId, long userID, ProtoGlobal.Room.Type type, ProtoGlobal.ClientAction clientAction) {
        if (!checkExistAction(roomId, userID, clientAction) && type != null && type != ProtoGlobal.Room.Type.CHANNEL) {
            // in here we add user and its room id to the list and starts the observer.
            final StructAction structAction = new StructAction();
            structAction.roomId = roomId;
            structAction.userId = userID;
            structAction.currentTime = System.currentTimeMillis();
            structAction.action = ProtoGlobal.ClientAction.TYPING;

            activeActions.add(structAction);
//            timeOutChecking(structAction);
            checkTimeOut(false);
        }
        if (type == ProtoGlobal.Room.Type.CHAT) {
            String action = convertActionEnum(clientAction);
            return action;
        } else if (type == ProtoGlobal.Room.Type.GROUP) {
            final String actionText = HelperGetAction.getMultipleAction(roomId);
            return actionText;
        }
        return null;
    }

    /**
     * search structActions list for this roomId and return correct string for show in toolbar
     *
     * @param roomId current roomId
     * @return text for show in toolbar
     */

    private static String getMultipleAction(long roomId) {

        ProtoGlobal.ClientAction latestAction = getLatestAction(roomId);
        if (latestAction == null) {
            return null;
        } else {
            return DbManager.getInstance().doRealmTask(realm -> {
                int count = 0;
                StructAction latestStruct = null;
                Iterator<StructAction> iterator1 = structActions.iterator();
                ArrayList<Long> userIds = new ArrayList<>();
                while (iterator1.hasNext()) {
                    StructAction struct = iterator1.next();
                    if (struct.roomId == roomId && struct.action == latestAction) {
                        if (!userIds.contains(struct.userId)) {
                            userIds.add(struct.userId);
                            latestStruct = struct;
                            count++;
                        }
                    }
                }
                if (count == 1) {
                    RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, latestStruct.userId);
                    if (realmRegisteredInfo != null && realmRegisteredInfo.getDisplayName().length() > 0) {
                        String action;

                        if (HelperCalander.isPersianUnicode) {
                            action = "\u200F" + realmRegisteredInfo.getDisplayName() + " " + convertActionEnum(latestStruct.action);
                        } else {
                            action = "\u200E" + realmRegisteredInfo.getDisplayName() + " " + G.fragmentActivity.getResources().getString(R.string.is) + " " + convertActionEnum(latestStruct.action);
                        }
                        return action;
                    } else {
                        return null;
                    }

                } else if (count < Config.GROUP_SHOW_ACTIONS_COUNT) {

                    StringBuilder concatenatedNames = new StringBuilder();

                    Iterator<StructAction> iterator = structActions.iterator();
                    while (iterator.hasNext()) {
                        StructAction struct = iterator.next();
                        if (struct.roomId == roomId && struct.action == latestAction) {
                            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, struct.userId);
                            if (realmRegisteredInfo != null) {
                                concatenatedNames.append(realmRegisteredInfo.getDisplayName()).append(" , ");
                            }
                        }
                    }

                    if ((concatenatedNames.length() == 0) || concatenatedNames.length() == 0) {
                        return null;
                    }
                    concatenatedNames = new StringBuilder(concatenatedNames.substring(0, concatenatedNames.length() - 1));

                    if (HelperCalander.isPersianUnicode) {

                        return "\u200F" + concatenatedNames + " " + HelperConvertEnumToString.convertActionEnum(latestAction);

                    } else {
                        return concatenatedNames + " " + G.fragmentActivity.getResources().getString(R.string.are) + " " + convertActionEnum(latestAction);
                    }
                } else {
                    if (HelperCalander.isPersianUnicode) {

                        return "\u200F" + count + " " + G.fragmentActivity.getResources().getString(R.string.members_are) + " " + convertActionEnum(latestAction);
                    } else {

                        return count + " " + G.fragmentActivity.getResources().getString(R.string.members_are) + " " + convertActionEnum(latestAction);
                    }
                }
            });

        }
    }

    /**
     * get latest action that do in this room
     *
     * @param roomId current roomId
     * @return latest Action
     */

    private static ProtoGlobal.ClientAction getLatestAction(long roomId) {
        for (int i = (structActions.size() - 1); i >= 0; i--) {
            if (structActions.get(i).roomId == roomId) {
                return structActions.get(i).action;
            }
        }
        return null;
    }

    /**
     * after get GroupSetActionResponse fill or clear structActions list
     *
     * @param roomId roomId that setAction in
     * @param userId userId that setAction in
     * @param action action that doing
     */

    public static void fillOrClearAction(long roomId, long userId, ProtoGlobal.ClientAction action) {
        if (action == ProtoGlobal.ClientAction.CANCEL) {
            for (int i = 0; i < structActions.size(); i++) {
                if (structActions.get(i).roomId == roomId && structActions.get(i).userId == userId) {
                    structActions.remove(i);
                }
            }
        } else {

            if (structActions.size() > 0) {
                boolean checkItemExist = false;
                for (StructAction structCheck : structActions) {
                    if (structCheck.roomId == roomId & structCheck.userId == userId & structCheck.action.toString().equals(action.toString())) {
                        checkItemExist = true;
                        break;
                    }
                }
                if (!checkItemExist) {
                    StructAction struct = new StructAction();
                    struct.roomId = roomId;
                    struct.userId = userId;
                    struct.action = action;
                    structActions.add(struct);
                }
            } else {
                StructAction struct = new StructAction();
                struct.roomId = roomId;
                struct.userId = userId;
                struct.action = action;
                structActions.add(struct);
            }
        }
    }

    private static class StructAction {
        public long roomId;
        public long userId;
        public long currentTime;
        public ProtoGlobal.ClientAction action;
    }

    /**
     * check that action with same roomId is exist or not.
     * return true if exist and update time otherwise just return false. and if the status is cancel so we will delete it from out lists.
     *
     * @param roomId roomId that send action from that
     * @param action action that send
     */
    private static boolean checkExistAction(long roomId, long userID, ProtoGlobal.ClientAction action) {
        for (StructAction struct : activeActions) {
            if (struct.roomId == roomId && struct.userId == userID) {
                if (action == ProtoGlobal.ClientAction.CANCEL) {
                    removeStructWithRoomID(roomId, userID);
                    fillOrClearAction(roomId, userID, ProtoGlobal.ClientAction.CANCEL);
                    if (G.onSetAction != null)
                        G.onSetAction.onSetAction(roomId, struct.userId, ProtoGlobal.ClientAction.CANCEL);
                    if (G.onSetActionInRoom != null) {
                        G.onSetActionInRoom.onSetAction(struct.roomId, struct.userId, ProtoGlobal.ClientAction.CANCEL);
                    }
                } else {
                    struct.currentTime = System.currentTimeMillis();
                }
                return true;
            }
        }
        if (action == ProtoGlobal.ClientAction.CANCEL) {
            return true;
        }
        return false;
    }

    /**
     * this function will repeat itself until there is no item in the list.
     *
     * @param isCallInside is it send inside the observer or not
     */

    private static void checkTimeOut(boolean isCallInside) {
        if (handler != isCallInside)
            return;
        else
            handler = true;
        LooperThreadHelper.getInstance().getHandler().postDelayed(
                () -> {
                    for (StructAction struct : activeActions) {
                        if (autoCancel(struct.currentTime)) {
                            if (G.onSetAction != null) {
                                G.onSetAction.onSetAction(struct.roomId, struct.userId, ProtoGlobal.ClientAction.CANCEL);
                            }
                            if (G.onSetActionInRoom != null) {
                                G.onSetActionInRoom.onSetAction(struct.roomId, struct.userId, ProtoGlobal.ClientAction.CANCEL);
                            }
                        }
                    }
                    if (activeActions.size() > 0)
                        checkTimeOut(true);
                    else
                        handler = false;
                }, Config.ACTION_EXPIRE_LOOP);
    }

    /**
     * check difference time from latest set action .
     * if time difference was more than Config.ACTION_TIME_OUT second
     * return true for send cancel request for that action
     *
     * @param startActionTime currentTimeMillis for start action or update action(when repeat it)
     */

    private static boolean autoCancel(long startActionTime) {

        long difference;

        long currentTime = System.currentTimeMillis();
        difference = (currentTime - startActionTime);

        return difference >= Config.CLIENT_ACTION_TIME_OUT;
    }


    /**
     * remove item from structActions array with roodID.
     *
     * @param roomID randomKey that should remove struct with that
     */
    private synchronized static void removeStructWithRoomID(long roomID, long userID) {
        for (int i = 0; i < activeActions.size(); i++) {
            if (activeActions.get(i).roomId == roomID && activeActions.get(i).userId == userID) {
                activeActions.remove(i);
                break;
            }
        }
    }
}
