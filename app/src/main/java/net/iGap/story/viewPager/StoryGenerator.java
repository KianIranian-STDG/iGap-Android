package net.iGap.story.viewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StoryGenerator {

    public List<StoryUser> generateStories() {
        List<StoryUser> storyUserList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            StoryUser storyUser = new StoryUser();
            switch (i) {
                case 0:
                    List<Story> stories = new ArrayList<>();
                    storyUser.setUserName("Contact 1");
                    storyUser.setProfilePicUrl("https://randomuser.me/api/portraits/women/1.jpg");
                    for (int j = 0; j < 3; j++) {
                        Story story = new Story();
                        switch (j) {
                            case 0:
                                story.setUrl("https://www.koko.org/wp-content/uploads/2019/08/koko_smoky_hat1_T-phone.jpg");
                                break;
                            case 1:
                                story.setUrl("https://www.koko.org/wp-content/uploads/2019/08/koko_smoky_hat1_T-phone.jpg");
                                break;
                            case 2:
                                story.setUrl("https://www.koko.org/wp-content/uploads/2019/08/koko_smoky_hat1_T-phone.jpg");
                                break;
                        }
                        story.setStoryData(System.currentTimeMillis() - (1 * (24 - j) * 60 * 60 * 1000));
                        stories.add(story);

                    }
                    storyUser.setStories(stories);
                    break;
                case 1:
                    List<Story> stories1 = new ArrayList<>();
                    storyUser.setUserName("Contact 2");
                    storyUser.setProfilePicUrl("https://randomuser.me/api/portraits/women/1.jpg");
                    for (int j = 0; j < 2; j++) {
                        Story story1 = new Story();
                        switch (j) {
                            case 0:
                                story1.setUrl("https://image.freepik.com/free-vector/shining-bokeh-overlay-background_1409-778.jpg");
                                break;
                            case 1:
                                story1.setUrl("https://image.freepik.com/free-vector/shining-bokeh-overlay-background_1409-778.jpg");
                                break;
                        }
                        story1.setStoryData(System.currentTimeMillis() - (1 * (24 - j) * 60 * 60 * 1000));
                        stories1.add(story1);
                    }

                    storyUser.setStories(stories1);
                    break;
                case 2:
                    List<Story> stories2 = new ArrayList<>();
                    storyUser.setUserName("Contact 3");
                    storyUser.setProfilePicUrl("https://randomuser.me/api/portraits/women/1.jpg");
                    for (int j = 0; j < 2; j++) {
                        Story story2 = new Story();
                        switch (j) {
                            case 0:
                                story2.setUrl("https://image.freepik.com/free-vector/shining-bokeh-overlay-background_1409-778.jpg");
                                break;
                            case 1:
                                story2.setUrl("https://image.freepik.com/free-vector/shining-bokeh-overlay-background_1409-778.jpg");
                                break;
                        }
                        story2.setStoryData(System.currentTimeMillis() - (1 * (24 - j) * 60 * 60 * 1000));
                        stories2.add(story2);
                    }

                    storyUser.setStories(stories2);
                    break;
                case 3:
                    List<Story> stories3 = new ArrayList<>();
                    storyUser.setUserName("Contact 3");
                    storyUser.setProfilePicUrl("https://randomuser.me/api/portraits/women/1.jpg");
                    for (int j = 0; j < 3; j++) {
                        Story story3 = new Story();
                        switch (j) {
                            case 0:
                                story3.setUrl("https://www.koko.org/wp-content/uploads/2019/08/koko_smoky_hat1_T-phone.jpg");
                                break;
                            case 1:
                                story3.setUrl("https://www.koko.org/wp-content/uploads/2019/08/koko_smoky_hat1_T-phone.jpg");
                                break;
                            case 2:
                                story3.setUrl("https://www.koko.org/wp-content/uploads/2019/08/koko_smoky_hat1_T-phone.jpg");
                                break;
                        }
                        story3.setStoryData(System.currentTimeMillis() - (1 * (24 - j) * 60 * 60 * 1000));
                        stories3.add(story3);
                    }

                    storyUser.setStories(stories3);
                    break;
            }
            storyUserList.add(storyUser);
        }
        return storyUserList;
    }
}
