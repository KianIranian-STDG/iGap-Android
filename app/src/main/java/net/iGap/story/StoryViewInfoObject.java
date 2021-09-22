package net.iGap.story;

import net.iGap.realm.RealmStoryViewInfo;

import java.util.ArrayList;
import java.util.List;

public class StoryViewInfoObject {

    public long id;
    public long userId;
    public long createdTime;
    public String displayName;


    public static StoryViewInfoObject create(RealmStoryViewInfo realmStoryViewInfo) {
        StoryViewInfoObject storyViewInfoObject = new StoryViewInfoObject();


        storyViewInfoObject.createdTime = realmStoryViewInfo.getCreatedTime();
        storyViewInfoObject.id = realmStoryViewInfo.getId();
        storyViewInfoObject.userId = realmStoryViewInfo.getUserId();
        storyViewInfoObject.displayName=realmStoryViewInfo.getDisplayName();




        return storyViewInfoObject;
    }


}
