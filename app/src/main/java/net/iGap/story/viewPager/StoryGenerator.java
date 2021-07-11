package net.iGap.story.viewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StoryGenerator {

    public List<StoryUser> generateStories() {

        List<String> storyUrls = new ArrayList<>();
        storyUrls.add("https://image.freepik.com/free-vector/shining-bokeh-overlay-background_1409-778.jpg");
        storyUrls.add("https://www.koko.org/wp-content/uploads/2019/08/koko_smoky_hat1_T-phone.jpg");

        List<String> userProfileUrls = new ArrayList<>();
        userProfileUrls.add("https://randomuser.me/api/portraits/women/1.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/men/1.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/women/2.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/men/2.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/women/3.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/men/3.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/women/4.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/men/4.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/women/5.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/men/5.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/women/6.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/men/6.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/women/7.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/men/7.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/women/8.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/men/8.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/women/9.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/men/9.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/women/10.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/men/10.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/women/11.jpg");
        userProfileUrls.add("https://randomuser.me/api/portraits/men/11.jpg");

        List<StoryUser> storyUserList = new ArrayList<>();
        Random random = new java.util.Random();

        for (int i = 0; i < 10; i++) {
            List<Story> stories = new ArrayList<>();
            int storySize = random.nextInt(5);
            for (int j = 0; j < storySize; j++) {
                Story story = new Story();
                story.setUrl(storyUrls.get(random.nextInt(storyUrls.size())));
                story.setStoryData(System.currentTimeMillis() - (1 * (24 - j) * 60 * 60 * 1000));
                stories.add(story);
            }

            StoryUser storyUser = new StoryUser();
            storyUser.setProfilePicUrl(userProfileUrls.get(random.nextInt(userProfileUrls.size())));
            storyUser.setUserName("username" + " " + i);
            storyUser.setStories(stories);
            storyUserList.add(storyUser);
        }

        return storyUserList;
    }
}
