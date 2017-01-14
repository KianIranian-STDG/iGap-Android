package com.iGap.helper;

import com.iGap.G;
import com.iGap.realm.RealmRoomMessage;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;


public class HelperCalculateKeepMedia {
    private RealmResults<RealmRoomMessage> mRealmList;

    public void calculateTime() { // calculate time for delete media in after 7 days

        Realm realm = Realm.getDefaultInstance();
        mRealmList = realm.where(RealmRoomMessage.class).findAll();
        for (int i = 0; i < mRealmList.size(); i++) {

            if (mRealmList.get(i).getAttachment() != null) {
                long timeMedia = mRealmList.get(i).getUpdateTime() / 1000;
                long currentTime = G.currentTime;
                long oneWeeks = (7 * 24 * 60 * 60 * 1000);
                long b = currentTime - timeMedia;
                if ((b) >= oneWeeks) {

                    String filePath = mRealmList.get(i).getAttachment().getLocalFilePath();
                    if (filePath != null) {
                        new File(filePath).delete();
                    }

                    String filePathThumbnail = mRealmList.get(i).getAttachment().getLocalThumbnailPath();
                    if (filePathThumbnail != null) {
                        new File(filePathThumbnail).delete();
                    }
                }
            }
        }
    }
}
