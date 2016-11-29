package com.iGap.helper;

import android.util.Log;

import com.iGap.Config;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;

import java.util.ArrayList;

import io.realm.Realm;

public class HelperGetAction {

    private static ArrayList<HelperGetAction.StructAction> structActions = new ArrayList<>();

    private static class StructAction {
        public long roomId;
        public long userId;
        public ProtoGlobal.ClientAction action;
    }

    /**
     * search structActions list for this roomId and return correct string for show in toolbar
     *
     * @param roomId current roomId
     * @return text for show in toolbar
     */

    public static String getAction(long roomId) {

        ProtoGlobal.ClientAction latestAction = getLatestAction(roomId);
        if (latestAction == null) {
            return null;
        } else {
            int count = 0;
            StructAction latestStruct = null;
            for (StructAction struct : structActions) {
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

                for (StructAction struct : structActions) {
                    if (struct.action == latestAction) {
                        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, struct.userId).findFirst();
                        if (realmRegisteredInfo != null) {
                            concatenatedNames += realmRegisteredInfo.getDisplayName() + ",";
                        }
                    }
                }

                realm.close();

                Log.i("QQQ", "concatenatedNames 1 : " + concatenatedNames);
                concatenatedNames = concatenatedNames.substring(0, concatenatedNames.length() - 1);
                Log.i("QQQ", "concatenatedNames 2 : " + concatenatedNames);

                if (concatenatedNames.isEmpty()) {
                    return null;
                }
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
            StructAction struct = new StructAction();
            struct.roomId = roomId;
            struct.userId = userId;
            struct.action = action;
            structActions.add(struct);
        }

    }

}
