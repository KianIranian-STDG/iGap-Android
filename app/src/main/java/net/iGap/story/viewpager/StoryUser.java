package net.iGap.story.viewpager;


import java.io.Serializable;
import java.util.List;

public class StoryUser implements Serializable {

    private String userName;
    private String profilePicUrl;
    private List<Story> stories;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }
}
