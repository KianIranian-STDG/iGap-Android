package com.iGap.helper;

import android.util.Log;
import com.iGap.Config;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import io.realm.Realm;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class HelperGetAction {

    private static CopyOnWriteArrayList<StructAction> structActions = new CopyOnWriteArrayList<>();

    private static class StructAction {
        public long roomId;
        public long userId;
        public ProtoGlobal.ClientAction action;
    }

    public static String getAction(long roomId, ProtoGlobal.Room.Type type, ProtoGlobal.ClientAction clientAction) {
        if (type == ProtoGlobal.Room.Type.CHAT) {
            String action = HelperConvertEnumToString.convertActionEnum(clientAction);
            if (action != null) {
                return action;
            }
            return null;

        } else if (type == ProtoGlobal.Room.Type.GROUP) {

            final String actionText = HelperGetAction.getMultipleAction(roomId);
            if (actionText != null) {
                return actionText;
            }
            return null;
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
            int count = 0;
            StructAction latestStruct = null;
            Iterator<StructAction> iterator1 = structActions.iterator();
            while (iterator1.hasNext()) {
                StructAction struct = iterator1.next();
                if (struct.roomId == roomId && struct.action == latestAction) {
                    latestStruct = struct;
                    count++;
                }
            }
            if (count == 1) {

                Realm realm = Realm.getDefaultInstance();

                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, latestStruct.userId).findFirst();
                if (realmRegisteredInfo != null) {

                    String action = realmRegisteredInfo.getDisplayName() + " is " + HelperConvertEnumToString.convertActionEnum(latestStruct.action);
                    realm.close();

                    return action;
                } else {
                    realm.close();
                    return null;
                }
            } else if (count < Config.GROUP_SHOW_ACTIONS_COUNT) {

                String concatenatedNames = "";

                Realm realm = Realm.getDefaultInstance();

                //for (StructAction struct : structActions) {
                Iterator<StructAction> iterator = structActions.iterator();
                while (iterator.hasNext()) {
                    StructAction struct = iterator.next();
                    if (struct.action == latestAction) {
                        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, struct.userId).findFirst();
                        if (realmRegisteredInfo != null) {
                            concatenatedNames += realmRegisteredInfo.getDisplayName() + ",";
                        }
                    }
                }

                realm.close();

                if (concatenatedNames.isEmpty() || concatenatedNames.length() == 0) {
                    return null;
                }
                concatenatedNames = concatenatedNames.substring(0, concatenatedNames.length() - 1);

                return concatenatedNames + " are " + HelperConvertEnumToString.convertActionEnum(latestAction);
            } else {
                return count + " members are " + HelperConvertEnumToString.convertActionEnum(latestAction);
            }

        }
    }

    /**
     * get latest action that do in this room
     *
     * @param roomId current roomId
     * @return latest Action
     */

    private static ProtoGlobal.ClientAction getLatestAction(long roomId) {
        //use this commented code
        /*Iterator<StructAction> iterator = structActions.iterator();
        while (iterator.hasNext()) {
            StructAction struct = iterator.next();*/
        for (int i = (structActions.size() - 1); i >= 0; i--) {
            if (structActions.get(i).roomId == roomId) {
                Log.i("VVV", "LatestAction : " + structActions.get(i).action);
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
        //use this commented code
       /* Iterator<StructAction> iterator = structActions.iterator();
        while (iterator.hasNext()) {
            StructAction struct = iterator.next();*/
        if (action == ProtoGlobal.ClientAction.CANCEL) {
            Log.i("VVV", "ClientAction.CANCEL");
            for (int i = 0; i < structActions.size(); i++) {
                if (structActions.get(i).roomId == roomId && structActions.get(i).userId == userId) {
                    Log.i("VVV", "CLEAR");
                    structActions.remove(i);
                }
            }

        } else {

            Log.i("VVV", "ADD");
            StructAction struct = new StructAction();
            struct.roomId = roomId;
            struct.userId = userId;
            struct.action = action;
            if (structActions.size() > 0) {
                for (StructAction structCheck : structActions) {
                    boolean checkItemExist = false;
                    if (structCheck.roomId == roomId & structCheck.userId == userId & structCheck.action.toString().equals(action.toString())) {
                        Log.i("VVV", "CONTAINS");
                        checkItemExist = true;
                    }
                    if (!checkItemExist) {
                        structActions.add(struct);
                        Log.i("VVV", "NEW ITEM");
                    }

                }
            } else {
                structActions.add(struct);
                Log.i("VVV", "NEW ITEM ZERO");
            }

        }
        Log.i("VVV", "***");
        Log.i("VVV", "**********");
        Log.i("VVV", "***");

    }

}
