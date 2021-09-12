package net.iGap.story;

import net.iGap.realm.RealmStory;

import java.util.ArrayList;
import java.util.List;

public class MainStoryObject {

    public long id;
    public long uid; // id for sorting avatars
    public long userId; // userId for users and roomId for rooms
    public boolean isSeenAll;
    public boolean isSentAll;
    public boolean isUploadedAll;
    public int indexOfSeen;
    public List<StoryObject> storyObjects = new ArrayList<>();

    public MainStoryObject() {
    }

    public MainStoryObject(long id, long uid, long userId, boolean isSeenAll, boolean isSentAll, boolean isUploadedAll, int indexOfSeen, List<StoryObject> storyObjects) {
        this.id = id;
        this.uid = uid;
        this.userId = userId;
        this.isSeenAll = isSeenAll;
        this.isSentAll = isSentAll;
        this.isUploadedAll = isUploadedAll;
        this.indexOfSeen = indexOfSeen;
        this.storyObjects = storyObjects;
    }

    public static MainStoryObject create(RealmStory realmStory) {
        MainStoryObject mainStoryObject = new MainStoryObject();

        mainStoryObject.id = realmStory.getId();
        mainStoryObject.uid = realmStory.getUid();
        mainStoryObject.userId = realmStory.getUserId();
        mainStoryObject.isSeenAll = realmStory.isSeenAll();
        mainStoryObject.isSentAll = realmStory.isSentAll();
        mainStoryObject.isUploadedAll = realmStory.isUploadedAll();
        mainStoryObject.indexOfSeen = realmStory.getIndexOfSeen();

        for (int i = 0; i < realmStory.getRealmStoryProtos().size(); i++) {
            mainStoryObject.storyObjects.add(StoryObject.create(realmStory.getRealmStoryProtos().get(i)));
        }
        return mainStoryObject;
    }
}
